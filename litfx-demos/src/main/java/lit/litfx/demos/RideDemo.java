package lit.litfx.demos;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Birdasaur
 */
public class RideDemo extends Application {
    private Scene scene;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RideDemo.fxml"));
        Parent parent = loader.load();        
        scene = new Scene(parent, 800, 800, Color.BLACK);
        String CSS = getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(CSS);  
        
        primaryStage.setTitle("Ride the lightning");
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