package lit.litfx.demos;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lit.litfx.LitView;
import lit.litfx.components.BoltDynamics;

/**
 *
 * @author phillsm1
 */
public class LitViewDemo extends Application {

    double animationDuration = 100;
        
    @Override
    public void start(Stage primaryStage) {
        Image image = new Image(getClass().getResourceAsStream("pjbolt_dark.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        
        Button button1 = new Button("Arc forward to ImageView");
        Button button2 = new Button("Arc back to ImageView");
        
        HBox topLeftHBox = new HBox(button1);
        topLeftHBox.setAlignment(Pos.CENTER_LEFT);
        HBox topRightHBox = new HBox(button2);
        topRightHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox topHBox = new HBox(800, topLeftHBox, topRightHBox);
        topHBox.setAlignment(Pos.CENTER);
        
        HBox middleHBox = new HBox(imageView);
        middleHBox.setAlignment(Pos.CENTER);
 
        RadioButton singleBolt = new RadioButton("Single Bolts");
        singleBolt.setSelected(true);
        RadioButton chainLightning = new RadioButton("Chain Lightning");
        ToggleGroup tg = new ToggleGroup();
        singleBolt.setToggleGroup(tg);
        chainLightning.setToggleGroup(tg);
        
        CheckBox loopBack = new CheckBox("Loop Start Node");
        loopBack.disableProperty().bind(chainLightning.selectedProperty().not());
        CheckBox loopForward = new CheckBox("Loop End Node");
        loopForward.disableProperty().bind(chainLightning.selectedProperty().not());
        
        HBox bottomHBox = new HBox(50, singleBolt, chainLightning, loopBack, loopForward);
        bottomHBox.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(200, topHBox, middleHBox, bottomHBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);

        //0.1, 80, 15, 0.85, 0.1);
        BoltDynamics boltDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 80; dynamics.jitter = 30;
            dynamics.envelopeSize = 0.85; dynamics.envelopeScalar = 0.1;
        }).build();
        BoltDynamics loopDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 15; dynamics.jitter = 5;
            dynamics.envelopeSize = 0.75; dynamics.envelopeScalar = 0.1;
            dynamics.loopStartNode = true; dynamics.loopEndNode = true;
        }).build();

        LitView litView = new LitView(vbox);
        button1.setOnAction(action -> {
            if(singleBolt.isSelected())
                litView.arcNodes(button1, imageView, boltDynamics);
            else {
                ArrayList<Node> nodes = new ArrayList<>();
                nodes.add(button1);
                nodes.add(imageView);
                nodes.add(button2);
                litView.chainNodes(nodes, boltDynamics, loopDynamics);
            }
        });
        button2.setOnAction(action -> {
            if(singleBolt.isSelected())
                litView.arcNodes(button2, imageView, boltDynamics);
            else {
                ArrayList<Node> nodes = new ArrayList<>();
                nodes.add(button2);
                nodes.add(imageView);
                nodes.add(button1);
                litView.chainNodes(nodes, boltDynamics, loopDynamics);
            }
        });
        
        StackPane centerStack = new StackPane(vbox, litView);
        
        BorderPane root = new BorderPane(centerStack);
        root.setBackground(Background.EMPTY);
        Scene scene = new Scene(root, 800, 800, Color.BLACK);
        String CSS = getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(CSS);  
        
        primaryStage.setTitle("Click a button to arc between nodes.");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}