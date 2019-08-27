package lit.litfx.core;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;
import lit.litfx.core.components.EdgePoint;

/**
 *
 * @author Birdasaur
 */
public enum Algorithms {
    INSTANCE;
    public static ArrayList<Point2D> simpleBres2D(int x1, int y1, int x2, int y2) {
        ArrayList<Point2D> pointList = new ArrayList<>();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            pointList.add(new Point2D(x1, y1));
            if (x1 == x2 && y1 == y2) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
        return pointList;
    }
    
    public static Polyline toPolyLine(ArrayList<Point2D> point2DList) {
        double [] points = new double [point2DList.size() * 2];
        for(int i=0;i<point2DList.size();i++) {
            points[i*2] = point2DList.get(i).getX();
            points[i*2+1] = point2DList.get(i).getY();
        }
        return new Polyline(points);        
    }
    
    public static double divergenceAngle(Point2D startPoint, Point2D endPoint, double deltaAngle) {
            //Calculate the actual angle of the line with regard to the screen
            //Screen coordinates is Y positive down, 0,0 upper left corner
            double baseAngle = Math.toDegrees(Math.atan2(
                endPoint.getY()-startPoint.getY(), 
                endPoint.getX()-startPoint.getX()));
            //add a angle to diverge from the base 
            return Math.toRadians(baseAngle + deltaAngle);
    }
	// Inspired by examples at https://gist.github.com/Roland09 
	// http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
        //Does not currently support the Z axis
	private EdgePoint getLineIntersection(EdgePoint startPoint1, EdgePoint endPoint1, 
                EdgePoint startPoint2, EdgePoint endPoint2) {
		double x1 = startPoint1.getX();
		double y1 = startPoint1.getY();
		double x2 = endPoint1.getX();
		double y2 = endPoint1.getY();

		double x3 = startPoint2.getX();
		double y3 = startPoint2.getY();
		double x4 = endPoint2.getX();
		double y4 = endPoint2.getY();

		double ax = x2 - x1;
		double ay = y2 - y1;
		double bx = x4 - x3;
		double by = y4 - y3;

		double denominator = ax * by - ay * bx;

		if (denominator == 0)
                    return null;

		double cx = x3 - x1;
		double cy = y3 - y1;

		double t = (cx * by - cy * bx) / denominator;
		if (t < 0 || t > 1)
                    return null;

		double u = (cx * ay - cy * ax) / denominator;
		if (u < 0 || u > 1)
                    return null;

//		return new PVector(x1 + t * ax, y1 + t * ay);
                return new EdgePoint(0, x1 + t * ax, y1 + t * ay, 0);
	}    
    
}