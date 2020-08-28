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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
//        centerPane.widthProperty().addListener((obs, oV, nV)-> {
//            start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0);    
//        });
//        centerPane.heightProperty().addListener((obs, oV, nV)-> {
//            start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0);    
//        });
//
//        start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0); 
//        end = new Point2D(centerPane.getWidth()-10.0, centerPane.getHeight() / 2.0); 
        
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
//            centerPane.getChildren().clear();
            bandEmitters.stream().forEach(be -> {
                be.setPolygonPoints(Double.valueOf(pointsSlider.getValue()).intValue());
                be.setInitialRadius(radiusSlider.getValue());
                be.setEdgeVariation(pointDivergenceSlider.getValue());
                be.setVelocity(velocitySlider.getValue());
                be.createQuadBand();
//            branch.setBoltThickness(boltThicknessSlider.getValue());
//            branch.setBranchThickness(pathThicknessSlider.getValue());
//            branch.setEffect(collectEffects(BranchLightning.Member.PRIMARYBOLT), 
//                    BranchLightning.Member.PRIMARYBOLT);
//            branch.setEffect(collectEffects(BranchLightning.Member.BRANCH), 
//                    BranchLightning.Member.BRANCH);
//            branch.setOpacity(boltOpacitySlider.getValue(), BranchLightning.Member.PRIMARYBOLT);
//            branch.setOpacity(opacitySlider.getValue(), BranchLightning.Member.BRANCH);

            });
   
        });
    }    
//    private Effect collectEffects(BranchLightning.Member member) {
//        if(member == BranchLightning.Member.PRIMARYBOLT) {
//            SepiaTone st = new SepiaTone(boltSepiaSlider.getValue());
//            Bloom bloom = new Bloom(boltBloomSlider.getValue());
//            bloom.setInput(st);
//            Glow glow = new Glow(boltGlowSlider.getValue());
//            glow.setInput(bloom);
//            DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.AZURE, boltShadowSlider.getValue(), 0.5, 0, 0);
//            shadow.setInput(glow);
//            return shadow; 
//        } else {
//            SepiaTone st = new SepiaTone(branchSepiaSlider.getValue());
//            Bloom bloom = new Bloom(branchBloomSlider.getValue());
//            bloom.setInput(st);
//            Glow glow = new Glow(glowSlider.getValue());
//            glow.setInput(bloom);
//            DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.AZURE, shadowSlider.getValue(), 0.5, 0, 0);
//            shadow.setInput(glow);
//            return shadow; 
//        }
//    }    
}
