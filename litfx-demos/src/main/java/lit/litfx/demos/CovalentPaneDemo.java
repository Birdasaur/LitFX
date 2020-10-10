package lit.litfx.demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lit.litfx.controls.covalent.PathPane;

public class CovalentPaneDemo extends Application {
    @Override
    public void start(Stage stage) {
        Background transBack = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();
        Button newPaneButton = new Button("Create Pane");
        
        HBox hbox = new HBox(5, newPaneButton);
        StackPane stackPane = new StackPane();
        root.setCenter(stackPane);
        root.setTop(hbox);
        root.setBackground(transBack);
        Scene scene = new Scene(root, 1200, 800, Color.ALICEBLUE);
        newPaneButton.setOnAction(e -> {
            AnchorPane somePane = new AnchorPane(new Text("Content"));
            BorderPane someContentPane = new BorderPane(somePane);
            someContentPane.setMinSize(200, 200);
            someContentPane.setBackground(new Background(
                new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            PathPane newPane = new PathPane(scene, someContentPane);
            stackPane.getChildren().add(newPane);
            newPane.show();
        });

        stage.setScene(scene);
        stage.show();        
        
    }
    public static void main(String[] args) {
        launch();
    }
}



