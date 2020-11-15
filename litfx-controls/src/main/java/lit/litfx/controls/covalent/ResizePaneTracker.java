package lit.litfx.controls.covalent;

import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lit.litfx.controls.covalent.CursorMappings.RESIZE_DIRECTION;

/**
 * This class tracks the window's state. The following items are being maintained:
 * <ul>
 *     <li>The xTo, yTo of the stage's upper left corner coordinate</li>
 *     <li>The bindXToWidth and bindYToHeight of the stage window.</li>
 *     <li>The anchor positions for dragging and moving the stage window.</li>
 *     <li>The current segment index of the PathWindow to update the cursor resize direction.</li>
 * </ul>
 */
public class ResizePaneTracker {

    PathPane pathPane; // a PathPane
    Pane desktopPane; // parent desktop area.

    ObjectProperty<Point2D> anchorPathPaneXYCoordValue = new SimpleObjectProperty<>();

    // Stage's xTo and yTo current upper left corner location
    DoubleProperty paneXCoordValue = new SimpleDoubleProperty(-1);
    DoubleProperty paneYCoordValue = new SimpleDoubleProperty();

    ObjectProperty<Point2D> anchorCoordValue = new SimpleObjectProperty<>();
    DoubleProperty anchorWidthSizeValue = new SimpleDoubleProperty();
    DoubleProperty anchorHeightSizeValue = new SimpleDoubleProperty();
    DoubleProperty resizeWidthValue = new SimpleDoubleProperty();
    DoubleProperty resizeHeightValue = new SimpleDoubleProperty();

    PaneMousePressed mousePressed;
    PaneMouseDragged mouseDragged;
    PaneMouseReleased mouseReleased;

    SimpleObjectProperty<RESIZE_DIRECTION> currentResizeDirection = new SimpleObjectProperty<>(RESIZE_DIRECTION.NONE);
    IntegerProperty currentSegmentIndex = new SimpleIntegerProperty(-1);

    public ResizePaneTracker(PathPane pathPane, Pane desktopPane) {
        this.pathPane = pathPane;
        this.desktopPane = desktopPane;

        // Debug output when bindXToWidth property is changing
//        pathPane.widthProperty().addListener(l -> {
//            System.out.println("bindXToWidth = " + pathPane.getWidth());
//        });

        // Debug output when xTo property is changing
//        pathPane.translateXProperty().addListener(l -> {
//            System.out.println("xProperty = " + pathPane.getTranslateX());
//        });

        // Change stage's bindXToWidth
        resizeWidthValue.addListener( obs -> {
//            System.out.println("setting Pane bindXToWidth: " + resizeWidthValue.get());
            double newWidth = resizeWidthValue.get();
            this.pathPane.setPrefWidth(Math.max(this.pathPane.getMinWidth(), newWidth));
        });

        // Change stage's bindYToHeight
        resizeHeightValue.addListener( obs -> {
//            System.out.println("setting Pane bindYToHeight: " + resizeHeightValue.get());
            double newHeight = resizeHeightValue.get();
            this.pathPane.setPrefHeight(Math.max(this.pathPane.getMinHeight(), newHeight));
        });

        // Change stage's upper left corner's X
        paneXCoordValue.addListener(obs -> {
            this.pathPane.setTranslateX(paneXCoordValue.get());
        });

        // Change pane's upper left corner's Y
        paneYCoordValue.addListener(obs -> {
            this.pathPane.setTranslateY(paneYCoordValue.get());
        });

        // Listener to drag window start (mouse press)
        this.pathPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent ->
            this.mousePressed.pressed(mouseEvent, this)
        );

        // Listener to drag window dragged (mouse dragged)
        this.pathPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent ->
            this.mouseDragged.dragged(mouseEvent, this)
        );

        // Listener to drag window stop (mouse release)
        this.pathPane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent ->{
            this.mouseReleased.released(mouseEvent, this);
        });
    }

    // ================================= SET-UP MOUSE EVENTS ==========================

    public void setOnMousePressed(PaneMousePressed mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void setOnMouseDragged(PaneMouseDragged mouseDragged) {
        this.mouseDragged = mouseDragged;
    }
    public void setOnMouseReleased(PaneMouseReleased mouseReleased) {
        this.mouseReleased = mouseReleased;
    }
}