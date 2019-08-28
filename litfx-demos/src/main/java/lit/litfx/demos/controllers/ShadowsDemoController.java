package lit.litfx.demos.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import lit.litfx.core.LitView;
import lit.litfx.core.NodeTools;
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
    
    LitView litView;
    ArrayList<Node> centerChildren;     
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        //assign the Region you want to engulf in shadows
//        litView = new LitView(centerPane);
//        
//        //If you already have a nice stackpane, just add your LitView last
//        centerStackPane.getChildren().add(litView);

        //get the Node children in traversal order
        centerChildren = new ArrayList<>();
        //This way is ... pretty close... but JavaFX currently does not publically
        //expose a method or means to find the true focus traversable order. :-(
        centerChildren.addAll(
            NodeTools.getAllChildren(centerPane).stream()
                .filter(node -> node.isFocusTraversable())
                .collect(toList()));
        
        EdgePoint currentPoint = new EdgePoint(0, 0, 0, 0);
        LineOfSight los = new LineOfSight(currentPoint, 50);
        
        List<Line> nodeLines = centerChildren.stream()
            .map(node -> NodeTools.boundsToLines(node))
            .flatMap(Collection::stream)
            .collect(toList());
        System.out.println("Node Lines count: " + nodeLines.size()); 
        
        // capture mouse position
        centerStackPane.addEventFilter(MouseEvent.ANY, e -> {
            los.centerPoint = new EdgePoint(0, e.getX(), e.getY(), 0);
            List<Line> scanLines = los.createScanLines(0, 360, 1);
            System.out.println("Scan Lines count: " + scanLines.size());
            List<EdgePoint> intersections = los.getIntersectionPoints(scanLines, nodeLines);
            System.out.println("Intersections In Range: " + intersections.size());
        });        
        
        
        //start the arc redraw thread
//        initAnimationTask();
    }    
    
//    private void initAnimationTask() {
//        Task animationTask = new Task() {
//            @Override
//            protected Void call() throws Exception {
//                while(!this.isCancelled() && !this.isDone()) {
//                    if(repeatCheckBox.isSelected()) {
//                        //check if the current chain lightning is finished
//                        if(null == currentChain || !currentChain.isAnimating() )
//                            rideTheLightning();
//                    }
//                    System.out.println("riding the lightning...");
//                    Thread.sleep(timeDelayProp.get());
//                }
//                return null;
//            }
//        };
//        Thread animationThread = new Thread(animationTask);
//        animationThread.setDaemon(true);
//        animationThread.start();        
//    }

}
