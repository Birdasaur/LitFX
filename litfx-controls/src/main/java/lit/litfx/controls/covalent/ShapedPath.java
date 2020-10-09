package lit.litfx.controls.covalent;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import static lit.litfx.controls.covalent.BindablePointBuilder.pt;

/**
 * A shaped path is able to make path elements that are able to be bound to its parent's width or height.
 * This allows path elements to change when the parent's width or height changes.
 */
public class ShapedPath extends Path {
    private ReadOnlyDoubleProperty parentWidth;
    private ReadOnlyDoubleProperty parentHeight;

    private DoubleProperty previousPointX = null;
    private DoubleProperty previousPointY = null;

    public ShapedPath(Stage stage) {
        parentWidth = stage.widthProperty();
        parentHeight = stage.heightProperty();
    }

    public ShapedPath(Pane pane) {
        parentWidth = pane.widthProperty();
        parentHeight = pane.heightProperty();
    }

    public static ShapedPath framePath(Stage stage) {
        return new ShapedPath(stage);
    }

    public static ShapedPath framePath(Pane pane) {
        return new ShapedPath(pane);
    }

    public double getParentWidth() {
        return parentWidth.get();
    }

    public ReadOnlyDoubleProperty parentWidthProperty() {
        return parentWidth;
    }

    public double getParentHeight() {
        return parentHeight.get();
    }

    public DoubleProperty previousPointXProperty() {
        return previousPointX;
    }

    public DoubleProperty previousPointYProperty() {
        return previousPointY;
    }

    public ReadOnlyDoubleProperty parentHeightProperty() {
        return parentHeight;
    }

    public void moveTo(double x, double y) {
        moveTo(pt(x, y));
    }
    public void moveTo(BindablePoint bindablePoint) {
        MoveTo moveTo = new MoveTo();
        bindPoint(moveTo.xProperty(), moveTo.yProperty(), bindablePoint);
        getElements().add(moveTo);
        setPreviousPointXY(moveTo.xProperty(), moveTo.yProperty());
    }

    private void setPreviousPointXY(DoubleProperty x, DoubleProperty y) {
        this.previousPointX = x;
        this.previousPointY = y;
    }

    private void setPreviousPointXY(LineTo lineTo) {
        this.previousPointX = lineTo.xProperty();
        this.previousPointY = lineTo.yProperty();
    }

    /*
       bind(target, src, offset);
     */

    /**
     * Bind property to parent's width or height property (readonly).
     * @param targetProp x or y coordinate property.
     * @param sourceProp Parent's width or height property.
     * @param offset offset. ie: x binds to width with offset of -10 means width minus 10.
     */
    private void bind(DoubleProperty targetProp,
                      ReadOnlyDoubleProperty sourceProp,
                      double offset) {
        if (offset > 0 || offset < 0) {
            targetProp.bind(Bindings.add(sourceProp, offset));
        } else {
            targetProp.bind(sourceProp);
        }
    }

    private void bindPoint(DoubleProperty xProp, DoubleProperty yProp, BindablePoint bindablePoint) {
        if (bindablePoint.isBindWidth()) {
            bindX(xProp, bindablePoint.getXOrOffset());
        } else if (bindablePoint.isBindPrevX()) {
            bind(xProp, previousPointX, bindablePoint.getXOrOffset());
        } else {
            xProp.set(bindablePoint.getXOrOffset());
        }

        if (bindablePoint.isBindHeight()) {
            bindY(yProp, bindablePoint.getYOrOffset());
        } else if (bindablePoint.isBindPrevY()) {
            bind(yProp, previousPointY, bindablePoint.getYOrOffset());
        } else {
            yProp.set(bindablePoint.getYOrOffset());
        }
    }

    private void bindX(DoubleProperty xProp, double xOrOffset) {
        bind(xProp, parentWidthProperty(), xOrOffset);
    }

    private void bindY(DoubleProperty yProp, double yOrOffset) {
        bind(yProp, parentHeightProperty(), yOrOffset);
    }

    public void horzSeg(BindablePoint bindablePoint) {
        BindablePoint clone = bindablePoint.clone();
        LineTo lineTo = new LineTo(clone.getXOrOffset(), clone.getYOrOffset());
        // take previous y coord
        lineTo.yProperty().bind(previousPointYProperty());
        if (bindablePoint.isBindWidth()) {
            bindX(lineTo.xProperty(), clone.getXOrOffset());
        }

        getElements().add(lineTo);
        setPreviousPointXY(lineTo);
    }

    public void horzSeg(double x) {
        horzSeg(new BindablePoint(x, 0));
    }

    public void vertSeg(BindablePoint bindablePoint) {
        BindablePoint clone = bindablePoint.clone();
        LineTo lineTo = new LineTo(clone.getXOrOffset(), clone.getYOrOffset());
        // take previous x coord
        lineTo.xProperty().bind(previousPointXProperty());
        if (bindablePoint.isBindHeight()) {
            bindY(lineTo.yProperty(), clone.getYOrOffset());
        }
        getElements().add(lineTo);
        setPreviousPointXY(lineTo);
    }
    
    public void vertSeg(double y) {
        vertSeg(new BindablePoint(0, y));
    }

    public void lineSeg(BindablePoint bindablePoint) {
        LineTo lineTo = new LineTo();
        bindPoint(lineTo.xProperty(), lineTo.yProperty(), bindablePoint);
        getElements().add(lineTo);
        setPreviousPointXY(lineTo);
    }

    public void closeSeg() {
        ClosePath closePath = new ClosePath();
        getElements().add(closePath);
    }


}

