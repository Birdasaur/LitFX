package lit.litfx;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

/**
 *
 * @author phillsm1
 */
public class BoltPoint extends Point3D {
    
    public int index;
    
    public BoltPoint(int index, Point2D point) {
        this(index, point.getX(), point.getY(), 0.0 );
    }
    public BoltPoint(int index, double x, double y, double z) {
        super(x, y, z);
        this.index = index;
    }
}
