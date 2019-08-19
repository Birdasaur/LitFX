
package lit.litfx.core.components;

import java.util.ArrayList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Polyline;

/**
 *
 * @author phillsm1
 */
public abstract class AnimatedEdge extends Polyline implements AnimatedEffect{
    public EdgePoint start;
    public EdgePoint end;
    public double density;
    public SimpleIntegerProperty pointIndexProperty = new SimpleIntegerProperty(0);
    private ArrayList<EdgePoint> edgePoints;
    SimpleBooleanProperty animating = new SimpleBooleanProperty(false);
    
    public AnimatedEdge(EdgePoint start, EdgePoint end, double density) {
        super();
        this.start = start;
        this.end = end;
        this.density = density;
        edgePoints = new ArrayList<>();
    }

    public void updatePolylinePoints(int length) {
        Double [] points = new Double [length * 2];
        for(int i=0;i<length;i++) {
            points[i*2] = getEdgePoints().get(i).getX();
            points[i*2+1] = getEdgePoints().get(i).getY();
        }   
        this.getPoints().setAll(points); 
    }
    
    public double[] getXpointArray() {
        double [] xPointArray = new double[edgePoints.size()];
        for(int i=0; i<xPointArray.length;i++)
            xPointArray[i] = edgePoints.get(i).getX();
        return xPointArray;
    }
    public double[] getYpointArray() {
        double [] yPointArray = new double[edgePoints.size()];
        for(int i=0; i<yPointArray.length;i++)
            yPointArray[i] = edgePoints.get(i).getY();
        return yPointArray;
    }
    
    /**
     * @return the edgePoints
     */
    public ArrayList<EdgePoint> getEdgePoints() {
        return edgePoints;
    }

    /**
     * @param edgePoints the edgePoints to set
     */
    public void setEdgePoints(ArrayList<EdgePoint> edgePoints) {
        this.edgePoints = edgePoints;
        updateLength(edgePoints.size());
    }     
    public void setVisibleLength(int length) {
        pointIndexProperty.set(length);
    }
    public int getVisibleLength() {
        return pointIndexProperty.get();
    }
}
