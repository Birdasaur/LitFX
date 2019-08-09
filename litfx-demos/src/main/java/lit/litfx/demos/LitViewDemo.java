package lit.litfx.demos;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
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
import lit.litfx.core.LitView;
import lit.litfx.core.components.BoltDynamics;

/**
 *
 * @author phillsm1
 */
public class LitViewDemo extends Application {

    double animationDuration = 100;
    boolean forwardArc = false;
    boolean backwardArc = false;
    ImageView imageView;
    Button button1;
    Button button2;    
    RadioButton singleBolt;
    RadioButton chainLightning;
    CheckBox autoUpdate;
    Slider updateRateSlider;
    BoltDynamics boltDynamics;
    BoltDynamics loopDynamics;
    LitView litView;
    
        
    @Override
    public void start(Stage primaryStage) {
        Image image = new Image(getClass().getResourceAsStream("pjbolt_dark.png"));
        imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        
        button1 = new Button("Arc forward to ImageView");
        button2 = new Button("Arc back to ImageView");
        
        HBox topLeftHBox = new HBox(button1);
        topLeftHBox.setAlignment(Pos.CENTER_LEFT);
        HBox topRightHBox = new HBox(button2);
        topRightHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox topHBox = new HBox(800, topLeftHBox, topRightHBox);
        topHBox.setAlignment(Pos.CENTER);
        
        HBox middleHBox = new HBox(imageView);
        middleHBox.setAlignment(Pos.CENTER);
 
        singleBolt = new RadioButton("Single Bolts");
        singleBolt.setSelected(true);
        chainLightning = new RadioButton("Chain Lightning");
        ToggleGroup tg = new ToggleGroup();
        singleBolt.setToggleGroup(tg);
        chainLightning.setToggleGroup(tg);

        boltDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 80; dynamics.jitter = 20;
            dynamics.envelopeSize = 0.95; dynamics.envelopeScalar = 0.1;
        }).build();
        loopDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 15; dynamics.jitter = 5;
            dynamics.envelopeSize = 0.75; dynamics.envelopeScalar = 0.1;
            dynamics.loopStartNode = true; dynamics.loopEndNode = true;
        }).build();
        
        CheckBox loopBack = new CheckBox("Loop Start Node");
        loopBack.disableProperty().bind(chainLightning.selectedProperty().not());
        loopBack.selectedProperty().addListener(event -> {
            boltDynamics.loopStartNode = loopBack.isSelected();
            loopDynamics.loopStartNode = loopBack.isSelected();
        });
        CheckBox loopForward = new CheckBox("Loop End Node");
        loopForward.disableProperty().bind(chainLightning.selectedProperty().not());
        loopForward.selectedProperty().addListener(event -> {
            boltDynamics.loopStartNode = loopForward.isSelected();
            loopDynamics.loopStartNode = loopForward.isSelected();
        });
        
        Button clearAll = new Button("Clear All");
        
        HBox bottomHBox = new HBox(50, singleBolt, chainLightning, loopBack, loopForward, clearAll);
        bottomHBox.setAlignment(Pos.CENTER);
        
        autoUpdate = new CheckBox("Auto Update");
        updateRateSlider = new Slider(1, 100, 50);
        updateRateSlider.setShowTickMarks(true);
        updateRateSlider.setShowTickLabels(true);
        updateRateSlider.setMajorTickUnit(10);
        updateRateSlider.setBlockIncrement(10); 
        updateRateSlider.setSnapToTicks(true);
        updateRateSlider.setPrefSize(500, 40);
        
        HBox updatesHBox = new HBox(50, autoUpdate, updateRateSlider );
        updatesHBox.setAlignment(Pos.CENTER);
        
        
        VBox vbox = new VBox(100, topHBox, middleHBox, bottomHBox, updatesHBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);

        litView = new LitView(vbox);
        button1.setOnAction(action -> {
            forwardArc();
            forwardArc = true;
        });
        button2.setOnAction(action -> {
            backwardArc();
            backwardArc = true;
        });
        clearAll.setOnAction(event -> {
            litView.clearAll();
            forwardArc = false;
            backwardArc = false;
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
        
        initAnimation();
    }

    private void backwardArc() {
        if(singleBolt.isSelected())
            litView.arcNodes(button2, imageView, boltDynamics);
        else {
            ArrayList<Node> nodes = new ArrayList<>();
            nodes.add(button2);
            nodes.add(imageView);
            nodes.add(button1);
            litView.chainNodes(nodes, boltDynamics, loopDynamics);
        }        
    }
    private void forwardArc() {
        System.out.println("started forwardArc()");
        if(singleBolt.isSelected()) {
            System.out.println("attempting arc...");
            litView.arcNodes(button1, imageView, boltDynamics);
        }
        else {
            ArrayList<Node> nodes = new ArrayList<>();
            nodes.add(button1);
            nodes.add(imageView);
            nodes.add(button2);
            System.out.println("attempting chain...");
            litView.chainNodes(nodes, boltDynamics, loopDynamics);
        }     
        System.out.println("finished forwardArc()");
    }
    
    private void initAnimation() {
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(!this.isCancelled() && !this.isDone()) {
                    if(autoUpdate.isSelected()) {
                        if(forwardArc)
                            Platform.runLater(()->forwardArc());
                        if(backwardArc)
                            Platform.runLater(()->backwardArc());
                    }
                    System.out.println("animation...");
                    Thread.sleep(updateRateSlider.valueProperty().longValue());
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}