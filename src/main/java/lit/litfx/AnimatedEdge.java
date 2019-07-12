
package lit.litfx;

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
