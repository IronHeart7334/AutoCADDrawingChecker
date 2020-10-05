package autocadDrawingChecker.grading;

import autocadDrawingChecker.grading.criteria.AbstractGradingCriteria;
import autocadDrawingChecker.data.AutoCADExport;
import autocadDrawingChecker.grading.criteria.implementations.CompareColumn;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * The GradedExport class
 handles grading a single student
 file to the instructor file.
 * <b>Note that this comparison may 
 * not be commutative:</b> 
 * <pre>GradedExport(x, y) != GradedExport(y, x)</pre>
 * 
 * @author Matt Crow.
 */
public class GradedExport {
    private final AutoCADExport instructorExport;
    private final AutoCADExport studentExport;
    private final List<AbstractGradingCriteria> gradedCriteria;
    private final HashMap<AbstractGradingCriteria, Double> grades;
    private final double finalGrade;
    
    public GradedExport(AutoCADExport instructorsExport, AutoCADExport studentsExport, List<AbstractGradingCriteria> gradeOnThese){
        instructorExport = instructorsExport;
        studentExport = studentsExport;
        gradedCriteria = gradeOnThese;
        grades = new HashMap<>();
        finalGrade = runComparison();
    }
    
    private double runComparison(){
        double similarityScore = 0.0;
        double newScore = 0.0;
        for(AbstractGradingCriteria attr : gradedCriteria){
            newScore = attr.computeScore(instructorExport, studentExport);
            grades.put(attr, newScore);
            similarityScore += newScore;
        }
        
        
        
        // new stuff
        CompareColumn newCrit = null;
        Set<String> cols = instructorExport.getColumns();
        for(String column : cols){
            newCrit = new CompareColumn(column);
            newScore = newCrit.computeScore(instructorExport, studentExport);
            grades.put(newCrit, newScore);
            similarityScore += newScore;
        }
        
        
        
        
        return similarityScore / (gradedCriteria.size() + cols.size()); // average similarity score
    }
    
    public final AutoCADExport getInstructorFile(){
        return instructorExport;
    }
    
    public final AutoCADExport getStudentFile(){
        return studentExport;
    }
    
    public final double getFinalGrade(){
        return finalGrade;
    }
    
    public final double getGradeFor(AbstractGradingCriteria criteria){
        return grades.get(criteria);
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Comparing %s to \n%s:", instructorExport.getFileName(), studentExport.getFileName()));
        grades.forEach((attr, score)->{
            sb.append(String.format("\n* %s: %d%%", attr.getName(), (int)(score * 100)));
        });
        sb.append(String.format("\nFinal Grade: %d%%", (int)(finalGrade * 100)));
        return sb.toString();
    }
}
