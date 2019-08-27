package lit.litfx.core.components;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Line;
import lit.litfx.core.Algorithms;

/**
 *
 * @author Birdasaur
 */
public class LineOfSight {

    EdgePoint centerPoint;
    double scanLength;

    public LineOfSight(EdgePoint centerPoint, double scanLength) {
        this.centerPoint = centerPoint;
        this.scanLength = scanLength;
    }

    /**
     * Sweep around the given circle with the given distance and create the scan
     * lines
     *
     * @param angleStart
     * @param angleEnd
     * @param angleStep
     * @param scanLineLength how far to shoot the scan lines
     * @return List of type Line Inspired by examples at
     * https://gist.github.com/Roland09
     */
    public List<Line> createScanLines(double angleStart, double angleEnd, double angleStep, double scanLineLength) {
        //Create a new collection to store the scan lines
        List<Line> scanLines = new ArrayList<>();
        for (double angle = angleStart; angle < angleEnd; angle += angleStep) {
            //find the endpoint based on the current angle
            double x = centerPoint.getX() + Math.cos(angle) * scanLineLength;
            double y = centerPoint.getY() + Math.sin(angle) * scanLineLength;
            //new line eminating from the origin
            Line line = new Line(centerPoint.getX(), centerPoint.getY(), x, y);
            scanLines.add(line);
        }
        return scanLines;
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
            List<EdgePoint> intersections = getIntersections(scanLine, sceneLines);

            // find the intersection that is closest to the scanline
            if (intersections.size() > 0) {
                double x = 0;
                double y = 0;
                double shortestDistance = Double.MAX_VALUE;
                //find the closest intersection find the closest 
                for (EdgePoint intersectionPoint : intersections) {
                    //double currDist = scanLine.getStart().dist(intersectionPoint);
                    double currDist = new EdgePoint(0, scanLine.getStartX(), scanLine.getEndY(), 0)
                        .distance(intersectionPoint);
                    if (currDist < shortestDistance) {
                        x = intersectionPoint.getX();
                        y = intersectionPoint.getY();
                        shortestDistance = currDist;
                    }
                }
                intersectionPoints.add(new EdgePoint(0, x, y, 0));
            }
        }
        return intersectionPoints;
    }

    /**
     * Find intersecting lines.
     *
     * @param scanLine
     * @param sceneLines
     * @return
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
            else if (start.distance(end) > scanLength) {
                end.normalize();
                end.multiply(scanLength);
            }
            // we have a valid line end, either an intersection with another line or we have the scan line limit
            if (end != null) {
                list.add(end);
            }
        }
        return list;
    }
}
