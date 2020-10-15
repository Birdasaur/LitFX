
package lit.litfx.controls.menus;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 *
 * @author phillsm1
 */
public class LitRadialMenuLink extends Line {
    
    protected ObjectProperty<Paint> mouseOnFill;
    protected ObjectProperty<Paint> mouseOnStroke;
    protected ObjectProperty<LitRadialMenuItem> item;
    Node source;
    
    public LitRadialMenuLink(Node source, LitRadialMenuItem item, 
        Paint fill, Paint stroke, Paint mouseOnFill, Paint mouseOnStroke, double width) {
        super();
        this.source = source;
        this.item = new SimpleObjectProperty<>(item);
        this.mouseOnFill = new SimpleObjectProperty<>(mouseOnFill);
        this.mouseOnStroke = new SimpleObjectProperty<>(mouseOnStroke);
        setFill(fill);
        setStroke(stroke);
        setStrokeWidth(width);
        
        connect(this.source);
    }
    
    protected void connect(Node node) {
        setStartX(node.getTranslateX());
        setStartY(node.getTranslateY());
        setEndX(item.get().innerStartX);
        setEndY(item.get().innerStartY);
        System.out.println("isX: " + item.get().innerStartX);
        System.out.println("isY: " + item.get().innerStartY);

//        startXProperty().bind(node.translateXProperty());
//        startYProperty().bind(node.translateYProperty());
//        endXProperty().bind(item.get().graphic.translateXProperty());
//        endYProperty().bind(item.get().graphic.translateYProperty());
    }
    protected void disconnect() {
        startXProperty().unbind();
        startYProperty().unbind();
        endXProperty().unbind();
        endYProperty().unbind();
    }
    
//<editor-fold defaultstate="collapsed" desc="Properties">
    
    public Paint getMouseOnFill() {
        return mouseOnFill.get();
    }

    public void setMouseOnFill(Paint fill) {
        this.mouseOnFill.set(fill);
    }

    public ObjectProperty<Paint> mouseOnFillProperty() {
        return mouseOnFill;
    }
    
    
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
