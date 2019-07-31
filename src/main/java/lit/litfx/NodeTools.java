package lit.litfx;

import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import lit.litfx.components.Bolt;

/**
 *
 * @author phillsm1
 */
public enum NodeTools {
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
}
