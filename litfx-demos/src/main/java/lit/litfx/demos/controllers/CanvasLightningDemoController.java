package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lit.litfx.core.components.BranchLightning;

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

    ArrayList<BranchLightning> bolts = new ArrayList<>();
    Point2D start;
    Point2D end;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        centerCanvas.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton() == MouseButton.PRIMARY)
                start = new Point2D(event.getX(), event.getY());
            else if(event.getButton() == MouseButton.SECONDARY)
                end = new Point2D(event.getX(), event.getY());
        });
        centerCanvas.widthProperty().addListener((obs, oV, nV)-> {
            start = new Point2D(centerCanvas.getWidth() / 2.0, centerCanvas.getHeight() / 2.0);    
        });
        centerCanvas.heightProperty().addListener((obs, oV, nV)-> {
            start = new Point2D(centerCanvas.getWidth() / 2.0, centerCanvas.getHeight() / 2.0);    
        });

        start = new Point2D(centerCanvas.getWidth() / 2.0, centerCanvas.getHeight() / 2.0); 
        end = new Point2D(centerCanvas.getWidth()-10.0, centerCanvas.getHeight() / 2.0); 
        
        timeDelayProp.bind(durationSlider.valueProperty());
        
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(!this.isCancelled() && !this.isDone()) {
                    updateBolts();
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
        GraphicsContext gc = centerCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, centerPane.getWidth(), centerPane.getHeight());
        BranchLightning branch = new BranchLightning(start, end, 
            densitySlider.getValue(), 
            swaySlider.getValue(), 
            jitterSlider.getValue(),
            branchesSlider.valueProperty().getValue().intValue(),
            branchDivergenceSlider.getValue()
        );
        
        gc.setStroke(Color.ALICEBLUE.deriveColor(
            Color.ALICEBLUE.getRed(), Color.ALICEBLUE.getGreen(), 
            Color.ALICEBLUE.getBlue(), boltOpacitySlider.getValue()));
//        gc.setEffect(collectEffects(BranchLightning.Member.PRIMARYBOLT));
        gc.setLineWidth(boltThicknessSlider.getValue());
            gc.strokePolyline(branch.primaryBolt.getXpointArray(), 
                branch.primaryBolt.getYpointArray(), branch.primaryBolt.getVisibleLength());

        gc.setStroke(Color.STEELBLUE.deriveColor(
            Color.STEELBLUE.getRed(), Color.STEELBLUE.getGreen(), 
            Color.STEELBLUE.getBlue(), branchOpacitySlider.getValue()));
//        gc.setEffect(collectEffects(BranchLightning.Member.BRANCH));
        gc.setLineWidth(branchThicknessSlider.getValue());
        branch.branchList.forEach(branchBolt -> {
            gc.strokePolyline(branchBolt.getXpointArray(), 
                branchBolt.getYpointArray(), branchBolt.getVisibleLength());
        });
        
    }    
    private Effect collectEffects(BranchLightning.Member member) {
        if(member == BranchLightning.Member.PRIMARYBOLT) {
            SepiaTone st = new SepiaTone(boltSepiaSlider.getValue());
            Bloom bloom = new Bloom(boltBloomSlider.getValue());
            bloom.setInput(st);
            Glow glow = new Glow(boltGlowSlider.getValue());
            glow.setInput(bloom);
            DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.AZURE, boltShadowSlider.getValue(), 0.5, 0, 0);
            shadow.setInput(glow);
            return shadow; 
        } else {
            SepiaTone st = new SepiaTone(branchSepiaSlider.getValue());
            Bloom bloom = new Bloom(branchBloomSlider.getValue());
            bloom.setInput(st);
            Glow glow = new Glow(branchGlowSlider.getValue());
            glow.setInput(bloom);
            DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.AZURE, branchShadowSlider.getValue(), 0.5, 0, 0);
            shadow.setInput(glow);
            return shadow; 
        }
    }    
}
