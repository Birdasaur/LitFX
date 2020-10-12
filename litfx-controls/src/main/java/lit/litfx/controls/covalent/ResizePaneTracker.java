package lit.litfx.controls.covalent;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
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
    Pane pane;
    
    ObjectProperty<Point2D> anchorStageXYCoordValue = new SimpleObjectProperty<>();
    //ObjectProperty<Point2D> stageXYCoordValue = new SimpleObjectProperty<>();

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
    SimpleObjectProperty<RESIZE_DIRECTION> currentResizeDirection = new SimpleObjectProperty<>(RESIZE_DIRECTION.NONE);
    IntegerProperty currentSegmentIndex = new SimpleIntegerProperty(-1);

    public ResizePaneTracker(Pane pane) {
        this.pane = pane;
        
        // Debug output when bindXToWidth property is changing
        pane.widthProperty().addListener( l -> {
            System.out.println("bindXToWidth = " + pane.getWidth());
        });

        // Debug output when xTo property is changing
        pane.translateXProperty().addListener( l -> {
            System.out.println("xProperty = " + pane.getTranslateX());
        });

        // Change stage's bindXToWidth
        resizeWidthValue.addListener( obs -> {
            System.out.println("setting Pane bindXToWidth: " + resizeWidthValue.get());
            //this.stage.setWidth(resizeWidthValue.get());
            this.pane.setMinWidth(resizeWidthValue.get());
            this.pane.setPrefWidth(resizeWidthValue.get());
            this.pane.setMaxWidth(resizeWidthValue.get());
        });

        // Change stage's bindYToHeight
        resizeHeightValue.addListener( obs -> {
            System.out.println("setting Pane bindYToHeight: " + resizeHeightValue.get());
//            this.pane.setHeight(resizeHeightValue.get());
            this.pane.setMinHeight(resizeWidthValue.get());
            this.pane.setPrefHeight(resizeWidthValue.get());            
            this.pane.setMaxHeight(resizeWidthValue.get());            
        });

        // Change stage's upper left corner's X
        paneXCoordValue.addListener(obs -> {
            System.out.println("1 paneXCoordValue " + paneXCoordValue.get() + " paneX = " + this.pane.getTranslateX());
            this.pane.setTranslateX(paneXCoordValue.get());
            System.out.println("2 paneXCoordValue " + paneXCoordValue.get() + " paneX = " + this.pane.getTranslateX());
            //this.stage.setX(stageXCoordValue.get());
        });

        // Change pane's upper left corner's Y
        paneYCoordValue.addListener(obs -> {
            this.pane.setTranslateY(paneYCoordValue.get());
        });

//        stageXYCoordValue.addListener(obs -> {
//            System.out.println("setting stage left xTo, top yTo: " + stageXYCoordValue.get());
//            //this.stage.setX(stageXYCoordValue.get().getX() - 10);
//            this.stage.setX(stageXYCoordValue.get().getX());
//            this.stage.setY(stageXYCoordValue.get().getY());
//        });


        // Listener to drag window start (mouse press)
        this.pane.setOnMousePressed(mouseEvent ->
            this.mousePressed.pressed(mouseEvent, this)
        );

        // Listener to drag window dragged (mouse dragged)
        this.pane.setOnMouseDragged(mouseEvent ->{
            this.mouseDragged.dragged(mouseEvent, this);
        });

        // Listener to drag window stop (mouse release)
        this.pane.setOnMouseReleased(mouseEvent ->{
            //resizeWidth.set(0.0);
            System.out.println("Release sx " + mouseEvent.getSceneX());
        });

    }

    // ================================= SET-UP MOUSE EVENTS ==========================

    public void setOnMousePressed(PaneMousePressed mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void setOnMouseDragged(PaneMouseDragged mouseDragged) {
        this.mouseDragged = mouseDragged;
    }

}
