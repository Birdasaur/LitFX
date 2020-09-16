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

    public static Paint DEFAULT_FILL = Color.ALICEBLUE.deriveColor(1, 1, 1, 0.05);
    public static Paint DEFAULT_STROKE = Color.ALICEBLUE;
    public static double DEFAULT_GENERATION_SECONDS = 0.5;
    public static double DEFAULT_TIMETOLIVE_SECONDS = 4.0;
    
    private int polygonPoints;
    private double initialRadius;
    private double edgeVariation;
    private double generatorCenterX = 100.0;
    private double generatorCenterY = 100.0;
    private double velocity; 
    private double pathThickness;
    private boolean showPoints;
    private double generationSeconds;
    private double timeToLiveSeconds;
    
    public Paint fill;
    public Paint stroke;
    private QuadBandCreator quadBandCreator;
    private Timeline generate;

    public BandEmitter(int points, double velocity, double initialRadius, double edgeVariation) {
        this.polygonPoints = points;
        this.initialRadius = initialRadius;
        this.edgeVariation = edgeVariation;
        //Velocity of 0.0 means the band will not visually move when it animates
        //Negative velocity will animate inward towards the center and positive outward
        this.velocity = velocity;
        //Create a circle quad band by default
        quadBandCreator = new CircleQuadBandCreator(points, edgeVariation, 
            generatorCenterX, generatorCenterY, velocity, initialRadius);
        fill = DEFAULT_FILL;
        stroke = DEFAULT_STROKE;
        generationSeconds = DEFAULT_GENERATION_SECONDS;
        timeToLiveSeconds = DEFAULT_TIMETOLIVE_SECONDS;
        //Default band generaton capability which can be started and stopped 
        //users can manage generation of bands themselves by simply calling the createQuadBand()
        generate = new Timeline(new KeyFrame(Duration.seconds(generationSeconds), e -> createQuadBand()));
        generate.setCycleCount(Timeline.INDEFINITE);
    }

    public void createQuadBand() {
        Band band = quadBandCreator.createQuadBand();
        band.path.setStrokeWidth(pathThickness);
        band.path.setFill(fill);
        band.path.setStroke(stroke);
        band.setVelocity(velocity);
        band.setTimeToLiveSeconds(timeToLiveSeconds);
        if(showPoints)
            band.setPointFill(Color.ALICEBLUE);
        else
            band.setPointFill(Color.TRANSPARENT);
        getChildren().add(band);
        //starts the animation of the band by applying the velocity to the band.
        band.animation.play();

        Timeline remover = new Timeline(new KeyFrame(
            Duration.seconds(timeToLiveSeconds), e -> {
            getChildren().remove(band);
            band.animation.stop();
        }));
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
        quadBandCreator.setGeneratorCenterX(generatorCenterX);
    }

    public void setGeneratorCenterY(double generatorCenterY) {
        this.generatorCenterY = generatorCenterY;
        quadBandCreator.setGeneratorCenterY(generatorCenterY);
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
        quadBandCreator.setPolygonPoints(polygonPoints);
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
        quadBandCreator.setEdgeVariation(edgeVariation);
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

    /**
     * @return the quadBandCreator
     */
    public QuadBandCreator getQuadBandCreator() {
        return quadBandCreator;
    }

    /**
     * @param quadBandCreator the quadBandCreator to set
     */
    public void setQuadBandCreator(QuadBandCreator quadBandCreator) {
        this.quadBandCreator = quadBandCreator;
        this.quadBandCreator.setGeneratorCenterX(generatorCenterX);
        this.quadBandCreator.setGeneratorCenterY(generatorCenterY);
    }
}