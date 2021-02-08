package lit.litfx.controls.covalent;

import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.List;
import java.util.function.BiFunction;

public class Utils {
    private final static double EPSILON = 0.00001;
    public static boolean isOnSegment(double mouseX, double mouseY, Point2D p1, Point2D p2) {
        // first check if slope is undefined
        if (isSlopeUndefined(p1, p2)) {
            // check mouseY is equal to any xTo
            return (equals(mouseX, p1.getX()) && isBetween(mouseY, p1.getY(), p2.getY()));
        }


        return false;
    }

    public static void main(String[] args) {
        Point2D p1 = new Point2D(100.000000001, 0.00000);
        Point2D p2 = new Point2D(100, 100.00000);
        System.out.println("isSlopeUndefined " + isSlopeUndefined(p1, p2));
        Point2D targetPt = new Point2D(100, 100.0);
        boolean isptbetween = isBetweenPoints(targetPt, p1, p2);
        System.out.println("is pt between " + isptbetween);
    }

    private static boolean isBetween(double targetVal, double startVal, double endVal) {
        if (lessThan(startVal, endVal)) {
            return greaterThanOrEqual(targetVal, startVal) && lessThanOrEqual(targetVal, endVal);
        } else {
            return greaterThanOrEqual(targetVal, endVal) && lessThanOrEqual(targetVal, startVal);
        }
    }
    private static double slope(double x1, double x2, double y1,  double y2) {
        double m = (y2 - y1) / (x2 - x1);
        return m;
    }

    private static boolean isSlopeUndefined(Point2D p1, Point2D p2) {
        return (equals(p1.getX(), p2.getX()));
    }

    /**
     * Returns true if two doubles are considered equal.  Tests if the absolute
     * difference between two doubles has a difference less then .00001.   This
     * should be fine when comparing prices, because prices have a precision of
     * .001.
     *
     * @param a double to compare.
     * @param b double to compare.
     * @return true true if two doubles are considered equal.
     */
    public static boolean equals(double a, double b){
        return a == b || Math.abs(a - b) < EPSILON;
    }


    /**
     * Returns true if two doubles are considered equal. Tests if the absolute
     * difference between the two doubles has a difference less then a given
     * double (epsilon). Determining the given epsilon is highly dependant on the
     * precision of the doubles that are being compared.
     *
     * @param a double to compare.
     * @param b double to compare
     * @param epsilon double which is compared to the absolute difference of two
     * doubles to determine if they are equal.
     * @return true if a is considered equal to b.
     */
    public static boolean equals(double a, double b, double epsilon){
        return a == b || Math.abs(a - b) < epsilon;
    }


    /**
     * Returns true if the first double is considered greater than the second
     * double.  Test if the difference of first minus second is greater then
     * .00001.  This should be fine when comparing prices, because prices have a
     * precision of .001.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     *              double
     */
    public static boolean greaterThan(double a, double b){
        return greaterThan(a, b, EPSILON);
    }
    public static boolean greaterThanOrEqual(double a, double b){
        return greaterThan(a, b, EPSILON) || equals(a, b);
    }


    /**
     * Returns true if the first double is considered greater than the second
     * double.  Test if the difference of first minus second is greater then
     * a given double (epsilon).  Determining the given epsilon is highly
     * dependant on the precision of the doubles that are being compared.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered greater than the second
     *              double
     */
    public static boolean greaterThan(double a, double b, double epsilon){
        return a - b > epsilon;
    }


    /**
     * Returns true if the first double is considered less than the second
     * double.  Test if the difference of second minus first is greater then
     * .00001.  This should be fine when comparing prices, because prices have a
     * precision of .001.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     *              double
     */
    public static boolean lessThan(double a, double b){
        return lessThan(a, b, EPSILON);
    }
    public static boolean lessThanOrEqual(double a, double b){
        return lessThan(a, b, EPSILON) || equals(a, b);
    }


    /**
     * Returns true if the first double is considered less than the second
     * double.  Test if the difference of second minus first is greater then
     * a given double (epsilon).  Determining the given epsilon is highly
     * dependant on the precision of the doubles that are being compared.
     *
     * @param a first double
     * @param b second double
     * @return true if the first double is considered less than the second
     *              double
     */
    public static boolean lessThan(double a, double b, double epsilon){
        return b - a > epsilon;
    }

