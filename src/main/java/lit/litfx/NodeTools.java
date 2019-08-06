package lit.litfx;

import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import lit.litfx.components.Bolt;
import lit.litfx.components.EdgePoint;

/**
 *
 * @author phillsm1
 */
public enum NodeTools {
    INSTANCE;
    public static Bolt arcNodes(Node node1, Node node2) {
        ArrayList<EdgePoint> arcPoints = shortArcPoints(node1, node2);
        
        Bolt bolt = new Bolt(arcPoints.get(0).toPoint2D(), arcPoints.get(1).toPoint2D(),
            0.1, 80, 15, 0.85, 0.1); 
        bolt.setStroke(Color.ALICEBLUE);
        return bolt;
    }

    public static ArrayList<EdgePoint> shortArcPoints(Node node1, Node node2) {
        Bounds boundsInLocal = node1.getBoundsInLocal();
        Bounds node1SceneBounds = node1.localToScene(boundsInLocal);        
        boundsInLocal = node2.getBoundsInLocal();
        Bounds node2SceneBounds = node2.localToScene(boundsInLocal);        

        //Default: node 1 is left of node 2 
        double minXNode1 = node1SceneBounds.getMaxX();
        double minXNode2 = node2SceneBounds.getMinX();
        //Default: node 1 is above or equal to node 2 
        double minYNode1 = node1SceneBounds.getMaxY();
        double minYNode2 = node2SceneBounds.getMinY();
        
        //if node 1 is to the right of node 2
        if(node1SceneBounds.getMinX() > node2SceneBounds.getMaxX()) {
            minXNode1 = node1SceneBounds.getMinX();
            minXNode2 = node2SceneBounds.getMaxX();
        }
        //if node 1 is below node 2
        if(node1SceneBounds.getMinY() > node2SceneBounds.getMaxY()) {
            minYNode1 = node1SceneBounds.getMinY();
            minYNode2 = node2SceneBounds.getMaxY();
        }      
        //System.out.println(minXNode1 + ", " + minYNode1 + " -> " + minXNode2 + ", " + minYNode2);

        ArrayList<EdgePoint> points = new ArrayList<>();
        points.add(new EdgePoint(0, minXNode1, minYNode1, 0));
        points.add(new EdgePoint(1, minXNode2, minYNode2, 0));
        return points;
    }
    public static double transformX(double dataXcoord, double domain, double range) {
        return (dataXcoord * range) / domain;
    }
    public static double transformY(double dataYcoord, double domain, double range) {
        return (dataYcoord * range) / domain;
    }
}
