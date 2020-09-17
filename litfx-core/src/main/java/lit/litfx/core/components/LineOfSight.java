package lit.litfx.core.components;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Line;
import lit.litfx.core.Algorithms;

/**
 *
 * @author Birdasaur
 */
public class LineOfSight {

    public SimpleObjectProperty<EdgePoint> centerPoint;
    public SimpleDoubleProperty scanLength;
    public List<Line> scanLines = new ArrayList<>();
    public List<EdgePoint> intersections = new ArrayList<>();
            
    public LineOfSight(EdgePoint centerPoint, double scanLength) {
        this.centerPoint = new SimpleObjectProperty<>(centerPoint);
        this.scanLength = new SimpleDoubleProperty(scanLength);
    }

    public void updateScan(List<Line> nodeLines, double scanlines) {
        scanLines = createScanLines(0, 360, scanlines);
//        System.out.println("Scan Lines count: " + scanLines.size());
        intersections = getIntersectionPoints(scanLines, nodeLines);
//        System.out.println("Intersections In Range: " + intersections.size());
    }
    
    /**
     * Sweep around the given circle with the given distance and create the scan
     * lines
     *
     * @param angleStart
     * @param angleEnd
     * @param angleStep
     * @return List of type Line Inspired by examples at
     * https://gist.github.com/Roland09
     */
    public List<Line> createScanLines(double angleStart, double angleEnd, double angleStep) {
        double angleStartRads = Math.toRadians(angleStart);
        double angleEndRads = Math.toRadians(angleEnd);
        double angleStepRads = Math.toRadians(angleStep);
        //Create a new collection to store the scan lines
        List<Line> newScanLines = new ArrayList<>();
        for (double angle = angleStartRads; angle < angleEndRads; angle += angleStepRads) {
            //find the endpoint based on the current angle
            double x = centerPoint.get().getX() + Math.cos(angle) * scanLength.get();
            double y = centerPoint.get().getY() + Math.sin(angle) * scanLength.get();
            //new line eminating from the origin
            Line line = new Line(centerPoint.get().getX(), centerPoint.get().getY(), x, y);
            newScanLines.add(line);
        }
        return newScanLines;
    }

    /**
     * Get all the intersecting points for the given scan lines and the given
     * scene lines.
     *
     * @param scanLines
     * @param sceneLines
     * @return
     */
    public List<EdgePoint> getIntersectionPoints(List<Line> scanLines, List<Line> sceneLines) {
        List<EdgePoint> intersectionPoints = new ArrayList<>();
        //Compare each scanLine against each scene line (node bounds etc)
        for (Line scanLine : scanLines) {
            //Given the current scanline find all the intersections with scene lines
            List<EdgePoint> newIntersections = getIntersections(scanLine, sceneLines);

            // find the intersection that is closest to the scanline
            if (newIntersections.size() > 0) {
                intersectionPoints.add(
                    closestPoint(
                        new EdgePoint(0, scanLine.getStartX(), scanLine.getStartY(), 0),
                        newIntersections
                    )
                );
            }
        }
        return intersectionPoints;
    }

    private EdgePoint closestPoint(EdgePoint startPoint, List<EdgePoint> intersections) {
        double closestX = 0;
        double closestY = 0;
        double shortestDistance = Double.MAX_VALUE;
        //find the closest intersection find the closest 
        for (EdgePoint intersectionPoint : intersections) {
            double currDist = startPoint.distance(intersectionPoint);
            if (currDist < shortestDistance) {
                closestX = intersectionPoint.getX();
                closestY = intersectionPoint.getY();
                shortestDistance = currDist;
            }
        }
        return new EdgePoint(0, closestX, closestY, 0);
    }
    
    /**
     * Find List of EdgePoints for any intersecting lines.
     *
     * @param scanLine The line shooting out
     * @param sceneLines List of Line objects to test against
     * @return List of EdgePoints of each location where an intersection on a 
     * Line in the scene occurs
     */
    public List<EdgePoint> getIntersections(Line scanLine, List<Line> sceneLines) {

        List<EdgePoint> list = new ArrayList<>();
        EdgePoint intersection;

        for (Line line : sceneLines) {
            // check if 2 lines intersect
            intersection = Algorithms.getLineIntersection(
                    new EdgePoint(0, scanLine.getStartX(), scanLine.getStartY(), 0),
                    new EdgePoint(1, scanLine.getEndX(), scanLine.getEndY(), 0),
                    new EdgePoint(2, line.getStartX(), line.getStartY(), 0),
                    new EdgePoint(3, line.getEndX(), line.getEndY(), 0)
            );
            // lines intersect => we have an end point
            EdgePoint end = null;
            if (intersection != null) {
                end = new EdgePoint(0, intersection.getX(), intersection.getY(), 0);
            }
            EdgePoint start = new EdgePoint(0, scanLine.getStartX(), scanLine.getStartY(), 0);
            // no intersection found => full scan line length
            if (end == null) {
                end = new EdgePoint(1, scanLine.getEndX(), scanLine.getEndY(), 0);
            } // intersection found => limit to scan line length
            else if (start.distance(end) > scanLength.get()) {
                end.normalize();
                end.multiply(scanLength.get());
            }
            // we have a valid line end, either an intersection with another line or we have the scan line limit
            if (end != null) {
                list.add(end);
            }
        }
        return list;
    }
}
