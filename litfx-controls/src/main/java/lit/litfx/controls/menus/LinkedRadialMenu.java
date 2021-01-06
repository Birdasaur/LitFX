/**
 * @author Birdasaur
 * Extends LitRadialMenu by providing rendered links between menu items 
 */
package lit.litfx.controls.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class LinkedRadialMenu extends LitRadialMenu {

    public static double DEFAULT_LINK_STROKE_WIDTH = 5.0;
    public static Color DEFAULT_LINK_STROKE = Color.SKYBLUE.deriveColor(1, 1, 1, 0.5);
    public static Color DEFAULT_LINK_MOUSEON_STROKE = Color.SKYBLUE;
    
    protected DoubleProperty linkStrokeWidth;
    protected ObjectProperty<Paint> linkStroke;
    protected ObjectProperty<Paint> linkMouseOnStroke;
    protected BooleanProperty linksVisible;
    protected ObjectProperty<Effect> linkEffect;
    
//    protected List<LitRadialMenuLink> links = new ArrayList<>();
    protected List<LitRadialMenuCurvedLink> links = new ArrayList<>();
    protected Group linkGroup;
//    HashMap<LitRadialMenuItem, LitRadialMenuLink> itemToLinkMap;
    HashMap<LitRadialMenuItem, LitRadialMenuCurvedLink> itemToLinkMap;
    
    public LinkedRadialMenu() {
        super();
        this.itemToLinkMap = new HashMap<>();
    }

    public LinkedRadialMenu(final double initialAngle, final double innerRadius,
                      final double radius, final double offset, final Paint bgFill,
                      final Paint bgMouseOnFill, final Paint strokeFill,
                      final Paint strokeMouseOnFill, final boolean clockwise,
                      final CenterVisibility centerVisibility, final Node centerGraphic) {
        super(initialAngle, innerRadius, radius, offset, bgFill, bgMouseOnFill, strokeFill, strokeMouseOnFill, clockwise, centerVisibility, centerGraphic);
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

//        centerGroup = new Group();
//        centerGroup.getChildren().add(centerStrokeShape);
//        centerGroup.setOnMouseEntered(event -> {
//            mouseOn = true;
//            mouseOnProperty.set(mouseOn);
//            redraw();
//        });
//        centerGroup.setOnMouseExited(event -> {
//            mouseOn = false;
//            mouseOnProperty.set(mouseOn);
//            redraw();
//        });
//        centerGroup.setOnMouseClicked(event -> {
//            if(!event.isControlDown()) {
//                final boolean visible = itemGroup.isVisible();
//                if (visible) {
//                    hideRadialMenu();
//                } else {
//                    showRadialMenu();
//                }
//            }
//            event.consume();
//        });
//        getChildren().add(centerGroup);
    }
    
//    public void setGraphicsFitWidth(double fitWidth) {
//        Node centerNode = getCenterGraphic();
//        if(centerNode instanceof ImageView) {
//            ImageView civ = (ImageView)centerNode;
//            civ.setFitWidth(fitWidth);
//            civ.setTranslateX(-fitWidth / 2.0);
//            civ.setTranslateY(-fitWidth / 2.0);
//        }
//        items.stream().forEach(item -> {
//            Node node = item.getGraphic();
//            if(node instanceof ImageView) {
//                ImageView iv = (ImageView)node;
//                iv.setFitWidth(fitWidth);
//            }
//        });        
//    }
//    public void setMenuItemSize(double menuItemSize) {
//        items.stream().forEach(item -> item.setMenuSize(menuItemSize));
//    }
//    public void setOnMenuItemMouseClicked(
//            final EventHandler<? super MouseEvent> paramEventHandler) {
//        for (final LitRadialMenuItem item : items) {
//            item.setOnMouseClicked(paramEventHandler);
//        }
//    }

   
    @Override
    public void addMenuItem(final LitRadialMenuItem item) {
        super.addMenuItem(item);
//        LitRadialMenuLink link = new LitRadialMenuLink(this.centerStrokeShape, item,  
//            linkStroke.get(), linkMouseOnStroke.get(), linkStrokeWidth.get());
        LitRadialMenuCurvedLink link = new LitRadialMenuCurvedLink(this.centerStrokeShape, item,  
            linkStroke.get(), linkMouseOnStroke.get(), linkStrokeWidth.get());
        itemToLinkMap.put(item, link);
        links.add(link);
        linkGroup.getChildren().add(link);
        
        link.visibleProperty().bind(item.visibleProperty());
        link.strokeProperty().bind(linkStroke);
        link.mouseOnStrokeProperty().bind(strokeMouseOnFill);
        link.strokeWidthProperty().bind(linkStrokeWidth);
        link.effectProperty().bind(linkEffect);

//        link.setOnMouseClicked(this);
    }

    
    @Override
    public void removeMenuItem(final LitRadialMenuItem item) {
        super.removeMenuItem(item);
//        LitRadialMenuLink link = itemToLinkMap.get(item);
        LitRadialMenuCurvedLink link = itemToLinkMap.get(item);
        links.remove(link);
        linkGroup.getChildren().remove(link);

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
//    public void handle(final MouseEvent event) {
//        if (event.getButton() == MouseButton.PRIMARY) {
//            final LitRadialMenuItem item = (LitRadialMenuItem) event.getSource();
//            item.setSelected(!item.isSelected());
//            for (final LitRadialMenuItem it : items) {
//                if (it != item) {
//                    it.setSelected(false);
//                }
//            }
//            if (!item.isSelected()) {
//                hideRadialMenu();
//            }
//            event.consume();
//        }
//    }

//@TODO SMP
//    public void hideRadialMenu() {
//        saveStateBeforeAnimation();
//
//        final List<Animation> anim = new ArrayList<>();
//
//        final FadeTransition fadeItemGroup = new FadeTransition(Duration.millis(300), itemGroup);
//        fadeItemGroup.setFromValue(1);
//        fadeItemGroup.setToValue(0);
//        fadeItemGroup.setOnFinished(event -> {
//            itemGroup.setVisible(false);
//        });
//
//        anim.add(fadeItemGroup);
//
//        if (centerVisibility.get() == CenterVisibility.WITH_MENU) {
//            final FadeTransition fadeCenter = new FadeTransition(Duration.millis(300), centerGroup);
//            fadeCenter.setFromValue(1);
//            fadeCenter.setToValue(0);
//            fadeCenter.setOnFinished(event -> {
//                centerGroup.setVisible(false);
//            });
//            anim.add(fadeCenter);
//        }
//        final ParallelTransition transition = new ParallelTransition(anim.toArray(new Animation[]{}));
//        transition.play();
//    }

//@TODO SMP
//    public void showRadialMenu() {
//        final List<Animation> animationList = new ArrayList<>();
//        final FadeTransition fade = new FadeTransition(Duration.millis(400), itemGroup);
//        fade.setFromValue(0);
//        fade.setToValue(1.0);
//        animationList.add(fade);
//
//        final Animation offsetAnimation = new Timeline(
//            new KeyFrame(Duration.ZERO, new KeyValue(offsetProperty(), 0)), 
//            new KeyFrame(Duration.millis(300), new KeyValue(offsetProperty(), lastOffsetValue)));
//        animationList.add(offsetAnimation);
//
//        final Animation angle = new Timeline(
//            new KeyFrame(Duration.ZERO, new KeyValue(initialAngleProperty(), lastInitialAngleValue + 20)), 
//            new KeyFrame(Duration.millis(300), new KeyValue(initialAngleProperty(),lastInitialAngleValue)));
//        animationList.add(angle);
//
//        if (centerVisibility.get() == CenterVisibility.WITH_MENU) {
//            final FadeTransition fadeCenter = new FadeTransition(Duration.millis(300), centerGroup);
//            fadeCenter.setFromValue(0);
//            fadeCenter.setToValue(1);
//            animationList.add(fadeCenter);
//            centerGroup.setVisible(true);
//        }
//        final ParallelTransition transition = new ParallelTransition(animationList.toArray(new Animation[]{}));
//        itemGroup.setVisible(true);
//        transition.play();
//    }

    //@TODO SMP
    private void redrawLinks() {
//        if (centerVisibility.get() == CenterVisibility.NEVER) {
//            centerGroup.visibleProperty().set(false);
//        } else if (centerVisibility.get() == CenterVisibility.ALWAYS) {
//            centerGroup.visibleProperty().set(true);
//        } else {
//            centerGroup.visibleProperty().set(itemGroup.isVisible());
//        }
//
//        centerStrokeShape.setFill(backgroundVisible.get() ? (mouseOn
//                        && backgroundMouseOnFill.get() != null ? backgroundMouseOnFill
//                        .get() : backgroundFill.get())
//                        : Color.TRANSPARENT);
//        centerStrokeShape.setStroke(strokeVisible.get() ? (mouseOn
//                        && strokeMouseOnFill.get() != null ? strokeMouseOnFill
//                        .get() : strokeFill.get())
//                        : Color.TRANSPARENT);
//        centerStrokeShape.setStrokeWidth(strokeWidth.get());
    }
    
//<editor-fold defaultstate="collapsed" desc="Properties">
    //@TODO SMP
//    public BooleanProperty getMouseOnProperty() {
//        return mouseOnProperty;
//    }
    public Paint getLinkStroke() {
        return linkStroke.get();
    }

    public void setLinkStroke(final Paint stroke) {
        this.linkStroke.set(stroke);
    }

    public ObjectProperty<Paint> linkStrokeProperty() {
        return linkStroke;
    }

    public Paint getLinkMouseOnStroke() {
        return linkMouseOnStroke.get();
    }

    public void setLinkMouseOnStroke(Paint stroke) {
        linkMouseOnStroke.set(stroke);
    }

    public ObjectProperty<Paint> linkMouseOnStrokeProperty() {
        return linkMouseOnStroke;
    }

    public double getLinkStrokeWidth() {
        return linkStrokeWidth.get();
    }

    public void setLinkStrokeWidth(double width) {
        linkStrokeWidth.set(width);
    }

    public DoubleProperty linkStrokeWidthProperty() {
        return linkStrokeWidth;
    }
    
    public Effect getLinkEffect() {
        return linkEffect.get();
    }

    public void setLinkEffect(Effect effect) {
        this.linkEffect.set(effect);
    }

    public ObjectProperty<Effect> linkEffectProperty() {
        return linkEffect;
    }    
    
    //</editor-fold>    
}
