package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import lit.litfx.core.ShadowView;
import lit.litfx.core.components.EdgePoint;
import lit.litfx.core.components.LineOfSight;

/**
 * FXML Controller class
 *
 * @author Birdasaur
 */
public class ShadowsDemoController implements Initializable {

    @FXML
    GridPane centerPane;
    @FXML
    StackPane centerStackPane;
    
    @FXML
    ColorPicker shadowColorPicker;
    @FXML
    ColorPicker lightColorPicker;
    @FXML
    ColorPicker intersectionsColorPicker;
    @FXML
    ColorPicker wireframeColorPicker;

    @FXML
    CheckBox shadowCheckBox;
    @FXML
    CheckBox lightCheckBox;
    @FXML
    CheckBox intersectionsCheckBox;
    @FXML
    CheckBox wireframeCheckBox;

    
    @FXML
    Slider ambientLightSlider;    
    
    ShadowView shadowView;
    ArrayList<Node> centerChildren;     
    LineOfSight los;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //assign the Region you want to engulf in shadows
        shadowView = new ShadowView(centerPane);
        
        //If you already have a nice stackpane, just add your LitView last
        centerStackPane.getChildren().add(shadowView);
        shadowView.ambientLightIntensity.bind(ambientLightSlider.valueProperty());

        EdgePoint currentPoint = new EdgePoint(0, 0, 0, 0);
        los = new LineOfSight(currentPoint, 150);
        
        shadowView.addLoS(los);
    
        // capture mouse position
        centerStackPane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            los.centerPoint.set(new EdgePoint(0, e.getX(), e.getY(), 0));
        });  
        
//        shadowView.shadowColor.bind(shadowColorPicker.valueProperty());
        shadowView.lightColor.bind(lightColorPicker.valueProperty());
        shadowView.intersectionColor.bind(intersectionsColorPicker.valueProperty());
        shadowView.wireframeColor.bind(wireframeColorPicker.valueProperty());
        
        shadowView.shadowEnabled.bind(shadowCheckBox.selectedProperty());
        shadowView.lightEnabled.bind(lightCheckBox.selectedProperty());
        shadowView.intersectionEnabled.bind(intersectionsCheckBox.selectedProperty());
        shadowView.wireframeEnabled.bind(wireframeCheckBox.selectedProperty());
        
    }    
}
