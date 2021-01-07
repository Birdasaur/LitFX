package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import lit.litfx.core.FireView;

/**
 * FXML Controller class
 *
 * @author Birdasaur
 */
public class InfernoGaloreDemoController implements Initializable {
    @FXML
    Pane centerPane;
    @FXML
    TabPane tabPane;
    @FXML
    ToggleGroup flameTypeToggleGroup;
    @FXML
    Circle circleButton;
    
    //Dynamics
    @FXML
    Slider animationDelaySlider;
    @FXML
    Slider convolutionDelaySlider;
    @FXML
    Slider opacitySlider;
    @FXML
    RadioButton classicRB;    
    @FXML
    RadioButton wavesRB;    
    @FXML
    RadioButton pixelWriterRB;    
    @FXML
    RadioButton pixelBufferRB;    
    @FXML
    ChoiceBox<Integer> shift1ChoiceBox;
    @FXML
    ChoiceBox<Integer> shift2ChoiceBox;    
    @FXML
    ChoiceBox<Integer> shift3ChoiceBox;    

    FireView fireView;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        circleButton.setOnMouseClicked(eh->toggleFire());
        //assign the Region you want to engulf in shadows
        fireView = new FireView(centerPane);
        centerPane.getChildren().add(fireView);
        
        fireView.classic.bind(classicRB.selectedProperty());
        fireView.convolutionSleepTime.bind(convolutionDelaySlider.valueProperty());
        fireView.animationDelayTime.bind(animationDelaySlider.valueProperty());
        fireView.flameOpacity.bind(opacitySlider.valueProperty());
        
        shift1ChoiceBox.setItems(
            FXCollections.observableArrayList(
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        shift1ChoiceBox.setOnAction(event -> fireView.setShift1(shift1ChoiceBox.getValue()));
        shift1ChoiceBox.getSelectionModel().select(17);
        
        shift2ChoiceBox.setItems(
            FXCollections.observableArrayList(
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        shift2ChoiceBox.setOnAction(event -> fireView.setShift2(shift2ChoiceBox.getValue()));
        shift2ChoiceBox.getSelectionModel().select(9);
        
        shift3ChoiceBox.setItems(
            FXCollections.observableArrayList(
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        shift3ChoiceBox.setOnAction(event -> fireView.setShift3(shift3ChoiceBox.getValue()));
        shift3ChoiceBox.getSelectionModel().select(1);
        
//       centerPane.getChildren().addAll(be1, be2);
        
        centerPane.setOnMouseClicked((MouseEvent event) -> {
//            if(event.getButton() == MouseButton.PRIMARY)
//                start = new Point2D(event.getX(), event.getY());
//            else if(event.getButton() == MouseButton.SECONDARY)
//                end = new Point2D(event.getX(), event.getY());
        });
        tabPane.setOnMouseEntered(event -> tabPane.requestFocus());
    }    
    
    public void toggleFire() {
        if(fireView.animating.get())
            fireView.stop();
        else
            fireView.start();
    }
}
