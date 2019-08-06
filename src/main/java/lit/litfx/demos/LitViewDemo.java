package lit.litfx.demos;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
 
        VBox vbox = new VBox(200, topHBox, middleHBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);
        
        LitView litView = new LitView(vbox);
        button1.setOnAction(action -> {
            litView.arcNodes(button1, imageView);
        });
        button2.setOnAction(action -> {
            litView.arcNodes(button2, imageView);
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