package lit.litfx;

import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

/**
 *
 * @author phillsm1
 */
public enum Algorithms {
    INSTANCE;
    public static Bolt litBounds(Node node1, Node node2) {
        Bounds bounds = node1.getBoundsInLocal();
        Bounds screenBounds = node1.localToScreen(bounds);        
        double minXNode1 = screenBounds.getMinX();
        double minYNode1 = screenBounds.getMinY();
        bounds = node2.getBoundsInLocal();
        screenBounds = node2.localToScreen(bounds);        
        double minXNode2 = screenBounds.getMinX();
        double minYNode2 = screenBounds.getMinY();
        System.out.println(minXNode1 + ", " + minYNode1 + " -> " + minXNode2 + ", " + minYNode2);
        Bolt bolt = new Bolt(
            new Point2D(minXNode1, minYNode1), new Point2D(minXNode2, minYNode2),
            0.05, 80, 10, 0.75, 0.25);   
        bolt.setStroke(Color.ALICEBLUE);
        return bolt;
    }
    public static ArrayList<Point2D> simpleBres2D(int x1, int y1, int x2, int y2) {
        ArrayList<Point2D> pointList = new ArrayList<>();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;

        while (true) {
            pointList.add(new Point2D(x1, y1));
//            System.out.print("(" +x1 + "," + y1 + ")\n"); 

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
}
