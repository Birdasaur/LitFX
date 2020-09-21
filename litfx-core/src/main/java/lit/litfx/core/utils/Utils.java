package lit.litfx.core.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author Birdasaur
 */
public enum Utils {
    INSTANCE;

    public static void printTotalTime(long startTime) {
        long estimatedTime = System.nanoTime() - startTime;
        long totalNanos = estimatedTime;
        long s = totalNanos / 1000000000;
        totalNanos -= s * 1000000000;
        long ms = totalNanos / 1000000;
        totalNanos -= ms * 1000000;
        long us = totalNanos / 1000;
        totalNanos -= us * 1000;
        System.out.println("Total elapsed time: Total ns: " + estimatedTime
                + ", " + s + ":s:" + ms + ":ms:" + us + ":us:" + totalNanos + ":ns");
    }        
    public static List<Point2D> pathToPoints(Path path) {
        List<Point2D> points = new ArrayList<>();
        path.getElements().forEach(element -> {
            if(element instanceof MoveTo) {
                points.add(new Point2D(((MoveTo)element).getX(),((MoveTo)element).getY()));
            } else if(element instanceof ArcTo) {
                points.add(new Point2D(((ArcTo)element).getX(),((ArcTo)element).getY()));
            } else if(element instanceof LineTo) {
                points.add(new Point2D(((LineTo)element).getX(),((LineTo)element).getY()));
            }
        });
        return points;
    }
}
