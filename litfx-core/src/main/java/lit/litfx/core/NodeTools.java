package lit.litfx.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lit.litfx.core.components.Bolt;
import lit.litfx.core.components.BoltDynamics;
import lit.litfx.core.components.EdgePoint;

/**
 *
 * @author Birdasaur
 */
public enum NodeTools {
    INSTANCE;

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
    public static ArrayList<EdgePoint> nodeLoopPoints(EdgePoint startPoint, Node node) {
        Bounds boundsInLocal = node.getBoundsInLocal();
        Bounds nodeSceneBounds = node.localToScene(boundsInLocal);        

        ArrayList<EdgePoint> points = new ArrayList<>();
        //convert each bounds point to a usable EdgePoint
        EdgePoint upperLeft = new EdgePoint(0, nodeSceneBounds.getMinX(), nodeSceneBounds.getMinY(), 0);
        EdgePoint upperRight = new EdgePoint(1, nodeSceneBounds.getMaxX(), nodeSceneBounds.getMinY(), 0);
        EdgePoint lowerRight = new EdgePoint(2, nodeSceneBounds.getMaxX(), nodeSceneBounds.getMaxY(), 0);
        EdgePoint lowerLeft = new EdgePoint(3, nodeSceneBounds.getMinX(), nodeSceneBounds.getMaxY(), 0);
        //Find shortest distance to each bound point
        double upperLeftDistance = startPoint.distance(upperLeft);
        double upperRightDistance = startPoint.distance(upperRight);
        double lowerRightDistance = startPoint.distance(lowerRight);
        double lowerLeftDistance = startPoint.distance(lowerLeft);
        //Determine the order of the points based on location of bounds
        if(upperLeftDistance <= upperRightDistance 
        && upperLeftDistance <= lowerRightDistance 
        && upperLeftDistance <= lowerLeftDistance) {
            points.add(upperLeft);
            points.add(upperRight);
            points.add(lowerRight);
            points.add(lowerLeft);
        } else if(upperRightDistance <= upperLeftDistance 
        && upperRightDistance <= lowerRightDistance 
        && upperRightDistance <= lowerLeftDistance) {
            points.add(upperRight);
            points.add(lowerRight);
            points.add(lowerLeft);
            points.add(upperLeft);
        } else if(lowerRightDistance <= upperLeftDistance 
        && lowerRightDistance <= upperRightDistance 
        && lowerRightDistance <= lowerLeftDistance) {
            points.add(lowerRight);
            points.add(lowerLeft);
            points.add(upperLeft);
            points.add(upperRight);
        } else {
            points.add(lowerLeft);
            points.add(upperLeft);
            points.add(upperRight);
            points.add(lowerRight);
        }
        //System.out.println(minXNode1 + ", " + minYNode1 + " -> " + minXNode2 + ", " + minYNode2);
        return points;
    }

    public static Bolt arcNodes(Node node1, Node node2, BoltDynamics dynamics) {
        ArrayList<EdgePoint> arcPoints = shortArcPoints(node1, node2);
        Bolt bolt = new Bolt(arcPoints.get(0), arcPoints.get(1), dynamics); 
        bolt.setStroke(Color.ALICEBLUE);
        return bolt;
    }
   public static final List<Node> getAllChildren(final Parent parent) {
        final List<Node> result = new LinkedList<>();
        if (parent != null) {
            final List<Node> childrenLvl1 = parent.getChildrenUnmodifiable();
            result.addAll(childrenLvl1);
            final List<Node> childrenLvl2 =
                    childrenLvl1.stream()
                                .filter(c -> c instanceof Parent)
                                .map(c -> (Parent) c)
                                .map(NodeTools::getAllChildren)
                                .flatMap(List::stream)
                                .collect(Collectors.toList());
            result.addAll(childrenLvl2);
        }
        return result;
    }    
    public static List<Line> boundsToLines(Node node) {
        Bounds boundsInLocal = node.getBoundsInLocal();
        Bounds nodeSceneBounds = node.localToScene(boundsInLocal);   
        List<Line> lines = new ArrayList<>();
        //convert each bounds point pair to a Line, clockwise order from min x, min y
        lines.add(new Line(nodeSceneBounds.getMinX(), nodeSceneBounds.getMinY(), 
            nodeSceneBounds.getMaxX(), nodeSceneBounds.getMinY()));
        lines.add(new Line(nodeSceneBounds.getMaxX(), nodeSceneBounds.getMinY(), 
            nodeSceneBounds.getMaxX(), nodeSceneBounds.getMaxY()));
        lines.add(new Line(nodeSceneBounds.getMaxX(), nodeSceneBounds.getMaxY(), 
            nodeSceneBounds.getMinX(), nodeSceneBounds.getMaxY()));
        lines.add(new Line(nodeSceneBounds.getMinX(), nodeSceneBounds.getMaxY(), 
            nodeSceneBounds.getMinX(), nodeSceneBounds.getMinY()));
        return lines;
    }
   
    public static double transformX(double dataXcoord, double domain, double range) {
        return (dataXcoord * range) / domain;
    }
    public static double transformY(double dataYcoord, double domain, double range) {
        return (dataYcoord * range) / domain;
    }
}
