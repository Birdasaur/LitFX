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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
public class Band extends Group {

    Path path;
    ArrayList<SlopeVector> slopeVectors;
    double centerX;
    double centerY;
    SimpleDoubleProperty magnitudeProperty = new SimpleDoubleProperty(1.0);
    Timeline animation;
    ChangeListener<Number> magCL;
    
    public Band(double x, double y, double... doubles) {
        path = new Path();
        updateQuadPath(doubles);
        getChildren().add(path);
        path.setStroke(Color.rgb(200, 200, 255));
        RadialGradient gradient1 = new RadialGradient(0, 0.1, 0.5, 0.5,  
            0.55, true, CycleMethod.NO_CYCLE,  
            new Stop(0, Color.BLUE.deriveColor(1, 1, 1, 0.2)),  
            new Stop(0.6, Color.YELLOW.deriveColor(1, 1, 1, 0.8)),  
            new Stop(1, Color.RED.deriveColor(1, 1, 1, 0.8))  
        );

        path.setFill(gradient1);
        this.centerX = x;
        this.centerY = y;
        slopeVectors = calcSlopeVectors(doubles, magnitudeProperty.get());
        magCL = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                path.getElements().clear();
                //tweak the points position based on the change in magnitude
                double [] currentPoints = new double[slopeVectors.size()*2];
                double changeX, changeY;
                for(int i=0; i<slopeVectors.size();i++) {
                    changeX = slopeVectors.get(i).getRun() * magnitudeProperty.get() 
                        - slopeVectors.get(i).getRun();
                    if(slopeVectors.get(i).getRun() < centerX)
                        changeX *= -1;
                    currentPoints[2*i] = slopeVectors.get(i).getRun() + changeX;

                    changeY = slopeVectors.get(i).getRise() * magnitudeProperty.get() 
                        - slopeVectors.get(i).getRise();
                    if(slopeVectors.get(i).getRise() < centerY)
                        changeY *= -1;
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
            new KeyFrame(Duration.seconds(3), new KeyValue(magnitudeProperty, 1.2)),
            new KeyFrame(Duration.seconds(3), new KeyValue(opacityProperty(), 0))
        );        
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
    
    private void updateQuadPath(double[] points){
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
            //@DEBUG SMP visual starting point with unique circle
            //getChildren().add(new Circle(midPoint.getX(), midPoint.getY(), 3.0, 
            //    Color.DARKBLUE.deriveColor(1, 1, 1, 0.7)));
            //use the current point as the control for the curve
            QuadCurveTo qct = new QuadCurveTo(startX, startY, midPoint.getX(), midPoint.getY());
            qct.setAbsolute(true);
            path.getElements().add(qct); //add our curve to the path
        }
    }        
}
