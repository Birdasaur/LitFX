package lit.litfx.demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lit.litfx.NodeTools;
import lit.litfx.components.Bolt;

/**
 *
 * @author phillsm1
 */
public class LitGuiDemo extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Pane litPane = new Pane();
        litPane.setMouseTransparent(true);
        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button 2");
        button1.setOnAction(action -> {
            litPane.getChildren().removeIf(node -> node instanceof Bolt);
            Bolt bolt = NodeTools.litBounds(button1, button2);
            setBoltEffects(bolt);
            litPane.getChildren().add(bolt);
            //System.out.println("First Point: " + bolt.getPoints().get(0) + ", " + bolt.getPoints().get(1));
            bolt.setVisibleLength(0);
            bolt.animate(Duration.millis(100));
        });
        HBox hbox = new HBox(400, button1, button2);
        hbox.setAlignment(Pos.CENTER);

        BorderPane litBP = new BorderPane(litPane);
        litBP.setBackground(Background.EMPTY);
        litBP.setMouseTransparent(true);
        StackPane centerStack = new StackPane(hbox, litBP);
//        centerStack.widthProperty().addListener(event -> {
//            litPane.resize(centerStack.getWidth(), centerStack.getHeight());
//        });
//        centerStack.heightProperty().addListener(event -> {
//            litPane.resize(centerStack.getWidth(), centerStack.getHeight());
//        });
        
        BorderPane root = new BorderPane(centerStack);
        root.setBackground(Background.EMPTY);
        Scene scene = new Scene(root, 600, 600, Color.BLACK);

        primaryStage.setTitle("Click to fire bolt of lightning.");
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