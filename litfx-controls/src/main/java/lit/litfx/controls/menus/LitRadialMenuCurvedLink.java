package lit.litfx.controls.menus;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author phillsm1
 */
public class LitRadialMenuCurvedLink extends CubicCurve {
    
    protected ObjectProperty<Paint> mouseOnStroke;
    protected ObjectProperty<LitRadialMenuItem> item;
    Node source;
    
    public LitRadialMenuCurvedLink(Node source, LitRadialMenuItem item, 
        Paint stroke, Paint mouseOnStroke, double width) {
        super();
        this.source = source;
        this.item = new SimpleObjectProperty<>(item);
        this.mouseOnStroke = new SimpleObjectProperty<>(mouseOnStroke);
        setStroke(stroke);
        setStrokeWidth(width);
        connect(this.source);
    }
    
    protected void connect(Node node) {
        startXProperty().bind(node.translateXProperty()
            .add(node.getBoundsInLocal().getCenterX())
            .subtract(strokeWidthProperty().divide(2.0)));

        startYProperty().bind(node.translateYProperty()
            .add(node.getBoundsInLocal().getCenterY())
            .subtract(strokeWidthProperty().divide(2.0)));

        endXProperty().bind(item.get().graphic.translateXProperty()
            .add(item.get().graphic.getBoundsInLocal().getCenterX())
            .subtract(strokeWidthProperty().divide(2.0)));
        endYProperty().bind(item.get().graphic.translateYProperty()
            .add(item.get().graphic.getBoundsInLocal().getCenterY())
            .subtract(strokeWidthProperty().divide(2.0)));
        
        controlX1Property().bind(
            endXProperty().add(
                endXProperty().subtract(
                    startXProperty()).multiply(0.333)));

        controlY1Property().bind(
            endYProperty().add(
                endYProperty().subtract(
                    startYProperty()).multiply(0.333)));

        controlX2Property().bind(
            endXProperty().add(
                endXProperty().subtract(
                    startXProperty()).multiply(0.666)));

        controlY2Property().bind(
            endYProperty().add(
                endYProperty().subtract(
                    startYProperty()).multiply(0.666)));
                
    }
    protected void disconnect() {
        startXProperty().unbind();
        startYProperty().unbind();
        endXProperty().unbind();
        endYProperty().unbind();
        controlX1Property().unbind();
        controlY1Property().unbind();
        controlX2Property().unbind();
        controlY2Property().unbind();
    }
    
//<editor-fold defaultstate="collapsed" desc="Properties">
   
    public Paint getMouseOnStroke() {
        return mouseOnStroke.get();
    }

    public void setMouseOnStroke(Paint stroke) {
        mouseOnStroke.set(stroke);
    }

    public ObjectProperty<Paint> mouseOnStrokeProperty() {
        return mouseOnStroke;
    }    
    
    
    public LitRadialMenuItem getItem() {
        return item.get();
    }

    public void setItem(LitRadialMenuItem item) {
        this.item.set(item);
    }

    public ObjectProperty<LitRadialMenuItem> itemProperty() {
        return item;
    }     
    //</editor-fold> 
}
