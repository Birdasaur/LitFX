package lit.litfx.demos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
        Scene scene = new Scene(root, 1200, 800, Color.BLACK);
        newPaneButton.setOnAction(e -> {
            Text text = new Text("Some Styled Content.");
            text.setFont(new Font("Consolas Bold", 26));
            text.setFill(Color.GREEN);
            Button clickHere = new Button("click able stuff");
            TextField tf = new TextField("Type Social Security number here.");
            VBox vbox = new VBox(10, text, tf, clickHere);
            vbox.setAlignment(Pos.CENTER_LEFT);
            vbox.setFillWidth(true);
            BorderPane someContentPane = new BorderPane(new StackPane(vbox));
            someContentPane.setMinSize(400, 200);
            someContentPane.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            PathPane newPane = new PathPane(scene, someContentPane);
            stackPane.getChildren().add(newPane);
            newPane.show();
        });
        //Make the view look pretty
        String CSS = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(CSS);
        CSS = this.getClass().getResource("covalent.css").toExternalForm();
        scene.getStylesheets().add(CSS);
        
        stage.setScene(scene);
        stage.show();        
        
    }
    public static void main(String[] args) {
        launch();
    }
}



