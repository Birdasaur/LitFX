package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import lit.litfx.core.LitView;
import lit.litfx.core.NodeTools;
import lit.litfx.core.components.BoltDynamics;

/**
 * FXML Controller class
 *
 * @author phillsm1
 */
public class RideDemoController implements Initializable {

    @FXML
    GridPane centerPane;
    @FXML
    StackPane centerStackPane;
    
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
    
    BoltDynamics boltDynamics;
    BoltDynamics loopDynamics;
    LitView litView;
    ArrayList<Node> centerChildren;     
    ArrayList<Node> reverseCenterChildren;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //assign the Region you want to get lit
        litView = new LitView(centerPane);
        //If you already have a nice stackpane, just add your LitView last
        centerStackPane.getChildren().add(litView);
        //nice dynamics for the bolts that arc between nodes
        boltDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 80; dynamics.jitter = 20;
            dynamics.envelopeSize = 0.95; dynamics.envelopeScalar = 0.1;
        }).build();
        //dynamics for the bolts that travel around the nodes themselves
        loopDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 15; dynamics.jitter = 5;
            dynamics.envelopeSize = 0.75; dynamics.envelopeScalar = 0.1;
            dynamics.loopStartNode = true; dynamics.loopEndNode = true;
        }).build();

        //get the Node children in traversal order
        centerChildren = new ArrayList<>();
        //This way is ... pretty close... but JavaFX currently does not publically
        //expose a method or means to find the true focus traversable order. :-(
        centerChildren.addAll(
            NodeTools.getAllChildren(centerPane).stream()
                .filter(node -> node.isFocusTraversable())
                .collect(toList()));
        //Here is the opposite direction.  
        //Can't use Collections.reverse because Node doesn't implement Comparable
        reverseCenterChildren = new ArrayList<>();
        for(int i=centerChildren.size()-1; i>=0; i--)
            reverseCenterChildren.add(centerChildren.get(i));
        
        clockwiseRadioButton.selectedProperty().addListener(event -> clearAll());
        counterRadioButton.selectedProperty().addListener(event -> clearAll());
        
        //How much time will we wait between arc redraws
        timeDelayProp.bind(delaySlider.valueProperty());
        //start the arc redraw thread
        initAnimationTask();
    }    
    
    @FXML
    public void rideTheLightning() {
        //@TODO will replace the current arc
        if(clockwiseRadioButton.isSelected())
            Platform.runLater(()->forwardArc());
        else
            Platform.runLater(()->backwardArc());
    }
    @FXML
    public void clearAll() {
        litView.clearAll();
    }
    private void initAnimationTask() {
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(!this.isCancelled() && !this.isDone()) {
                    //@TODO check if the current chain lightning is finished

                    if(repeatCheckBox.isSelected()) {
                        rideTheLightning();
                    }
                    System.out.println("animation...");
                    Thread.sleep(timeDelayProp.get());
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();        
    }
    private void forwardArc() {
        System.out.println("started forwardArc()");
        litView.chainNodes((ArrayList<Node>) centerChildren, boltDynamics, loopDynamics);
        System.out.println("finished forwardArc()");
    }    
    private void backwardArc() {
        System.out.println("started backwardArc()");
        litView.chainNodes((ArrayList<Node>) reverseCenterChildren, boltDynamics, loopDynamics);
        System.out.println("finished backwardArc()");
    }    
}
