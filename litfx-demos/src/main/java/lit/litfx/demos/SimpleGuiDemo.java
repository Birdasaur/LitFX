package lit.litfx.demos;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lit.litfx.core.NodeTools;
import lit.litfx.core.components.Bolt;
import lit.litfx.core.components.BoltDynamics;

/**
 *
 * @author phillsm1
 */
public class SimpleGuiDemo extends Application {

    double animationDuration = 200;
        
    @Override
    public void start(Stage primaryStage) {
        Pane litPane = new Pane();
        litPane.setMouseTransparent(true);

        ChoiceBox<String> choices = new ChoiceBox<>();
        choices.getItems().addAll("JavaFX", "Choice", "Box");
        choices.getSelectionModel().selectFirst();

        Button button1 = new Button("Arc to Button 2");
        Button button2 = new Button("Arc to ChoiceBox");
        Button button3 = new Button("Arc to ChoiceBox");
        //0.1, 80, 15, 0.85, 0.1);
        final BoltDynamics boltDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 80; dynamics.jitter = 15;
            dynamics.envelopeSize = 0.85; dynamics.envelopeScalar = 0.1;
        }).build();
        
        button1.setOnAction(action -> {
            litPane.getChildren().removeIf(node -> node instanceof Bolt);
            Bolt bolt = NodeTools.arcNodes(button1, button2, boltDynamics);
            setBoltEffects(bolt);
            litPane.getChildren().add(bolt);
            //System.out.println("First Point: " + bolt.getPoints().get(0) + ", " + bolt.getPoints().get(1));
            bolt.setVisibleLength(0);
            bolt.animate(Duration.millis(animationDuration));
        });
        button2.setOnAction(action -> {
            litPane.getChildren().removeIf(node -> node instanceof Bolt);
            Bolt bolt = NodeTools.arcNodes(button2, choices, boltDynamics);
            setBoltEffects(bolt);
            litPane.getChildren().add(bolt);
            //System.out.println("First Point: " + bolt.getPoints().get(0) + ", " + bolt.getPoints().get(1));
            bolt.setVisibleLength(0);
            bolt.animate(Duration.millis(animationDuration));
        });
        
        button3.setOnAction(action -> {
            litPane.getChildren().removeIf(node -> node instanceof Bolt);
            Bolt bolt = NodeTools.arcNodes(button3, choices, boltDynamics);
            setBoltEffects(bolt);
            litPane.getChildren().add(bolt);
            //System.out.println("First Point: " + bolt.getPoints().get(0) + ", " + bolt.getPoints().get(1));
            bolt.setVisibleLength(0);
            bolt.animate(Duration.millis(animationDuration));
        });        

        litPane.widthProperty().addListener((obs, oV, nV) -> {
            litPane.getChildren().removeIf(node -> node instanceof Bolt);            
        });
        litPane.heightProperty().addListener(event -> {
            litPane.getChildren().removeIf(node -> node instanceof Bolt);            
        });        
        
        HBox hbox1 = new HBox(400, button1, button2);
        hbox1.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox(400, choices, button3);
        hbox2.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(200, hbox1, hbox2);
        vbox.setAlignment(Pos.CENTER);
        
        BorderPane litBP = new BorderPane(litPane);
        litBP.setBackground(Background.EMPTY);
        litBP.setMouseTransparent(true);
        StackPane centerStack = new StackPane(vbox, litBP);
        
        BorderPane root = new BorderPane(centerStack);
        root.setBackground(Background.EMPTY);
        Scene scene = new Scene(root, 600, 600, Color.BLACK);

        primaryStage.setTitle("Click a button to arc between nodes.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setBoltEffects(Bolt bolt) {
        bolt.setStroke(Color.ALICEBLUE);
        bolt.setOpacity(0.75);
        bolt.setStrokeWidth(4.0);
        SepiaTone st = new SepiaTone(0.25);
        Bloom bloom = new Bloom(0.25);
        bloom.setInput(st);
        Glow glow = new Glow(0.75);
        glow.setInput(bloom);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.CORNSILK, 10, 0.5, 0, 0);
        shadow.setInput(glow);
        shadow.setRadius(60.0);
        bolt.setEffect(shadow);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}