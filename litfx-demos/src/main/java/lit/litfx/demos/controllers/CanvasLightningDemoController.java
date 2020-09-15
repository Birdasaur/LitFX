package lit.litfx.demos.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.effect.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lit.litfx.core.components.BranchLightning;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Birdasaur
 */
public class CanvasLightningDemoController implements Initializable {
    @FXML
    Canvas centerCanvas;
    @FXML
    StackPane centerPane;

    @FXML
    TabPane tabPane;
    //Dynamics
    @FXML
    private Slider branchesSlider;
    @FXML
    private Slider durationSlider;
    @FXML
    private Slider swaySlider;
    @FXML
    private Slider jitterSlider;
    @FXML
    private Slider densitySlider;
    @FXML
    private Slider branchDivergenceSlider;
    
    //Special effects controls 
    @FXML
    private Slider boltThicknessSlider;
    @FXML
    Slider boltSepiaSlider;
    @FXML
    Slider boltBloomSlider;
    @FXML
    Slider boltGlowSlider;
    @FXML
    Slider boltShadowSlider;
    @FXML
    Slider boltOpacitySlider;
    
    @FXML
    private Slider branchThicknessSlider;
    @FXML
    Slider branchSepiaSlider;
    @FXML
    Slider branchBloomSlider;
    @FXML
    Slider branchGlowSlider;
    @FXML
    Slider branchShadowSlider;
    @FXML
    Slider branchOpacitySlider;
    
    @FXML
    SimpleLongProperty timeDelayProp = new SimpleLongProperty(500);    

    class LightningBoltEffect {
        Point2D start;
        Point2D end;
        Effect primaryBoltEffect;
        Effect branchBoltEffect;
        BranchLightning branch;
    }

    LightningBoltEffect lightningBolt = new LightningBoltEffect();

    public interface BoltEffectLook {
        Effect create(DoubleProperty sepiaTone, DoubleProperty bloomThreshold, DoubleProperty glowLevel, DoubleProperty shadowRadius);
    }

    /**
     * Creates a look for a lighting bolt based on the following:
     * sepia tone, bloom threshold, glow level, and drop shadow radius.
     *
     */
    private BoltEffectLook boltEffectLook = (sepiaTone,
                                     bloomThreshold,
                                     glowLevel,
                                     shadowRadius) -> {

        SepiaTone st = new SepiaTone();
        st.levelProperty().bind(sepiaTone);

        Bloom bloom = new Bloom();
        bloom.thresholdProperty().bind(bloomThreshold);
        bloom.setInput(st);

        Glow glow = new Glow();
        glow.levelProperty().bind(glowLevel);
        glow.setInput(bloom);

        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.AZURE, shadowRadius.getValue(), 0.5, 0, 0);
        shadow.radiusProperty().bind(shadowRadius);

        shadow.setInput(glow);

        return shadow;
    };

    private void wireupSliderEffects() {
        lightningBolt.primaryBoltEffect = boltEffectLook.create(
                boltSepiaSlider.valueProperty(),
                boltBloomSlider.valueProperty(),
                boltGlowSlider.valueProperty(),
                boltShadowSlider.valueProperty()
        );
        lightningBolt.branchBoltEffect = boltEffectLook.create(
                branchSepiaSlider.valueProperty(),
                branchBloomSlider.valueProperty(),
                branchGlowSlider.valueProperty(),
                branchShadowSlider.valueProperty()
        );
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // all sliders will be to effects to the lighting bolt look.
        wireupSliderEffects();

        centerCanvas.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton() == MouseButton.PRIMARY)
                lightningBolt.start = new Point2D(event.getX(), event.getY());
            else if(event.getButton() == MouseButton.SECONDARY)
                lightningBolt.end = new Point2D(event.getX(), event.getY());
        });
        centerCanvas.widthProperty().addListener((obs, oV, nV)-> {
            lightningBolt.start = new Point2D(centerCanvas.getWidth() / 2.0, centerCanvas.getHeight() / 2.0);
        });
        centerCanvas.heightProperty().addListener((obs, oV, nV)-> {
            lightningBolt.start = new Point2D(centerCanvas.getWidth() / 2.0, centerCanvas.getHeight() / 2.0);
        });

        lightningBolt.start = new Point2D(centerCanvas.getWidth() / 2.0, centerCanvas.getHeight() / 2.0);
        lightningBolt.end = new Point2D(centerCanvas.getWidth()-10.0, centerCanvas.getHeight() / 2.0);
        
        timeDelayProp.bind(durationSlider.valueProperty());
        
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(!this.isCancelled() && !this.isDone()) {
                    updateBolts();
                    Platform.runLater(() -> renderBolts());
                    Thread.sleep(timeDelayProp.get());
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();
        
        centerCanvas.widthProperty().bind(centerPane.widthProperty());
        centerCanvas.heightProperty().bind(centerPane.heightProperty());
        
        tabPane.setOnMouseEntered(event -> tabPane.requestFocus());
    }

    public void updateBolts() {
        // @TODO imo we could refactor the object as a Java bean rather than a JavaFX Group
        lightningBolt.branch = new BranchLightning(lightningBolt.start,
                lightningBolt.end,
                densitySlider.getValue(),
                swaySlider.getValue(),
                jitterSlider.getValue(),
                branchesSlider.valueProperty().getValue().intValue(),
                branchDivergenceSlider.getValue()
        );
    }

    public void renderBolts() {
        BranchLightning branch = lightningBolt.branch;

        GraphicsContext gc = centerCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, centerPane.getWidth(), centerPane.getHeight());
        gc.setStroke(Color.ALICEBLUE.deriveColor(
            Color.ALICEBLUE.getRed(), Color.ALICEBLUE.getGreen(), 
            Color.ALICEBLUE.getBlue(), boltOpacitySlider.getValue()));
        gc.setEffect(lightningBolt.primaryBoltEffect);
        gc.setLineWidth(boltThicknessSlider.getValue());
            gc.strokePolyline(branch.primaryBolt.getXpointArray(),
                branch.primaryBolt.getYpointArray(), branch.primaryBolt.getVisibleLength());

        gc.setStroke(Color.STEELBLUE.deriveColor(
            Color.STEELBLUE.getRed(), Color.STEELBLUE.getGreen(), 
            Color.STEELBLUE.getBlue(), branchOpacitySlider.getValue()));
        gc.setEffect(lightningBolt.branchBoltEffect);
        gc.setLineWidth(branchThicknessSlider.getValue());
        branch.branchList.forEach(branchBolt -> {
            gc.strokePolyline(branchBolt.getXpointArray(), 
                branchBolt.getYpointArray(), branchBolt.getVisibleLength());
        });
    }
}