    public static boolean isBetweenPoints(Point2D targetPt, Point2D p1, Point2D p2) {

//        double crossproduct = (targetPt.getY() - p1.getY()) * (p2.getX() - p1.getX()) - (targetPt.getX() - p1.getX()) * (p2.getY() - p1.getY());
//
//        if (greaterThan(Math.abs(crossproduct), 0)){
//            return false;
//        }
//        double dotproduct = (targetPt.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (targetPt.getY() - p1.getY())*(p2.getY() - p1.getY());
//        if (lessThan(dotproduct, 0) ){
//            return false;
//        }
//        double squaredlengthba = (p2.getX() - p1.getX())*(p2.getX() - p1.getX()) + (p2.getY() - p1.getY())*(p2.getY() - p1.getY());
//        if (greaterThan(dotproduct, squaredlengthba)) {
//            return false;
//        }
//        return true;
        return isBetweenPoints(targetPt.getX(), targetPt.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static boolean isBetweenPointOnLine(double targetPtX, double targetPtY, Line line) {
        //System.out.println(line);
        return isBetweenPoints(targetPtX, targetPtY, line.startXProperty().get(), line.startYProperty().get(), line.endXProperty().get(), line.endYProperty().get());
    }

    public static boolean isBetweenPoints(double targetPtX, double targetPtY,
                                          double p1X, double p1Y,
                                          double p2X, double p2Y) {
        //System.out.println("target xTo, yTo " + targetPtX + ", " + targetPtY);
        double crossproduct = (targetPtY - p1Y) * (p2X - p1X) - (targetPtX - p1X) * (p2Y - p1Y);
        if (greaterThan(Math.abs(crossproduct), 0)){
            return false;
        }
        double dotproduct = (targetPtX - p1X) * (p2X - p1X) + (targetPtY - p1Y)*(p2Y - p1Y);
        if (lessThan(dotproduct, 0) ){
            return false;
        }
        double squaredlengthba = (p2X - p1X)*(p2X - p1X) + (p2Y - p1Y)*(p2Y - p1Y);
        if (greaterThan(dotproduct, squaredlengthba)) {
            return false;
        }
        return true;
    }

    private static double sqr(double x) {
        return x * x;
    }

    private static double dist2(double x1, double y1, double x2, double y2) {
        return sqr(x1 - x2) + sqr(y1 - y2);
    }

    private static double distToSegmentSquared(double px, double py, double x1, double y1, double x2, double y2) {
        var l2 = dist2(x1, y1, x2, y2);
        if (l2 == 0) return dist2(px, py, x1, y1);
        var t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2;
        if (lessThan(t, 0)) return dist2(px, py, x1, y1);
        if (greaterThan(t, 1)) return dist2(px, py, x2, y2);
        return dist2(px, py, x1 + t * (x2 - x1), y1 + t * (y2 - y1));
    }

    private static double distToSegment(double px, double py,
                                 double x1, double y1,
                                 double x2, double y2) {
        return Math.sqrt(distToSegmentSquared(px, py, x1, y1, x2, y2));
    }



    public static boolean isPointNearLine(double targetX,
                                          double targetY,
                                          Line line,
                                          int threshold,
                                          Integer segment) {
        //take the distance from point 1 to the target, and point 2 to the target,
        double distance = distToSegment(targetX, targetY,
                line.getStartX(), line.getStartY(),
                line.getEndX(), line.getEndY());
        boolean isNear = equals(distance, 0 ) || (greaterThan(distance, 0) && lessThan(distance, threshold));
//        if (isNear) System.out.printf("isNear:" + isNear + " segment: %d, cursor(%f,%f), nearDistance = %s, %s \n",
//                segment, targetX, targetY, distance, line.toString());
        return isNear;
    }

    public static boolean isPointNearLineLocalToScene(double targetX,
                                          double targetY,
                                          PathPane pathPane,
                                          Line line,
                                          int threshold,
                                          Integer segment) {

        Point2D startPoint = line.localToScene(line.getStartX(),line.getStartY());
        Point2D endPoint = line.localToScene(line.getEndX(),line.getEndY());
        Point2D mousePt = new Point2D(targetX, targetY);
//        System.out.println("mousePt" + mousePt + " was " + targetX + ", " + targetY);
        //take the distance from point 1 to the target, and point 2 to the target,
        double distance = distToSegment(targetX, targetY,
                startPoint.getX(), startPoint.getY(),
                endPoint.getX(), endPoint.getY());
        boolean isNear = equals(distance, 0 ) || (greaterThan(distance, 0) && lessThan(distance, threshold));
//        if (isNear) System.out.printf("isPointNearLineLocalToParent() isNear:" + isNear + " segment: %d, cursor(%f,%f), nearDistance = %s, %s \n",
//                segment, targetX, targetY, distance, line.toString());
        return isNear;
    }

    // SVG type getTotalLength
    // todo These only work for lines..!!!!
    public static double getTotalLength(Path path) {
        MoveTo startPoint =  (MoveTo) path.getElements().get(0);

        final BiFunction<PathElement, PathElement, Double> combiner = (first, second) -> {
            if (first instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) first;
                LineTo lineTo = (LineTo) second;
                Point2D p1 = new Point2D(moveTo.getX(), moveTo.getY());
                return p1.distance(lineTo.getX(), lineTo.getY());
            } else if (first instanceof LineTo && second instanceof ClosePath) {
                LineTo lineTo1 = (LineTo) first;
                Point2D p1 = new Point2D(lineTo1.getX(), lineTo1.getY());
                return p1.distance(startPoint.getX(), startPoint.getY());
            } else {
                LineTo lineTo1 = (LineTo) first;
                LineTo lineTo2 = (LineTo) second;
                Point2D p1 = new Point2D(lineTo1.getX(), lineTo1.getY());
                return p1.distance(lineTo2.getX(), lineTo2.getY());
            }
        };

        List<PathElement> elements = path.getElements();
        double totalLength = 0d;
        PathElement prev = null;
        for (PathElement e:elements) {
            if (prev == null) {
                prev = e;
                continue;
            }
            totalLength += combiner.apply(prev, e);
            prev = e;
        }
        return totalLength;
    }



}
