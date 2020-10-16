package autocadDrawingChecker.data.core;

import autocadDrawingChecker.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Matt
 */
public class ExcelParser {
    private final String fileName;
    private Row currRow;
    
    public ExcelParser(String fileName){
        this.fileName = fileName;
        this.currRow = null;
    }
    
    protected final String getFileName(){
        return fileName;
    }
    
    protected synchronized Row locateHeaderRow(Sheet sheet){
        return sheet.getRow(0);
    }
    
    /**
     * Locates headers within the given
     * row, and populates headerToCol appropriately.
     * 
     * @param headerRow the first row of the spreadsheet,
     * containing headers.
     */
    private synchronized HashMap<String, Integer> locateColumns(Row headerRow){
        HashMap<String, Integer> headerToCol = new HashMap<>();
        
        ArrayList<String> headers = new ArrayList<>();
        headerRow.cellIterator().forEachRemaining((Cell c)->{
            headers.add(c.toString().toUpperCase());
        });
        for(int i = 0; i < headers.size(); i++){
            headerToCol.put(headers.get(i), i);
        }
        return headerToCol;
    }
    
    private boolean isRowEmpty(Row row){
        boolean couldBeEmpty = true;
        Iterator<Cell> cellIter = row.cellIterator();
        while(cellIter.hasNext() && couldBeEmpty){
            if(!cellIter.next().getCellType().equals(CellType.BLANK)){
                couldBeEmpty = false;
            }
        }
        return couldBeEmpty;
    }
    protected boolean isValidRow(Row row){
        return row != null && row.getLastCellNum() != -1 && !isRowEmpty(row);
    }
    
    private String currRowToString(){
        StringBuilder b = new StringBuilder();
        b.append("[");
        Iterator<Cell> iter = currRow.cellIterator();
        while(iter.hasNext()){
            b.append(iter.next().toString());
            if(iter.hasNext()){
                b.append(", ");
            }
        }
        b.append("]");
        return b.toString();
    }
    
    protected DataSet createExtractionHolder(){
        return new DataSet(this.fileName);
    }
    
    protected RecordExtractor createExtractor(HashMap<String, Integer> columns){
        return new RecordExtractor(columns);
    }
    
    protected int locateLastRow(Sheet sheet){
        return sheet.getLastRowNum() + 1; // need the + 1, otherwise it sometimes doesn't get the last row
    }
    
    public final DataSet parse() throws IOException {
        InputStream in = new FileInputStream(fileName);
        //                                                new Excel format       old Excel format
        Workbook workbook = (fileName.endsWith("xlsx")) ? new XSSFWorkbook(in) : new HSSFWorkbook(in);
        Sheet sheet = workbook.getSheetAt(0);
        
        DataSet containedTherein = createExtractionHolder();
        
        Row headerRow = locateHeaderRow(sheet);
        
        
        RecordExtractor recExtr = createExtractor(locateColumns(headerRow));
        
        int numRows = locateLastRow(sheet);
        /*
        Note that numRows will be greater than or
        equal to the last row with data, but not
        every row is guaranteed to contain data.
        
        From the Apache POI javadoc:
        """
        Gets the last row on the sheet 
        Note: rows which had content before and were set to empty later might still be counted as rows by Excel and Apache POI, 
        so the result of this method will include such rows and thus the returned value might be higher than expected!
        """
        */
        
        Record rec = null;
        
        //               skip headers
        for(int rowNum = headerRow.getRowNum() + 1; rowNum < numRows; rowNum++){
            currRow = sheet.getRow(rowNum);
            if(isValidRow(currRow) && recExtr.canExtractRow(currRow)){
                try {
                    rec = recExtr.extract(currRow);
                    containedTherein.add(rec);
                } catch(Exception ex){
                    Logger.logError(String.format("Error while parsing row: %s", currRowToString()));
                    Logger.logError(ex);
                }
            }
        }
        
        workbook.close();
        
        return containedTherein;
    }
}
