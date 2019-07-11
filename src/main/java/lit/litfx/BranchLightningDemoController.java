package lit.litfx;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author phillsm1
 */
public class BranchLightningDemoController implements Initializable {
    @FXML
    Pane centerPane;
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
        centerPane.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton() == MouseButton.PRIMARY)
                start = new Point2D(event.getX(), event.getY());
            else if(event.getButton() == MouseButton.SECONDARY)
                end = new Point2D(event.getX(), event.getY());
        });
        centerPane.widthProperty().addListener((obs, oV, nV)-> {
            start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0);    
        });
        centerPane.heightProperty().addListener((obs, oV, nV)-> {
            start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0);    
        });

        start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0); 
        end = new Point2D(centerPane.getWidth()-10.0, centerPane.getHeight() / 2.0); 
        
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
        
        tabPane.setOnMouseEntered(event -> tabPane.requestFocus());
    }    
    public void updateBolts() {
        Platform.runLater(()-> {
            centerPane.getChildren().clear();
            BranchLightning branch = new BranchLightning(start, end, 
                densitySlider.getValue(), 
                swaySlider.getValue(), 
                jitterSlider.getValue(),
                branchesSlider.valueProperty().getValue().intValue(),
                branchDivergenceSlider.getValue()
            );
            branch.setBoltThickness(boltThicknessSlider.getValue());
            branch.setBranchThickness(branchThicknessSlider.getValue());
            branch.setEffect(collectEffects(BranchLightning.Member.PRIMARYBOLT), 
                    BranchLightning.Member.PRIMARYBOLT);
            branch.setEffect(collectEffects(BranchLightning.Member.BRANCH), 
                    BranchLightning.Member.BRANCH);
            branch.setOpacity(boltOpacitySlider.getValue(), BranchLightning.Member.PRIMARYBOLT);
            branch.setOpacity(branchOpacitySlider.getValue(), BranchLightning.Member.BRANCH);
            centerPane.getChildren().add(branch);
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
