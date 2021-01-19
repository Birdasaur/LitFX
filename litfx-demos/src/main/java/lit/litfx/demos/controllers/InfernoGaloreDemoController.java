package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lit.litfx.core.FireView;
import lit.litfx.core.components.fire.Ember;

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
    ToggleGroup flameConvolutionToggleGroup;
    
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
    RadioButton serialRB;    
    @FXML
    RadioButton parallelRB;    

    @FXML
    ChoiceBox<Integer> shift1ChoiceBox;
    @FXML
    ChoiceBox<Integer> shift2ChoiceBox;    
    @FXML
    ChoiceBox<Integer> shift3ChoiceBox;    

    @FXML
    ToggleButton infernoToggleButton;
    
    FireView fireView;
    Point2D start, end;
    boolean dragStarted;
    ArrayList<Point2D> pointList;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        infernoToggleButton.selectedProperty().addListener(cl -> toggleFire());
        //assign the Region you want to engulf in shadows
        fireView = new FireView(centerPane);
        centerPane.getChildren().add(fireView);
        fireView.classic.bind(classicRB.selectedProperty());
        fireView.serialConvolve.bind(serialRB.selectedProperty());
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
        
        pointList = new ArrayList<>();
        centerPane.setOnMousePressed((MouseEvent event) -> {
            //System.out.println("Mouse pressed on centerPane...");
            if(event.getButton() == MouseButton.PRIMARY) {
                start = new Point2D(event.getX(), event.getY());
                dragStarted = true;
                pointList.clear();
                pointList = new ArrayList<>();
                pointList.add(new Point2D(event.getX(), event.getY()));
            }
        });
        
        centerPane.setOnMouseDragged((MouseEvent event) -> {
            //System.out.println("Mouse DRAGGED on centerPane...");
            if(dragStarted) {
                pointList.add(new Point2D(event.getX(), event.getY()));
            }
        });
        
        centerPane.setOnMouseReleased((MouseEvent event) -> {
            if(dragStarted) {
                end = new Point2D(event.getX(), event.getY());
                dragStarted = false;
                ArrayList<Color> colors = new ArrayList<>();
                System.out.println("Point List From Drag: " + pointList.size());
                pointList.forEach(point -> { 
                    colors.add(Color.WHITESMOKE);
//                    Platform.runLater(() ->
//                        centerPane.getChildren().add(
//                            new Circle(point.getX(), point.getY(), 1, Color.WHITESMOKE))
//                    );
                });
                ArrayList<Point2D> points = new ArrayList<>();
                points.addAll(pointList);
                Ember ember = new Ember(points, colors);
                fireView.addEmber(ember);
            }
        });

        tabPane.setOnMouseEntered(event -> tabPane.requestFocus());
    }    
    public void clearEmbers() {
        fireView.clearEmberFlag.set(true);
    }
    public void toggleFire() {
        if(fireView.animating.get())
            fireView.stop();
        else
            fireView.start();
    }
}
