package lit.litfx.core.components;

import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
public class Band extends Group {

    public static double DEFAULT_VELOCITY = 1.0;
    Paint pointFill = Color.TRANSPARENT;
    Path path;
    ArrayList<SlopeVector> slopeVectors;
    double centerX;
    double centerY;
    double velocity;
    private double timeToLiveSeconds = 3.0;    
    SimpleDoubleProperty magnitudeProperty = new SimpleDoubleProperty(1.0);
    Timeline animation;
    ChangeListener<Number> magCL;
    ArrayList<Circle> pointCircles;
        
    public Band(double x, double y, double... doubles) {
        this(x, y, DEFAULT_VELOCITY, doubles);
    }
    public Band(double x, double y, double velocity, double... doubles) {
        this.centerX = x;
        this.centerY = y;
        this.velocity = velocity;
        path = new Path();
        updateQuadPath(doubles);
        getChildren().add(path);
        path.setStroke(Color.ALICEBLUE);
        slopeVectors = calcSlopeVectors(doubles, magnitudeProperty.get());
        magCL = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                path.getElements().clear();
                //tweak the points position based on the change in magnitude
                double [] currentPoints = new double[slopeVectors.size()*2];
                double changeX, changeY;
                //for each point we need to calculate the change in location 
                for(int i=0; i<slopeVectors.size();i++) {
                    //calculate the amount of change in the x direction 
                    changeX = slopeVectors.get(i).getRun() * magnitudeProperty.get() 
                        - slopeVectors.get(i).getRun();
                    //if original point is left of center radiate AWAY using a negative value
                    if(slopeVectors.get(i).getRun() < centerX)
                        changeX *= -1; 
                    //update the current point's x value
                    currentPoints[2*i] = slopeVectors.get(i).getRun() + changeX;
                    //calculate the amount of change in the x direction 
                    changeY = slopeVectors.get(i).getRise() * magnitudeProperty.get() 
                        - slopeVectors.get(i).getRise();
                    //if the original point is above the center, radiate away using a negative value
                    if(slopeVectors.get(i).getRise() < centerY)
                        changeY *= -1;
                    //update current point's y value
                    currentPoints[2*i+1] = slopeVectors.get(i).getRise() + changeY;
                }
                //System.out.println("Drifting First point: " + currentPoints[0] + ", " + currentPoints[1]);
                //rebuild the path
                updateQuadPath(currentPoints);
            }
        };
        magnitudeProperty.addListener(magCL);
        animation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(magnitudeProperty, 1.0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(getTimeToLiveSeconds()), new KeyValue(magnitudeProperty, getVelocity())),
            new KeyFrame(Duration.seconds(getTimeToLiveSeconds()), new KeyValue(opacityProperty(), 0))
        ); 
    }

    private void updateQuadPath(double[] points){
        //Remove any points from the group that might exist
        getChildren().removeIf(t -> t instanceof Circle);
        //Go through the points, using points as a control points and midpoint 
        // of line segments as points for quad curves
        double startX, startY, nextX, nextY;
        //Each double is a component of a 2D point, order is x1, y1, x2, y2...
        for (int i = 0 ; i < points.length; i+=2) {
            //First case logic, we need to look backwards
            if (i==0) {
                //get the last point and current point
                startX = points[points.length-2];
                startY = points[points.length-1];
                nextX = points[i];
                nextY = points[i+1];                
                //use the midpoint between the previous and current point for the target
                Point2D midPoint = getMidpoint(startX, startY, nextX, nextY);   
                //@DEBUG SMP visual starting point with unique circle
                //getChildren().add(new Circle(midPoint.getX(), midPoint.getY(), 3.0, 
                //    Color.TOMATO.deriveColor(1, 1, 1, 0.7)));
                //Start the Path with its first element
                MoveTo moveTo = new MoveTo(midPoint.getX(), midPoint.getY());
                moveTo.setAbsolute(true);
                path.getElements().add(moveTo);
                //Now reset with our regular indexing
                startX = points[i];
                startY = points[i+1];                
                nextX = points[i+2];
                nextY = points[i+3];   
            } else if(i==points.length-2) { //last case logic, need to connect to start
                //get the current point and first point
                startX = points[i];
                startY = points[i+1];
                nextX = points[0];
                nextY = points[1];                
            } else { //normal indexing
                //Get the current and next point
                startX = points[i];
                startY = points[i+1];                
                nextX = points[i+2];
                nextY = points[i+3];                
            }
            //use the midpoint at the target point for the curve
            Point2D midPoint = getMidpoint(startX, startY, nextX, nextY);
            //Add point to the group
            Circle circle = new Circle(midPoint.getX(), midPoint.getY(), 3.0, pointFill);
            getChildren().add(circle);
            //@DEBUG SMP visual starting point with unique circle
            //getChildren().add(new Circle(midPoint.getX(), midPoint.getY(), 3.0, 
            //    Color.DARKBLUE.deriveColor(1, 1, 1, 0.7)));
            //use the current point as the control for the curve
            QuadCurveTo qct = new QuadCurveTo(startX, startY, midPoint.getX(), midPoint.getY());
            qct.setAbsolute(true);
            path.getElements().add(qct); //add our curve to the path
        }
    }        
    private ArrayList<SlopeVector> calcSlopeVectors(double [] doubles, double magnitude) {
        ArrayList<SlopeVector> pointSlopes = new ArrayList<>(doubles.length / 2);
        for (int i = 0; i < doubles.length; i += 2) {
            pointSlopes.add(new SlopeVector(doubles[i], doubles[i+1], magnitude));
        }     
        return pointSlopes;
    }

    private Point2D getMidpoint(double x1, double y1, double x2, double y2) {
        return new Point2D(x1, y1).midpoint(x2, y2);
    }
    
    public void setPointFill(Color color) {
        pointFill = color;
        getChildren().stream().filter(n -> n instanceof Circle)
            .forEach(t -> ((Circle)t).setFill(pointFill));
    }
    /**
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    /**
     * @return the timeToLiveSeconds
     */
    public double getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    /**
     * @param timeToLiveSeconds the timeToLiveSeconds to set
     */
    public void setTimeToLiveSeconds(double timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }    
}
