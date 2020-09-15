package lit.litfx.core.components;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
/**
 * Generates contour line bands on the screen whenever the
 * createQuadBand method is called. Bands grow and fade out over 3 seconds
 */
public class BandEmitter extends Group {

    private int polygonPoints;
    private double initialRadius;
    private double edgeVariation;
    private double generatorCenterX = 100.0;
    private double generatorCenterY = 100.0;
    private double velocity; 
    private double pathThickness;
    private boolean showPoints;
    public Paint fill;
    public Paint stroke;
    public static Paint DEFAULT_FILL = Color.ALICEBLUE.deriveColor(1, 1, 1, 0.05);
    public static Paint DEFAULT_STROKE = Color.ALICEBLUE;
    
    private Timeline generate = new Timeline(
        new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createQuadBand();
            }
        })
    );


    public BandEmitter(int points, double initialRadius, double edgeVariation) {
        this.polygonPoints = points;
        this.initialRadius = initialRadius;
        this.edgeVariation = edgeVariation;
        velocity = 0.0;
        generate.setCycleCount(Timeline.INDEFINITE);
        fill = DEFAULT_FILL;
        stroke = DEFAULT_STROKE;
    }

    public void createQuadBand() {
        //create a random polygon around the center point
        Random random = new Random();
        double [] doubles = new double[getPolygonPoints()*2];
        double angleRadiansSlice  = 6.283185 / getPolygonPoints(); 
        double randomLength, angleRadians = 0.0;

        //get a randomly perturbed length based on a set radius
        randomLength = getInitialRadius() + (getEdgeVariation() * random.nextGaussian()); 
        //setup first segment with a radian angle of 0.0
        doubles[0] = getGeneratorCenterX() + (Math.cos(angleRadians) * randomLength);
        doubles[1] = getGeneratorCenterY() + (Math.sin(angleRadians) * randomLength);
        //@DEBUG SMP create a circle showing the first point
        //getChildren().add(new Circle(doubles[0], doubles[1], 5.0, 
        //    Color.GREEN.deriveColor(1, 1, 1, 0.7)));
        //For each point, compute the coordinats    
        for(int i=1;i<getPolygonPoints();i++) {
            //jog the angle forward
            angleRadians += angleRadiansSlice;
            //get a new random length
            randomLength = getInitialRadius() + (getEdgeVariation() * random.nextGaussian()); 
            //create new end point
            doubles[2*i] = getGeneratorCenterX() + (Math.cos(angleRadians) * randomLength);
            doubles[2*i+1] = getGeneratorCenterY() + (Math.sin(angleRadians) * randomLength);
            //@DEBUG SMP Test circle to visualize points
            //getChildren().add(new Circle(doubles[2*i], doubles[2*i+1], 3.0, 
            //    Color.ALICEBLUE.deriveColor(1, 1, 1, 0.7)));
        }

        Band band = new Band(getGeneratorCenterX(), getGeneratorCenterY(), velocity, doubles);
        band.path.setStrokeWidth(pathThickness);
        band.path.setFill(fill);
        band.path.setStroke(stroke);
        if(showPoints)
            band.setPointFill(Color.ALICEBLUE);
        else
            band.setPointFill(Color.TRANSPARENT);
        getChildren().add(band);
        band.animation.play();

        Timeline remover = new Timeline(
            new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    getChildren().remove(band);
                    band.animation.stop();
                }
            })
        );
        remover.play();
    }

    public void setCustomFill(Paint fill) {
        this.fill = fill;
    }
    public void setCustomStroke(Paint stroke) {
        this.stroke = stroke;
    }    
    public void startGenerating() {
        generate.play();
    }

    public void stopGenerating() {
        generate.stop();
    }

    public void setGeneratorCenterX(double generatorCenterX) {
        this.generatorCenterX = generatorCenterX;
    }

    public void setGeneratorCenterY(double generatorCenterY) {
        this.generatorCenterY = generatorCenterY;
    }

    /**
     * @return the polygonPoints
     */
    public int getPolygonPoints() {
        return polygonPoints;
    }

    /**
     * @param polygonPoints the polygonPoints to set
     */
    public void setPolygonPoints(int polygonPoints) {
        this.polygonPoints = polygonPoints;
    }

    /**
     * @return the initialRadius
     */
    public double getInitialRadius() {
        return initialRadius;
    }

    /**
     * @param initialRadius the initialRadius to set
     */
    public void setInitialRadius(double initialRadius) {
        this.initialRadius = initialRadius;
    }

    /**
     * @return the edgeVariation
     */
    public double getEdgeVariation() {
        return edgeVariation;
    }

    /**
     * @param edgeVariation the edgeVariation to set
     */
    public void setEdgeVariation(double edgeVariation) {
        this.edgeVariation = edgeVariation;
    }

    /**
     * @return the generatorCenterX
     */
    public double getGeneratorCenterX() {
        return generatorCenterX;
    }

    /**
     * @return the generatorCenterY
     */
    public double getGeneratorCenterY() {
        return generatorCenterY;
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
     * @return the pathThickness
     */
    public double getPathThickness() {
        return pathThickness;
    }

    /**
     * @param pathThickness the pathThickness to set
     */
    public void setPathThickness(double pathThickness) {
        this.pathThickness = pathThickness;
    }    

    /**
     * @param showPoints the showPoints to set
     */
    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }
}