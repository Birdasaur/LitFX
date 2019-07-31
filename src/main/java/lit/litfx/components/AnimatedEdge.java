
package lit.litfx.components;

import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
public abstract class AnimatedEdge extends Polyline {
    public EdgePoint start;
    public EdgePoint end;
    public double density;
    public SimpleIntegerProperty pointIndexProperty = new SimpleIntegerProperty(0);
    private ArrayList<EdgePoint> edgePoints;
   
    public abstract void animate(Duration milliseconds);
    public abstract void updateLength(int length);
    
    public AnimatedEdge(EdgePoint start, EdgePoint end, double density) {
        super();
        this.start = start;
        this.end = end;
        this.density = density;
        edgePoints = new ArrayList<>();
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
