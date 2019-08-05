package lit.litfx;

import java.util.ArrayList;
import java.util.Properties;
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
        Bounds parentBounds = node1.localToParent(bounds);        
        double minXNode1 = parentBounds.getMinX();
        double minYNode1 = parentBounds.getMinY();
        bounds = node2.getBoundsInLocal();
        parentBounds = node2.localToParent(bounds);        
        double minXNode2 = parentBounds.getMinX();
        double minYNode2 = parentBounds.getMinY();
        System.out.println(minXNode1 + ", " + minYNode1 + " -> " + minXNode2 + ", " + minYNode2);
        Bolt bolt = new Bolt(
            new Point2D(minXNode1, minYNode1), new Point2D(minXNode2, minYNode2),
            0.1, 80, 15, 0.85, 0.1); 
        bolt.setStroke(Color.ALICEBLUE);
        return bolt;
    }
}
