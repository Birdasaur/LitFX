package lit.litfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 * @inspiredby https://gamedevelopment.tutsplus.com/tutorials/how-to-generate-shockingly-good-2d-lightning-effects--gamedev-2681
 * 
 */
public class Bolt extends Polyline {

    private ArrayList<BoltPoint> boltPoints;
    double density;
    double sway;
    double jitter;
    double envelopeSize = 0.75;
    double envelopeScaler = 0.1;
    SimpleIntegerProperty pointIndexProperty = new SimpleIntegerProperty(0);
            
    public Bolt(Point2D start, Point2D end, 
        double density, double sway, double jitter,
        double envelopeSize, double envelopeScalar) {
        super();
        this.density = density;
        this.sway = sway;
        this.jitter = jitter;
        this.envelopeSize = envelopeSize;
        this.envelopeScaler = envelopeScalar;
        
        double distance = start.distance(end);
        //create the starting point.
        boltPoints = new ArrayList<>();
        boltPoints.add(new BoltPoint(0, start.getX(), start.getY(), 0));

        //interpolate a line of points
        ArrayList<Point2D> linePoints = Algorithms.simpleBres2D(
            new Double(start.getX()).intValue(),
            new Double(start.getY()).intValue(), 
            new Double(end.getX()).intValue(),
            new Double(end.getY()).intValue()
        );
        
        //create random bolt points based on a density factor
        ArrayList<Integer> positions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < linePoints.size() * density; i++) {
            positions.add(new Long(Math.round(random.nextDouble()*distance)).intValue());
        }
//        System.out.println("Generated positions: " + positions);
        //Sort the indices in rising order
        Collections.sort(positions);
//        System.out.println("Sorted positions: " + positions);

        //For each ordered position, calculate displacement from the straight line
        double prevDisplacement = 0;
        for (int i = 1; i < positions.size(); i++) {
            //get the closest point on the straight line
            int pos = positions.get(i);
            if(pos >= linePoints.size())
                break;
            Point2D straightPoint = linePoints.get(pos);
            
            // defines an envelope. Points near the start of the bolt can be further from the central line.
            double envelope = i > (positions.size() * envelopeSize) ? envelopeScaler : 1;
            //small jitter  values  prevent sharp angles 
            double displacement = prevDisplacement + ThreadLocalRandom.current().nextDouble(-jitter, jitter);
            //bounds check against sway 
            if(displacement < (-sway*envelope))
                displacement = (-sway*envelope);
            else if(displacement > (sway*envelope))
                displacement = (sway*envelope);

            Point2D point = new Point2D(
                straightPoint.getX(), straightPoint.getY() + displacement);
                    
            boltPoints.add(new BoltPoint(pos, point));

            prevDisplacement = displacement;
        }
        //create the end point.
        boltPoints.add(new BoltPoint(positions.size(), end));
//        System.out.println("Calculated BoltPoints: " + boltPoints);
        updateLine(boltPoints.size());
        pointIndexProperty.set(boltPoints.size());
        pointIndexProperty.addListener((obs, oV, nV) -> {
            updateLine(nV.intValue());  
        });
    }
    public void setVisibleLength(int length) {
        pointIndexProperty.set(length);
    }
    public int getVisibleLength() {
        return pointIndexProperty.get();
    }
    
    public void animate(Duration milliseconds) {
        getPoints().clear();
        pointIndexProperty.set(0);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
            new KeyFrame(milliseconds,
                new KeyValue(pointIndexProperty, boltPoints.size(), Interpolator.EASE_OUT)
            )
        );
        timeline.play();
    }   
    
    private void updateLine(int boltLength) {
        //System.out.println("BoltLength: " + boltLength);
        //Should we remove some points?
        if(boltLength < (getPoints().size() / 2)) {
            if(boltLength <= 0)
                getPoints().clear();
            else {
                int start = 0;
                int end = boltLength * 2;
                getPoints().remove(start, end);
            }
        }
        //should we add some points?
        else if(boltLength > (getPoints().size() / 2)) {
            Double [] points = new Double [boltLength * 2];
            for(int i=0;i<boltLength;i++) {
                points[i*2] = boltPoints.get(i).getX();
                points[i*2+1] = boltPoints.get(i).getY();
            }   
            this.getPoints().setAll(points);
        }
    }
    
    /**
     * @return the boltPoints
     */
    public ArrayList<BoltPoint> getBoltPoints() {
        return boltPoints;
    }

    /**
     * @param boltPoints the boltPoints to set
     */
    public void setBoltPoints(ArrayList<BoltPoint> boltPoints) {
        this.boltPoints = boltPoints;
        updateLine(boltPoints.size());
    }    
}