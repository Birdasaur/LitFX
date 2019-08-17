package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lit.litfx.core.components.Bolt;

/**
 * FXML Controller class
 *
 * @author phillsm1
 */
public class RideDemoController implements Initializable {

    @FXML
    GridPane centerPane;
    
    @FXML
    Slider delaySlider;
    @FXML
    ChoiceBox nodePatternChoiceBox;
    @FXML
    Button traverseNodesButton;
    @FXML
    Button clearAllButton;
    @FXML
    CheckBox repeatCheckBox;
    @FXML
    RadioButton clockwiseRadioButton;
    @FXML
    RadioButton counterRadioButton;
    @FXML
    ToggleGroup directionToggleGroup;
    
    @FXML
    SimpleLongProperty timeDelayProp = new SimpleLongProperty(16);
    
    Bolt bolt = null;
    Point2D start;
    Point2D end;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        centerPane.setOnMouseClicked((MouseEvent event) -> {
//            end = new Point2D(event.getX(), event.getY());
//        });
//        centerPane.widthProperty().addListener((obs, oV, nV)-> {
//            start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0);    
//        });
//        centerPane.heightProperty().addListener((obs, oV, nV)-> {
//            start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0);    
//        });
//        
//        start = new Point2D(centerPane.getWidth() / 2.0, centerPane.getHeight() / 2.0); 
//        end = new Point2D(centerPane.getWidth()-10.0, centerPane.getHeight() / 2.0);        
//        bolt = new Bolt(start, end, 
//            densitySlider.valueProperty().get(), 
//            swaySlider.valueProperty().get(), 
//            jitterSlider.valueProperty().get(),
//            envelopeSizeSlider.valueProperty().get(),
//            envelopeScalarSlider.valueProperty().get()
//        );
//        
//        bolt.setStroke(Color.ALICEBLUE);
//        bolt.setOpacity(opacitySlider.valueProperty().get());
//        bolt.strokeWidthProperty().bind(thicknessSlider.valueProperty());
//        bolt.opacityProperty().bind(opacitySlider.valueProperty());
//
//
//        SepiaTone st = new SepiaTone(sepiaSlider.valueProperty().get());
//        st.levelProperty().bind(sepiaSlider.valueProperty());
//
//        Bloom bloom = new Bloom(bloomSlider.valueProperty().get());
//        bloom.setInput(st);
//        bloom.thresholdProperty().bind(bloomSlider.valueProperty());
//        
//        Glow glow = new Glow(glowSlider.valueProperty().get());
//        glow.setInput(bloom);
//        glow.levelProperty().bind(glowSlider.valueProperty());
//        
//        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.CORNSILK, 10, 0.5, 0, 0);
////        Shadow shadow = new Shadow(BlurType.GAUSSIAN, Color.CORNSILK, shadowSlider.valueProperty().get());
//        shadow.setInput(glow);
//        shadow.radiusProperty().bind(shadowSlider.valueProperty());
//
//        bolt.setEffect(shadow);
//        
//        centerPane.getChildren().add(bolt);
        
        timeDelayProp.bind(delaySlider.valueProperty());
        
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(!this.isCancelled() && !this.isDone()) {
                    updateBolt();
                    Thread.sleep(timeDelayProp.get());
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();
    }    

    public void updateBolt() {
//        Bolt newBolt = new Bolt(start, end, 
//            densitySlider.valueProperty().get(), 
//            swaySlider.valueProperty().get(), 
//            jitterSlider.valueProperty().get(),
//            envelopeSizeSlider.valueProperty().get(),
//            envelopeScalarSlider.valueProperty().get()                
//        );
//        Platform.runLater(()-> this.bolt.getPoints().setAll(newBolt.getPoints()));
    }
}
