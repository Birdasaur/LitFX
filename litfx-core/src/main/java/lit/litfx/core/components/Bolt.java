package lit.litfx.core.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import lit.litfx.core.Algorithms;

/**
 *
 * @author phillsm1
 * @inspiredby https://gamedevelopment.tutsplus.com/tutorials/how-to-generate-shockingly-good-2d-lightning-effects--gamedev-2681
 * 
 */
public class Bolt extends AnimatedEdge {
    double sway;
    double jitter;
    double envelopeSize = 0.75;
    double envelopeScaler = 0.1;
            
    public Bolt(EdgePoint start, EdgePoint end, BoltDynamics dynamics) {
        this(start.toPoint2D(), end.toPoint2D(), dynamics.density, dynamics.sway,
            dynamics.jitter, dynamics.envelopeSize, dynamics.envelopeScalar);
    }
    public Bolt(Point2D start, Point2D end, 
        double density, double sway, double jitter,
        double envelopeSize, double envelopeScalar) {
        super(new EdgePoint(0, start), new EdgePoint(1, end), density);
        this.sway = sway;
        this.jitter = jitter;
        this.envelopeSize = envelopeSize;
        this.envelopeScaler = envelopeScalar;
        
        //create the starting point.
        getEdgePoints().add(new EdgePoint(0, start.getX(), start.getY(), 0));
        //interpolate a line of points
        float distance = (float) start.distance(end);
        ArrayList<Point2D> linePoints = Algorithms.simpleBres2D(
            (int) start.getX(), (int) start.getY(), 
            (int) end.getX(),(int) end.getY()
        );
        //create random bolt points based on a density factor
        ArrayList<Integer> positions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < linePoints.size() * density; i++) {
            int pos = Math.round(random.nextFloat()*distance);
            if(pos <= 0)
                pos = 1;
            positions.add(pos);
            
        }
        //System.out.println("Generated positions: " + positions);
        //Sort the indices in rising order
        Collections.sort(positions);
        //For each ordered position, calculate displacement from the straight line
        getEdgePoints().addAll(calcEdgePoints(positions, linePoints));
        //create the end point.
        getEdgePoints().add(new EdgePoint(positions.size(), end));
        //System.out.println("Calculated BoltPoints: " + boltPoints);
        updateLength(getEdgePoints().size());
        pointIndexProperty.set(getEdgePoints().size());
        pointIndexProperty.addListener((obs, oV, nV) -> {
            updateLength(nV.intValue());  
        });
    }
    
    private ArrayList<EdgePoint> calcEdgePoints(ArrayList<Integer> positions, ArrayList<Point2D> linePoints) {
        //For each ordered position, calculate displacement from the straight line
        ArrayList<EdgePoint> edgePointList = new ArrayList<>();
        double prevDisplacement = 0;
        for (int i = 1; i < positions.size(); i++) {
            //get the closest point on the straight line
            int pos = positions.get(i);
            if(pos >= linePoints.size())
                break;
            Point2D straightPoint = linePoints.get(pos);
            
            // defines an envelope. Points near the start of the bolt can be 
            //further from the central line.
            double envelope = i > (positions.size() * envelopeSize) ? envelopeScaler : 1;
            //small jitter  values  prevent sharp angles 
            double displacement = prevDisplacement 
                + ThreadLocalRandom.current().nextDouble(-jitter, jitter);
            //bounds check against sway 
            if(displacement < (-sway*envelope))
                displacement = (-sway*envelope);
            else if(displacement > (sway*envelope))
                displacement = (sway*envelope);

            Point2D prevPoint = linePoints.get(pos-1);
            //Horizontal Case
            Point2D newPoint = new Point2D(
                straightPoint.getX(), straightPoint.getY() + displacement);
            //Vertical edge case
            if(newPoint.getX() == prevPoint.getX() 
            && straightPoint.getY() != prevPoint.getY())
                 newPoint = new Point2D(straightPoint.getX() + displacement, straightPoint.getY());
                
            edgePointList.add(new EdgePoint(pos, newPoint));
            prevDisplacement = displacement;
        }
        return edgePointList;
    }
    
    @Override
    public void animate(Duration milliseconds) {
        getPoints().clear();
        pointIndexProperty.set(0);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(milliseconds,
                new KeyValue(pointIndexProperty, getEdgePoints().size(), Interpolator.EASE_OUT)
            )
        );
        timeline.play();
    }   

    @Override
    public void updateLength(int length) {
        //System.out.println("BoltLength: " + boltLength);
        //Should we remove some points?
        if(length < (getPoints().size() / 2)) {
            if(length <= 0)
                getPoints().clear();
            else {
                int startIndex = 0;
                int endIndex = length * 2;
                getPoints().remove(startIndex, endIndex);
            }
        }
        //should we add some points?
        else if(length > (getPoints().size() / 2)) {
            updatePolylinePoints(length);
        }
    }
}