package lit.litfx.demos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author cpdea
 */
public class AlternateAnimatedWavesDemo extends Application {
    private Scene scene;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AlternateAnimatedWavesDemo.fxml"));
        Parent parent = loader.load();
        scene = new Scene(parent, Color.BLACK);
        String CSS = getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(CSS);  
        
        primaryStage.setTitle("FX Alternate Animated Waves!");
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