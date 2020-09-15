package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import lit.litfx.core.components.BandEmitter;

/**
 * FXML Controller class
 *
 * @author Birdasaur
 */
public class AnimatedWavesDemoController implements Initializable {
    @FXML
    Pane centerPane;
    @FXML
    TabPane tabPane;
    //Dynamics
    @FXML
    private Slider pointsSlider;
    @FXML
    private Slider pointDivergenceSlider;
    @FXML
    private Slider radiusSlider;
    @FXML
    private Slider velocitySlider;
    @FXML
    private Slider updateDelaySlider;
    
    //Special effects controls 
    @FXML
    CheckBox showPathLines;
    @FXML
    CheckBox showControlPoints;
    @FXML
    CheckBox enableGradientFill;
    
    @FXML
    Slider pathThicknessSlider;
    @FXML
    Slider glowSlider;
    @FXML
    Slider shadowSlider;
    @FXML
    Slider opacitySlider;

    @FXML
    Circle circle1;
    @FXML
    Circle circle2;
    
    @FXML
    SimpleLongProperty timeDelayProp = new SimpleLongProperty(500);    

    ArrayList<BandEmitter> bandEmitters = new ArrayList<>();

    RadialGradient gradient1 = new RadialGradient(0, 0.1, 0.5, 0.5,  
        0.55, true, CycleMethod.NO_CYCLE,  
        new Stop(0, Color.BLUE.deriveColor(1, 1, 1, 0.1)),  
        new Stop(0.7, Color.YELLOW.deriveColor(1, 1, 1, 0.2)),  
        new Stop(0.8, Color.YELLOW.deriveColor(1, 1, 1, 0.8)),  
        new Stop(1, Color.RED.deriveColor(1, 1, 1, 0.8))  
    );    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Create the default band emitters

        BandEmitter be1 = createBandEmitter(circle1.getLayoutX(), circle1.getLayoutY(),
            Double.valueOf(pointsSlider.getValue()).intValue(),
            radiusSlider.getValue(), pointDivergenceSlider.getValue(), velocitySlider.getValue());
        bandEmitters.add(be1);

        BandEmitter be2 = createBandEmitter(circle2.getLayoutX(), circle2.getLayoutY(),
            Double.valueOf(pointsSlider.getValue()).intValue(), 
            radiusSlider.getValue(), pointDivergenceSlider.getValue(), velocitySlider.getValue());
        bandEmitters.add(be2);
        
        centerPane.getChildren().addAll(be1, be2);
        
        centerPane.setOnMouseClicked((MouseEvent event) -> {
//            if(event.getButton() == MouseButton.PRIMARY)
//                start = new Point2D(event.getX(), event.getY());
//            else if(event.getButton() == MouseButton.SECONDARY)
//                end = new Point2D(event.getX(), event.getY());
        });
 
        timeDelayProp.bind(updateDelaySlider.valueProperty());
        
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(!this.isCancelled() && !this.isDone()) {
                    updateBands();
                    Thread.sleep(timeDelayProp.get());
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();
        
        tabPane.setOnMouseEntered(event -> tabPane.requestFocus());
    }    
    
    private BandEmitter createBandEmitter(double x, double y, int points,
            double initialRadius, double divergence, double velocity) {
        BandEmitter be = new BandEmitter(points,initialRadius, divergence);
        be.setGeneratorCenterX(x);
        be.setGeneratorCenterY(y);
        be.setVelocity(velocity);
        return be;
    }
    public void updateBands() {
        Platform.runLater(()-> {
            bandEmitters.stream().forEach(be -> {
                be.setPolygonPoints(Double.valueOf(pointsSlider.getValue()).intValue());
                be.setInitialRadius(radiusSlider.getValue());
                be.setEdgeVariation(pointDivergenceSlider.getValue());
                be.setVelocity(velocitySlider.getValue());
                be.createQuadBand();
                be.setPathThickness(pathThicknessSlider.getValue());
                be.setEffect(collectEffects());
                be.setOpacity(opacitySlider.getValue());
                be.setShowPoints(showControlPoints.isSelected());
                if(showPathLines.isSelected())
                    be.setCustomStroke(BandEmitter.DEFAULT_STROKE);
                else
                    be.setCustomStroke(Color.TRANSPARENT);
                if(enableGradientFill.isSelected())
                    be.setCustomFill(gradient1);
                else
                    be.setCustomFill(BandEmitter.DEFAULT_FILL);
            });
        });
    }    
    private Effect collectEffects() {
        Glow glow = new Glow(glowSlider.getValue());
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.AZURE, shadowSlider.getValue(), 0.5, 0, 0);
        shadow.setInput(glow);
        return shadow; 
    }    
}
