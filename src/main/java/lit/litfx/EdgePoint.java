package lit.litfx;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

/**
 *
 * @author phillsm1
 */
public class EdgePoint extends Point3D {
    
    public int index;
    
    public EdgePoint(int index, Point2D point) {
        this(index, point.getX(), point.getY(), 0.0 );
    }
    public EdgePoint(int index, double x, double y, double z) {
        super(x, y, z);
        this.index = index;
    }
}
