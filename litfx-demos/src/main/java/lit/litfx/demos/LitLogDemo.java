package lit.litfx.demos;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lit.litfx.controls.output.LitLog;
import lit.litfx.controls.output.LogToolbar;

/**
 *
 * @author Birdasaur
 */
public class LitLogDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        LogToolbar logToolbar = new LogToolbar(5,5);
        TextField tf = new TextField();
        LitLog litLog = new LitLog(5, 10);
        tf.setOnAction(e -> {
            litLog.addLine(tf.getText(), 
                new Font((String)logToolbar.fontChoiceBox.getValue(), 
                         (int)logToolbar.fontSizeSpinner.getValue()), 
                logToolbar.picker.getValue());
            tf.clear();
        });
        litLog.selectingProperty.bind(logToolbar.textToggleButton.selectedProperty());
        
        Background transBack = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();
        root.setCenter(litLog);
        root.setTop(logToolbar);
        root.setBottom(tf);
        root.setBackground(transBack);
        Scene scene = new Scene(root, 600, 400, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();
        //Make the view look pretty
        String CSS = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(CSS);
        tf.requestFocus();
        litLog.addLine("...waiting", 
            new Font((String)logToolbar.fontChoiceBox.getValue(), 
                     (int)logToolbar.fontSizeSpinner.getValue()), 
            logToolbar.picker.getValue());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}