package lit.litfx.controls.covalent;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;

public class MainContentViewArea extends AnchorPane {
    private Path mainContentInnerPath;
    private Pane mainContentPane;

    public Path getMainContentInnerPath() {
        return mainContentInnerPath;
    }

    public void setMainContentInnerPath(Path mainContentInnerPath) {
        this.mainContentInnerPath = mainContentInnerPath;
    }

    public Pane getMainContentPane() {
        return mainContentPane;
    }

    public void setMainContentPane(Pane mainContentPane) {
        this.mainContentPane = mainContentPane;
    }
}
