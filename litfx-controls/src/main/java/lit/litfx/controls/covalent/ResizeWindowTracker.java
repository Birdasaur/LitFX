package lit.litfx.controls.covalent;

import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
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
public class ResizeWindowTracker {
    Stage stage;

    ObjectProperty<Point2D> anchorStageXYCoordValue = new SimpleObjectProperty<>();
    //ObjectProperty<Point2D> stageXYCoordValue = new SimpleObjectProperty<>();

    // Stage's xTo and yTo current upper left corner location
    DoubleProperty stageXCoordValue = new SimpleDoubleProperty(-1);
    DoubleProperty stageYCoordValue = new SimpleDoubleProperty();

    ObjectProperty<Point2D> anchorCoordValue = new SimpleObjectProperty<>();
    DoubleProperty anchorWidthSizeValue = new SimpleDoubleProperty();
    DoubleProperty anchorHeightSizeValue = new SimpleDoubleProperty();
    DoubleProperty resizeWidthValue = new SimpleDoubleProperty();
    DoubleProperty resizeHeightValue = new SimpleDoubleProperty();
    MousePressed mousePressed;
    MouseDragged mouseDragged;
    SimpleObjectProperty<RESIZE_DIRECTION> currentResizeDirection = new SimpleObjectProperty<>(RESIZE_DIRECTION.NONE);
    IntegerProperty currentSegmentIndex = new SimpleIntegerProperty(-1);

    public ResizeWindowTracker(Stage stage) {
        this.stage = stage;

        // Debug output when bindXToWidth property is changing
        stage.widthProperty().addListener( l -> {
            System.out.println("bindXToWidth = " + stage.getWidth());
        });

        // Debug output when xTo property is changing
        stage.xProperty().addListener( l -> {
            System.out.println("xProperty = " + stage.getX());
        });

        // Change stage's bindXToWidth
        resizeWidthValue.addListener( obs -> {
            System.out.println("setting stage bindXToWidth: " + resizeWidthValue.get());
            //this.stage.setWidth(resizeWidthValue.get());
            this.stage.setWidth(resizeWidthValue.get());
        });

        // Change stage's bindYToHeight
        resizeHeightValue.addListener( obs -> {
            System.out.println("setting stage bindYToHeight: " + resizeHeightValue.get());
            this.stage.setHeight(resizeHeightValue.get());
        });

        // Change stage's upper left corner's X
        stageXCoordValue.addListener(obs -> {
            System.out.println("1 stageXCoordValue " + stageXCoordValue.get() + " stageX = " + this.stage.getX());
            this.stage.setX(stageXCoordValue.get());
            System.out.println("2 stageXCoordValue " + stageXCoordValue.get() + " stageX = " + this.stage.getX());
            //this.stage.setX(stageXCoordValue.get());
        });

        // Change stage's upper left corner's Y
        stageYCoordValue.addListener(obs -> {
            this.stage.setY(stageYCoordValue.get());
        });

//        stageXYCoordValue.addListener(obs -> {
//            System.out.println("setting stage left xTo, top yTo: " + stageXYCoordValue.get());
//            //this.stage.setX(stageXYCoordValue.get().getX() - 10);
//            this.stage.setX(stageXYCoordValue.get().getX());
//            this.stage.setY(stageXYCoordValue.get().getY());
//        });


        // Listener to drag window start (mouse press)
        this.stage.getScene().setOnMousePressed(mouseEvent ->
            this.mousePressed.pressed(mouseEvent, this)
        );

        // Listener to drag window dragged (mouse dragged)
        this.stage.getScene().setOnMouseDragged(mouseEvent ->{
            this.mouseDragged.dragged(mouseEvent, this);
        });

        // Listener to drag window stop (mouse release)
        this.stage.getScene().setOnMouseReleased(mouseEvent ->{
            //resizeWidth.set(0.0);
            System.out.println("Release sx " + mouseEvent.getSceneX());
        });

    }

    // ================================= SET-UP MOUSE EVENTS ==========================

    public void setOnMousePressed(MousePressed mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void setOnMouseDragged(MouseDragged mouseDragged) {
        this.mouseDragged = mouseDragged;
    }

}
