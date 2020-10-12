package lit.litfx.controls.covalent;

import javafx.scene.input.MouseEvent;

/**
 *
 * @author phillsm1
 */
public interface ResizeTracker {
    public void pressed(MouseEvent mouseEvent, ResizeTracker tracker);
    public void dragged(MouseEvent mouseEvent, ResizeTracker tracker);
}
