package lit.litfx.core.components;

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
    public EdgePoint(int index, Point3D point) {
        this(index, point.getX(), point.getY(), point.getZ());
    }
    public EdgePoint(int index, double x, double y, double z) {
        super(x, y, z);
        this.index = index;
    }
    public Point2D toPoint2D() {
        return new Point2D(getX(), getY());
    }
}
