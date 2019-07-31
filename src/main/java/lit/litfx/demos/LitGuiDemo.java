package lit.litfx.demos;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.util.Duration;
import lit.litfx.Algorithms;
import lit.litfx.Bolt;

/**
 *
 * @author phillsm1
 */
public class LitGuiDemo extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        StackPane centerStack = new StackPane();
        BorderPane root = new BorderPane(centerStack);
        root.setBackground(Background.EMPTY);
        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button 2");
        button1.setOnAction(action -> {
            centerStack.getChildren().removeIf(node -> node instanceof Bolt);
            Bolt bolt = Algorithms.litBounds(button1, button2);
            if(null != bolt) {
                centerStack.getChildren().add(bolt);
                bolt.setVisibleLength(0);
                bolt.animate(Duration.millis(100));
            }            
        });
        HBox hbox = new HBox(400, button1, button2);
        hbox.setAlignment(Pos.CENTER);
        centerStack.getChildren().add(hbox);    
        Scene scene = new Scene(root, 600, 600, Color.BLACK);
        primaryStage.setTitle("Click to fire bolt of lightning.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}