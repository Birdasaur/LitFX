/**
 * @author Birdasaur
 * Extends LitRadialContainerMenuItem by providing rendered links between menu items 
 */
package lit.litfx.controls.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class LinkedRadialContainerMenuItem extends LitRadialContainerMenuItem {

    public static double DEFAULT_LINK_STROKE_WIDTH = 5.0;
    public static Color DEFAULT_LINK_STROKE = Color.SKYBLUE.deriveColor(1, 1, 1, 0.5);
    public static Color DEFAULT_LINK_MOUSEON_STROKE = Color.SKYBLUE;
    
    protected DoubleProperty linkStrokeWidth;
    protected ObjectProperty<Paint> linkStroke;
    protected ObjectProperty<Paint> linkMouseOnStroke;
    protected BooleanProperty linksVisible;
    protected ObjectProperty<Effect> linkEffect;
    
    private final Group childAnimGroup = new Group();
    private FadeTransition fadeIn = null;
    private FadeTransition fadeOut = null;
    
    protected List<LitRadialMenuLink> links = new ArrayList<>();
    protected Group linkGroup;
    HashMap<LitRadialMenuItem, LitRadialMenuLink> itemToLinkMap;
    
    
    public LinkedRadialContainerMenuItem(final double menuSize, final Node graphic) {
	super(menuSize, graphic);
        this.itemToLinkMap = new HashMap<>();
    }

    public LinkedRadialContainerMenuItem(final double menuSize, final String text,
	    final Node graphic) {
	super(menuSize, text, graphic);
        this.itemToLinkMap = new HashMap<>();
        linkGroup = new Group();
        getChildren().add(0, linkGroup);
        linkGroup.toBack();
        
        //Size of link
        this.linkStrokeWidth = new SimpleDoubleProperty(DEFAULT_LINK_STROKE_WIDTH);
        linkStrokeWidth.addListener(this);        
        //colors of link
        this.linkStroke = new SimpleObjectProperty<>(DEFAULT_LINK_STROKE);
        this.linkStroke.addListener(this);
        //colors when mouse over
        this.linkMouseOnStroke = new SimpleObjectProperty<>(DEFAULT_LINK_MOUSEON_STROKE);
        this.linkMouseOnStroke.addListener(this);
        //Can we see the links
        linksVisible = new SimpleBooleanProperty(true);
        linksVisible.addListener(this);
        //Special Effects
        linkEffect = new SimpleObjectProperty<>(null);
        linkEffect.addListener(this);        
    }
//@TODO SMP
//    private void initialize() {
//        arrow.setFill(Color.GRAY);
//        arrow.setStroke(null);
//	childAnimGroup.setVisible(false);
//	visibleProperty().addListener(new ChangeListener<Boolean>() {
//	    @Override
//	    public void changed(final ObservableValue<? extends Boolean> arg0,
//		    final Boolean arg1, final Boolean arg2) {
//		if (!arg0.getValue()) {
//		    childAnimGroup.setVisible(false);
//		    setSelected(false);
//		}
//	    }
//	});
//	getChildren().add(childAnimGroup);
//	fadeIn = new FadeTransition(Duration.millis(400), childAnimGroup);
//	fadeIn.setFromValue(0.0);
//	fadeIn.setToValue(1.0);
//	fadeOut = new FadeTransition(Duration.millis(400), childAnimGroup);
//	fadeOut.setFromValue(0.0);
//	fadeOut.setToValue(1.0);
//	fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(final ActionEvent arg0) {
//                childAnimGroup.setVisible(false);
//            }
//        });
//	getChildren().add(arrow);
//    }

    @Override
    public void addMenuItem(final LitRadialMenuItem item) {
        super.addMenuItem(item);
        LitRadialMenuLink link = new LitRadialMenuLink(this.graphic, item,  
            linkStroke.get(), linkMouseOnStroke.get(), linkStrokeWidth.get());
        itemToLinkMap.put(item, link);
        links.add(link);
        linkGroup.getChildren().add(link);
        
        link.visibleProperty().bind(item.visibleProperty());
        link.strokeProperty().bind(linkStroke);
        link.mouseOnStrokeProperty().bind(linkMouseOnStroke);
        link.strokeWidthProperty().bind(linkStrokeWidth);
        link.effectProperty().bind(linkEffect);

        //@TODO SMP
//        link.setOnMouseClicked(this);
//	childAnimGroup.getChildren().add(item);
    }

    @Override
    public void removeMenuItem(final LitRadialMenuItem item) {
        super.removeMenuItem(item);
        LitRadialMenuLink link = itemToLinkMap.get(item);
        links.remove(link);
        linkGroup.getChildren().remove(link);
//@TODO SMP
//	childAnimGroup.getChildren().remove(item);

        link.visibleProperty().unbind();
        link.fillProperty().unbind();
        link.strokeProperty().unbind();
        link.mouseOnStrokeProperty().unbind();
        link.strokeWidthProperty().unbind();
        link.effectProperty().unbind();
        
        link.disconnect();
        
//        link.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
    }

//@TODO SMP    
//    @Override
//    protected void redraw() {
//	super.redraw();
//	if (selected) {
//	    path.setFill(backgroundVisible.get() ? (selected
//                && backgroundMouseOnColor.get() != null ? backgroundMouseOnColor
//                .get() : backgroundColor.get())
//                : null);
//	}
//	if (arrow != null) {
//	    arrow.setFill(backgroundVisible.get() ? (mouseOn
//		    && strokeColor.get() != null ? strokeColor.get()
//		    : strokeColor.get()) : null);
//	    arrow.setStroke(strokeVisible.get() ? strokeColor.get() : null);
//	    if (!clockwise.get()) {
//		arrow.setRotate(-(startAngle.get() + menuSize.get() / 2.0));
//		arrow.setTranslateX((radius.get() - arrow
//			.getBoundsInLocal().getWidth() / 2.0)
//			* Math.cos(Math.toRadians(startAngle.get()
//				+ menuSize.get() / 2.0)) + translateX);
//		arrow.setTranslateY(-(radius.get() - arrow
//			.getBoundsInLocal().getHeight() / 2.0)
//			* Math.sin(Math.toRadians(startAngle.get()
//				+ menuSize.get() / 2.0)) + translateY);
//	    } else {
//		arrow.setRotate(startAngle.get() + menuSize.get()
//			/ 2.0);
//		arrow.setTranslateX((radius.get() - arrow
//			.getBoundsInLocal().getWidth() / 2.0)
//			* Math.cos(Math.toRadians(startAngle.get()
//				+ menuSize.get() / 2.0)) + translateX);
//		arrow.setTranslateY((radius.get() - arrow
//			.getBoundsInLocal().getHeight() / 2.0)
//			* Math.sin(Math.toRadians(startAngle.get()
//				+ menuSize.get() / 2.0)) + translateY);
//	    }
//	}
//    }

//@TODO SMP    
//    @Override
//    void setSelected(final boolean selected) {
//	this.selected = selected;
//	if (selected) {
//	    double startOpacity = 0;
//	    if (fadeOut.getStatus() == Status.RUNNING) {
//		fadeOut.stop();
//		startOpacity = childAnimGroup.getOpacity();
//	    }
//	    // draw Children
//	    childAnimGroup.setOpacity(startOpacity);
//	    childAnimGroup.setVisible(true);
//	    fadeIn.fromValueProperty().set(startOpacity);
//	    fadeIn.playFromStart();
//	} else {
//	    // draw Children
//	    double startOpacity = 1.0;
//	    if (fadeIn.getStatus() == Status.RUNNING) {
//		fadeIn.stop();
//		startOpacity = childAnimGroup.getOpacity();
//	    }
//	    fadeOut.fromValueProperty().set(startOpacity);
//	    fadeOut.playFromStart();
//	}
//	redraw();
//    }

}
