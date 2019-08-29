package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import lit.litfx.core.NodeTools;
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
    Slider ambientLightSlider;    

//    @FXML
//    ColorPicker chainLightningColorPicker;
//    @FXML
//    CheckBox repeatCheckBox;
    
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
        los = new LineOfSight(currentPoint, 50);
        
        shadowView.addLoS(los);
    
        // capture mouse position
        centerStackPane.addEventFilter(MouseEvent.ANY, e -> {
            los.centerPoint.set(new EdgePoint(0, e.getX(), e.getY(), 0));
            shadowView.refreshView();
        });        
    }    
}
