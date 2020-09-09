package autocadDrawingChecker.autocadData;

import java.util.Arrays;

/**
 * The AutoCADLine class is used to differentiate 
 * between geometric lines and other AutoCAD elements.
 * 
 * I'm not quite sure how AutoCAD works, so I
 * don't know if I'll need different classes
 * for each element type.
 * 
 * @author Matt Crow
 */
public class AutoCADLine extends AutoCADRow {
    private final double[] r0; // r naught
    private final double[] r; // r final
    
    private static final int DIMENSION_COUNT = 3;
    
    public AutoCADLine(String layerName, double[] start, double[] end) {
        super(layerName);
        r0 = Arrays.copyOf(start, DIMENSION_COUNT);
        r = Arrays.copyOf(end, DIMENSION_COUNT);
    }

    /**
     * 
     * @return the physical length of this line. 
     * <b>Not to be confused with its 6-dimensional length</b>
     */
    public final double getLength(){
        double inRoot = 0.0;
        for(int i = 0; i < DIMENSION_COUNT; i++){
            inRoot += Math.pow(r[i] - r0[i], 2);
        }
        return Math.sqrt(inRoot);
    }
    
    public final double getNorm(){
        double norm = 0.0;
        for(int i = 0; i < DIMENSION_COUNT; i++){
            norm += Math.pow(r[i], 2);
            norm += Math.pow(r0[i], 2);
        }
        return Math.sqrt(norm);
    }
    
    /**
     * Computes the 6-dimensional dot product between
     * two AutoCADLines.
     * 
     * Merely computing the regular dot product, where both vectors are centered at 0,
     * is insufficient, as the coordinates matter here.
     * 
     * @param other the other AutoCADLine to dot-product with
     * @return this dot other.
     */
    public final double dot(AutoCADLine other){
        double dotProduct = 0.0;
        for(int i = 0; i < DIMENSION_COUNT; i++){
            dotProduct += r[i] * other.r[i]; // end point for this dimension...
            dotProduct += r0[i] * other.r0[i]; // ... start point for this dimension
        }
        return dotProduct;
    }
    
    /**
     * 
     * @param other
     * @return the absolute value of the inverse 
     * cosine of the angle between this and the other 
     * line when interpreted as 6-dimensional vectors.
     * <b>Note that this value ranges from 0.0 to 1.0,
     * so it can serve as a similarity score</b>
     */
    public final double normDot(AutoCADLine other){
        return Math.abs(dot(other) / (getNorm() * other.getNorm()));
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("AutoCAD Line:");
        sb.append("\n").append(super.toString());
        sb.append(String.format("\n\t* Line from (%f, %f, %f) to (%f, %f, %f)", r0[0], r0[1], r0[2], r[0], r[1], r[2]));
        return sb.toString();
    }
}
