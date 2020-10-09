package lit.litfx.demos;

import javafx.application.Application;
import javafx.stage.Stage;
import lit.litfx.controls.covalent.PathWindow;

public class CovalentUIDemo extends Application {
    @Override
    public void start(Stage stage) {
        PathWindow scifiWindow = new PathWindow(stage);
        scifiWindow.show();
    }
    public static void main(String[] args) {
        launch();
    }
}



