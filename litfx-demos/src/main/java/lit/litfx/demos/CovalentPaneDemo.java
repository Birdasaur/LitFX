package lit.litfx.demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lit.litfx.controls.covalent.PathPane;
import lit.litfx.controls.covalent.events.CovalentPaneEvent;

public class CovalentPaneDemo extends Application {
    Pane desktopPane;
    StackPane stackPane;
    CheckBox contentPaneCheckBox;
    CheckBox windowButtonsCheckBox;
    CheckBox mainTitleAreaCheckBox;
    CheckBox leftAccentCheckBox;
    CheckBox leftTabCheckBox;
    CheckBox outerFrameCheckBox;
    CheckBox mainContentBorderFrameCheckBox;
        
    @Override
    public void start(Stage stage) {
        Background transBack = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
        BorderPane root = new BorderPane();
        Button newPaneButton = new Button("Create Pane");
        Spinner<Double> borderTimeSpinner = new Spinner(100.0, 1000.0, 500.0, 50.0);
        Spinner<Double> contentTimeSpinner = new Spinner(100.0, 1000.0, 500.0, 50.0);
        Button closePanesButton = new Button("Close Panes");
        HBox hbox = new HBox(5, newPaneButton, borderTimeSpinner, contentTimeSpinner, closePanesButton);

        contentPaneCheckBox = new CheckBox("Content Pane");
        contentPaneCheckBox.setOnAction(e -> updateVisibilities());
        windowButtonsCheckBox = new CheckBox("windowButtons");
        windowButtonsCheckBox.setOnAction(e -> updateVisibilities());
        mainTitleAreaCheckBox = new CheckBox("mainTitleArea");
        mainTitleAreaCheckBox.setOnAction(e -> updateVisibilities());
        leftAccentCheckBox = new CheckBox("leftAccent");
        leftAccentCheckBox.setOnAction(e -> updateVisibilities());
        leftTabCheckBox = new CheckBox("leftTab");
        leftTabCheckBox.setOnAction(e -> updateVisibilities());
        outerFrameCheckBox = new CheckBox("outerFrame");
        outerFrameCheckBox.setOnAction(e -> updateVisibilities());
        mainContentBorderFrameCheckBox = new CheckBox("mainContentBorderFrame");
        mainContentBorderFrameCheckBox.setOnAction(e -> updateVisibilities());
        HBox hbox2 = new HBox(20, contentPaneCheckBox, windowButtonsCheckBox, 
            mainTitleAreaCheckBox, leftAccentCheckBox, leftTabCheckBox,
            outerFrameCheckBox, mainContentBorderFrameCheckBox);
        hbox2.getChildren().forEach(t -> ((CheckBox) t).setSelected(true));
        
        stackPane = new StackPane();

        // A pane to add path windows to be positioned with absolute positioning.
        desktopPane = new Pane();
        stackPane.getChildren().add(desktopPane);

        root.setCenter(stackPane);
        root.setTop(new VBox(10, hbox, hbox2));
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

            PathPane newPane = new PathPane(scene,
                    640,480,
                    someContentPane,
                "Cyber Battlespace", "Notifications",
                borderTimeSpinner.getValue(),
                contentTimeSpinner.getValue());
            desktopPane.getChildren().add(newPane);
            newPane.show();
        });
        
        closePanesButton.setOnAction(e -> {
            desktopPane.getChildren().clear();
        });
        scene.getRoot().addEventHandler(CovalentPaneEvent.COVALENT_PANE_CLOSE, e -> {
            desktopPane.getChildren().remove(e.pathPane);
        });
        //Make the view look pretty
        String CSS = this.getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(CSS);
        CSS = this.getClass().getResource("covalent.css").toExternalForm();
        scene.getStylesheets().add(CSS);
        
        stage.setScene(scene);
        stage.show();        
        
    }
    private void updateVisibilities() {
        desktopPane.getChildren().filtered(t -> t instanceof PathPane).forEach(p -> {
            PathPane pane = (PathPane) p;
            pane.contentPane.setVisible(contentPaneCheckBox.isSelected());
            pane.outerFrame.setVisible(outerFrameCheckBox.isSelected());
            pane.mainContentBorderFrame.setVisible(mainContentBorderFrameCheckBox.isSelected());
            pane.leftAccent.setVisible(leftAccentCheckBox.isSelected());
            pane.leftTab.setVisible(leftTabCheckBox.isSelected());
            pane.windowButtons.setVisible(windowButtonsCheckBox.isSelected());
            pane.mainTitleArea.setVisible(mainTitleAreaCheckBox.isSelected());
        });
    }
    public static void main(String[] args) {
        launch();
    }
}



