/**
 * Below is the original source code license header from the original version
 * found in JFXtras Labs. 
 * 
 * RadialMenu.java
 * <p>
 * Copyright (c) 2011-2015, JFXtras
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the organization nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * @author Birdasaur
 * heavily adapted From Mr. LoNee's awesome RadialMenu example. Source for original 
 * prototype can be found in JFXtras-labs project.
 * https://github.com/JFXtras/jfxtras-labs
 */
package lit.litfx.controls;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LitRadialMenu extends Group implements EventHandler<MouseEvent>,
        ChangeListener<Object> {

    public enum CenterVisibility {
        ALWAYS, WITH_MENU, NEVER
    }
    public static double DEFAULT_STROKE_WIDTH = 1.0;
    protected List<LitRadialMenuItem> items = new ArrayList<LitRadialMenuItem>();
    protected DoubleProperty innerRadius;
    protected DoubleProperty radius;
    protected DoubleProperty offset;
    protected DoubleProperty initialAngle;
    protected DoubleProperty itemFitWidth;
    protected DoubleProperty menuItemSize;
    protected ObjectProperty<Paint> backgroundFill;
    protected ObjectProperty<Paint> backgroundMouseOnFill;

    protected ObjectProperty<Paint> strokeMouseOnFill;
    protected ObjectProperty<Paint> strokeFill;
    protected DoubleProperty strokeWidth;
    protected BooleanProperty strokeVisible;

    protected ObjectProperty<Paint> outlineStrokeMouseOnFill;
    protected ObjectProperty<Paint> outlineStrokeFill;
    protected DoubleProperty outlineStrokeWidth;
    protected BooleanProperty outlineStrokeVisible;
    protected ObjectProperty<Effect> outlineEffect;
    
    protected BooleanProperty clockwise;
    protected BooleanProperty backgroundVisible;
    protected ObjectProperty<CenterVisibility> centerVisibility;
    protected ObjectProperty<Node> centerGraphic;
    protected Circle centerStrokeShape;
    protected Group centerGroup;
    protected Group itemGroup;
    private boolean mouseOn = false;
    protected BooleanProperty mouseOnProperty = new SimpleBooleanProperty(mouseOn);
    private double lastInitialAngleValue;
    private double lastOffsetValue;
    private boolean allowRedraw = true;
    
    public LitRadialMenu() {
    }

    public LitRadialMenu(final double initialAngle, final double innerRadius,
                      final double radius, final double offset, final Paint bgFill,
                      final Paint bgMouseOnFill, final Paint strokeFill,
                      final Paint strokeMouseOnFill, final boolean clockwise,
                      final CenterVisibility centerVisibility, final Node centerGraphic) {

        itemGroup = new Group();
        getChildren().add(itemGroup);
        
        itemFitWidth = new SimpleDoubleProperty(innerRadius);
        itemFitWidth.addListener((ov,t,t1) -> {
             setGraphicsFitWidth(ov.getValue().doubleValue());
        });
        menuItemSize = new SimpleDoubleProperty(innerRadius);
        menuItemSize.addListener((ov,t,t1) -> {
            setMenuItemSize(ov.getValue().doubleValue());
        });
        
        this.initialAngle = new SimpleDoubleProperty(initialAngle);
        this.initialAngle.addListener((ov,t,t1) -> {
            setInitialAngle(ov.getValue().doubleValue());
        });

        this.innerRadius = new SimpleDoubleProperty(innerRadius);
        this.strokeFill = new SimpleObjectProperty<>(strokeFill);
        this.strokeFill.addListener(this);
        strokeWidth = new SimpleDoubleProperty(DEFAULT_STROKE_WIDTH);
        strokeWidth.addListener(this);        
        this.strokeMouseOnFill = new SimpleObjectProperty<>(strokeMouseOnFill);
        this.strokeMouseOnFill.addListener(this);
        strokeVisible = new SimpleBooleanProperty(true);
        strokeVisible.addListener(this);
        
        outlineStrokeFill = new SimpleObjectProperty<>(strokeFill);
        outlineStrokeFill.addListener(this);
        outlineStrokeWidth = new SimpleDoubleProperty(DEFAULT_STROKE_WIDTH);
        outlineStrokeWidth.addListener(this);        
        outlineStrokeMouseOnFill = new SimpleObjectProperty<>(strokeMouseOnFill);
        outlineStrokeMouseOnFill.addListener(this);
        outlineStrokeVisible = new SimpleBooleanProperty(true);
        outlineStrokeVisible.addListener(this);   
        outlineEffect = new SimpleObjectProperty<>(null);
        outlineEffect.addListener(this);
        
        this.radius = new SimpleDoubleProperty(radius);
        this.offset = new SimpleDoubleProperty(offset);
        this.clockwise = new SimpleBooleanProperty(clockwise);
        backgroundFill = new SimpleObjectProperty<>(bgFill);
        backgroundFill.addListener(this);
        backgroundMouseOnFill = new SimpleObjectProperty<>(bgMouseOnFill);
        backgroundMouseOnFill.addListener(this);
        backgroundVisible = new SimpleBooleanProperty(true);
        this.centerVisibility = new SimpleObjectProperty<>(centerVisibility);
        centerStrokeShape = new Circle(innerRadius);
        centerStrokeShape.radiusProperty().bind(innerRadiusProperty());
        centerStrokeShape.setStroke(strokeFill);
        centerStrokeShape.setStrokeWidth(strokeWidth.get());
        centerStrokeShape.setFill(bgFill);
        this.centerVisibility.addListener(this);
        backgroundVisible.addListener(this);

        centerGroup = new Group();
        centerGroup.getChildren().add(centerStrokeShape);
        centerGroup.setOnMouseEntered(event -> {
            mouseOn = true;
            mouseOnProperty.set(mouseOn);
            redraw();
        });
        centerGroup.setOnMouseExited(event -> {
            mouseOn = false;
            mouseOnProperty.set(mouseOn);
            redraw();
        });
        centerGroup.setOnMouseClicked(event -> {
            if(!event.isControlDown()) {
                final boolean visible = itemGroup.isVisible();
                if (visible) {
                    hideRadialMenu();
                } else {
                    showRadialMenu();
                }
            }
            event.consume();
        });
        getChildren().add(centerGroup);
        this.centerGraphic = new SimpleObjectProperty<Node>(centerGraphic);
        setCenterGraphic(centerGraphic);
        saveStateBeforeAnimation();
    }
    
    public void setGraphicsFitWidth(double fitWidth) {
        Node centerNode = getCenterGraphic();
        if(centerNode instanceof ImageView) {
            ImageView civ = (ImageView)centerNode;
            civ.setFitWidth(fitWidth);
            civ.setTranslateX(-fitWidth / 2.0);
            civ.setTranslateY(-fitWidth / 2.0);
        }
        items.stream().forEach(item -> {
            Node node = item.getGraphic();
            if(node instanceof ImageView) {
                ImageView iv = (ImageView)node;
                iv.setFitWidth(fitWidth);
            }
        });        
    }
    public void setMenuItemSize(double menuItemSize) {
        items.stream().forEach(item -> item.setMenuSize(menuItemSize));
    }
    public void setOnMenuItemMouseClicked(
            final EventHandler<? super MouseEvent> paramEventHandler) {
        for (final LitRadialMenuItem item : items) {
            item.setOnMouseClicked(paramEventHandler);
        }
    }

    public void setInitialAngle(final double angle) {
        initialAngle.set(angle);

        double angleOffset = initialAngle.get();
        for (final LitRadialMenuItem item : items) {
            item.setStartAngle(angleOffset);
            angleOffset = angleOffset + item.getMenuSize();
        }
    }
   
    public void addMenuItem(final LitRadialMenuItem item) {
        item.visibleProperty().bind(visibleProperty());
        item.backgroundColorProperty().bind(backgroundFill);
        item.backgroundMouseOnColorProperty().bind(backgroundMouseOnFill);
        item.backgroundVisibleProperty().bind(backgroundVisible);

        item.innerRadiusProperty().bind(innerRadius);
        item.radiusProperty().bind(radius);
        item.offsetProperty().bind(offset);
        item.strokeMouseOnColorProperty().bind(strokeMouseOnFill);
        item.strokeColorProperty().bind(strokeFill);
        item.strokeWidthProperty().bind(strokeWidth);
        item.strokeVisibleProperty().bind(strokeVisible);
        item.outlineStrokeMouseOnColorProperty().bind(outlineStrokeMouseOnFill);
        item.outlineStrokeColorProperty().bind(outlineStrokeFill);
        item.outlineStrokeWidthProperty().bind(outlineStrokeWidth);
        item.outlineStrokeVisibleProperty().bind(outlineStrokeVisible);
        item.outlineEffectProperty().bind(outlineEffect);

        item.clockwiseProperty().bind(clockwise);
        items.add(item);
        itemGroup.getChildren().add(itemGroup.getChildren().size(), item);
        double angleOffset = initialAngle.get();
        for (final LitRadialMenuItem it : items) {
            it.setStartAngle(angleOffset);
            angleOffset = angleOffset + item.getMenuSize();
        }
        item.setOnMouseClicked(this);
    }

    public void removeMenuItem(final LitRadialMenuItem item) {
        items.remove(item);
        itemGroup.getChildren().remove(item);
        item.visibleProperty().unbind();
        item.backgroundColorProperty().unbind();
        item.backgroundMouseOnColorProperty().unbind();
        item.innerRadiusProperty().unbind();
        item.radiusProperty().unbind();
        item.offsetProperty().unbind();
        item.clockwiseProperty().unbind();
        item.backgroundVisibleProperty().unbind();
        item.strokeMouseOnColorProperty().unbind();
        item.strokeColorProperty().unbind();
        item.strokeWidthProperty().unbind();
        item.strokeVisibleProperty().unbind();
        item.outlineStrokeMouseOnColorProperty().unbind();
        item.outlineStrokeColorProperty().unbind();
        item.outlineStrokeWidthProperty().unbind();
        item.outlineStrokeVisibleProperty().unbind();
        item.outlineEffectProperty().unbind();

        item.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
    }

    public void removeMenuItem(final int itemIndex) {
        final LitRadialMenuItem item = items.get(itemIndex);
        removeMenuItem(item);
    }

    @Override
    public void handle(final MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            final LitRadialMenuItem item = (LitRadialMenuItem) event.getSource();
            item.setSelected(!item.isSelected());
            for (final LitRadialMenuItem it : items) {
                if (it != item) {
                    it.setSelected(false);
                }
            }
            if (!item.isSelected()) {
                hideRadialMenu();
            }
            event.consume();
        }
    }

    public void hideRadialMenu() {
        saveStateBeforeAnimation();

        final List<Animation> anim = new ArrayList<>();

        final FadeTransition fadeItemGroup = new FadeTransition(Duration.millis(300), itemGroup);
        fadeItemGroup.setFromValue(1);
        fadeItemGroup.setToValue(0);
        fadeItemGroup.setOnFinished(event -> {
            itemGroup.setVisible(false);
        });

        anim.add(fadeItemGroup);

        if (centerVisibility.get() == CenterVisibility.WITH_MENU) {
            final FadeTransition fadeCenter = new FadeTransition(Duration.millis(300), centerGroup);
            fadeCenter.setFromValue(1);
            fadeCenter.setToValue(0);
            fadeCenter.setOnFinished(event -> {
                centerGroup.setVisible(false);
            });
            anim.add(fadeCenter);
        }
        final ParallelTransition transition = new ParallelTransition(anim.toArray(new Animation[]{}));
        transition.play();
    }

    public void showRadialMenu() {
        final List<Animation> animationList = new ArrayList<>();
        final FadeTransition fade = new FadeTransition(Duration.millis(400), itemGroup);
        fade.setFromValue(0);
        fade.setToValue(1.0);
        animationList.add(fade);

        final Animation offsetAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(offsetProperty(), 0)), 
            new KeyFrame(Duration.millis(300), new KeyValue(offsetProperty(), lastOffsetValue)));
        animationList.add(offsetAnimation);

        final Animation angle = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(initialAngleProperty(), lastInitialAngleValue + 20)), 
            new KeyFrame(Duration.millis(300), new KeyValue(initialAngleProperty(),lastInitialAngleValue)));
        animationList.add(angle);

        if (centerVisibility.get() == CenterVisibility.WITH_MENU) {
            final FadeTransition fadeCenter = new FadeTransition(Duration.millis(300), centerGroup);
            fadeCenter.setFromValue(0);
            fadeCenter.setToValue(1);
            animationList.add(fadeCenter);
            centerGroup.setVisible(true);
        }
        final ParallelTransition transition = new ParallelTransition(animationList.toArray(new Animation[]{}));
        itemGroup.setVisible(true);
        transition.play();
    }

    private void saveStateBeforeAnimation() {
        lastInitialAngleValue = initialAngle.get();
        lastOffsetValue = offset.get();
    }

    @Override
    public void changed(final ObservableValue<? extends Object> arg0,
                        final Object arg1, final Object arg2) {
        if(isAllowRedraw())
            redraw();
    }
    public void requestDraw() {
        redraw();
    }

    private void redraw() {
        if (centerVisibility.get() == CenterVisibility.NEVER) {
            centerGroup.visibleProperty().set(false);
        } else if (centerVisibility.get() == CenterVisibility.ALWAYS) {
            centerGroup.visibleProperty().set(true);
        } else {
            centerGroup.visibleProperty().set(itemGroup.isVisible());
        }

        centerStrokeShape.setFill(backgroundVisible.get() ? (mouseOn
                        && backgroundMouseOnFill.get() != null ? backgroundMouseOnFill
                        .get() : backgroundFill.get())
                        : Color.TRANSPARENT);
        centerStrokeShape.setStroke(strokeVisible.get() ? (mouseOn
                        && strokeMouseOnFill.get() != null ? strokeMouseOnFill
                        .get() : strokeFill.get())
                        : Color.TRANSPARENT);
        centerStrokeShape.setStrokeWidth(strokeWidth.get());
    }
//<editor-fold defaultstate="collapsed" desc="Properties">
    public Group getCenterGroup() {
        return centerGroup;
    }
    public BooleanProperty getMouseOnProperty() {
        return mouseOnProperty;
    }
    public Paint getBackgroundFill() {
        return backgroundFill.get();
    }

    public void setBackgroundFill(final Paint backgroundFill) {
        this.backgroundFill.set(backgroundFill);
    }

    public ObjectProperty<Paint> backgroundFillProperty() {
        return backgroundFill;
    }

    public Paint getBackgroundMouseOnFill() {
        return backgroundMouseOnFill.get();
    }

    public void setBackgroundMouseOnFill(final Paint backgroundMouseOnFill) {
        this.backgroundMouseOnFill.set(backgroundMouseOnFill);
    }

    public ObjectProperty<Paint> backgroundMouseOnFillProperty() {
        return backgroundMouseOnFill;
    }

    public Paint getStrokeMouseOnFill() {
        return strokeMouseOnFill.get();
    }

    public void setStrokeMouseOnFill(final Paint backgroundMouseOnFill) {
        strokeMouseOnFill.set(backgroundMouseOnFill);
    }

    public ObjectProperty<Paint> strokeMouseOnFillProperty() {
        return strokeMouseOnFill;
    }

    public Paint getStrokeFill() {
        return strokeFill.get();
    }

    public void setStrokeFill(final Paint strokeFill) {
        this.strokeFill.set(strokeFill);
    }

    public ObjectProperty<Paint> strokeFillProperty() {
        return strokeFill;
    }

    public double getStrokeWidth() {
        return strokeWidth.get();
    }

    public void setStrokeWidth(final double width) {
        strokeWidth.set(width);
    }

    public DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

    public BooleanProperty strokeVisibleProperty() {
        return strokeVisible;
    }

    public boolean isStrokeVisible() {
        return strokeVisible.get();
    }


    public Paint getOutlineStrokeMouseOnFill() {
        return outlineStrokeMouseOnFill.get();
    }

    public void setOutlineStrokeMouseOnFill(final Paint backgroundMouseOnFill) {
        outlineStrokeMouseOnFill.set(backgroundMouseOnFill);
    }

    public ObjectProperty<Paint> outlineStrokeMouseOnFillProperty() {
        return outlineStrokeMouseOnFill;
    }

    public Paint getOutlineStrokeFill() {
        return outlineStrokeFill.get();
    }

    public void setOutlineStrokeFill(final Paint strokeFill) {
        outlineStrokeFill.set(strokeFill);
    }

    public ObjectProperty<Paint> outlineStrokeFillProperty() {
        return outlineStrokeFill;
    }

    public double getOutlineStrokeWidth() {
        return outlineStrokeWidth.get();
    }

    public void setOutlineStrokeWidth(final double width) {
        outlineStrokeWidth.set(width);
    }

    public DoubleProperty outlineStrokeWidthProperty() {
        return outlineStrokeWidth;
    }

    public BooleanProperty outlineStrokeVisibleProperty() {
        return outlineStrokeVisible;
    }

    public boolean isOutlinStrokeVisible() {
        return outlineStrokeVisible.get();
    }

    public Effect getOutlineEffect() {
        return outlineEffect.get();
    }

    public void setOutlineEffect(final Effect outlineEffect) {
        this.outlineEffect.set(outlineEffect);
    }

    public ObjectProperty<Effect> outlineEffectProperty() {
        return outlineEffect;
    }    
    
    public Node getCenterGraphic() {
        return centerGraphic.get();
    }

    public void setCenterGraphic(final Node graphic) {
        if (centerGraphic.get() != null) {
            centerGroup.getChildren().remove(centerGraphic.get());
        }
        if (graphic != null) {
            centerGroup.getChildren().add(graphic);
        }
        centerGraphic.set(graphic);
    }

    public ObjectProperty<Node> centerGraphicProperty() {
        return centerGraphic;
    }

    public double getInitialAngle() {
        return initialAngle.get();
    }

    public DoubleProperty initialAngleProperty() {
        return initialAngle;
    }
    public double getItemFitWidth() {
        return itemFitWidth.get();
    }

    public DoubleProperty itemFitWidthProperty() {
        return itemFitWidth;
    }
    public double getMenuItemSize() {
        return menuItemSize.get();
    }

    public DoubleProperty menuItemSizeProperty() {
        return menuItemSize;
    }

    public double getInnerRadius() {
        return innerRadius.get();
    }

    public DoubleProperty innerRadiusProperty() {
        return innerRadius;
    }

    public double getRadius() {
        return radius.get();
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    public double getOffset() {
        return offset.get();
    }

    public DoubleProperty offsetProperty() {
        return offset;
    }

    public boolean isClockwise() {
        return clockwise.get();
    }

    public BooleanProperty clockwiseProperty() {
        return clockwise;
    }

    public boolean isBackgroundVisible() {
        return backgroundVisible.get();
    }

    public BooleanProperty backgroundVisibleProperty() {
        return backgroundVisible;
    }

    public ObjectProperty<CenterVisibility> centerVisibilityProperty() {
        return centerVisibility;
    }

    public CenterVisibility getCenterVisibility() {
        return centerVisibility.get();
    }

    public void setCenterVisibility(final CenterVisibility visibility) {
        centerVisibility.set(visibility);
    }

    public void setInnerRadius(final double radius) {
        innerRadius.set(radius);
    }

    public void setRadius(final double radius) {
        this.radius.set(radius);
    }

    public void setOffset(final double offset) {
        this.offset.set(offset);
    }

    public void setBackgroundVisible(final boolean visible) {
        backgroundVisible.set(visible);
    }

    public void setStrokeVisible(final boolean visible) {
        strokeVisible.set(visible);
    }

    public void setBackgroundColor(final Paint color) {
        backgroundFill.set(color);
    }

    public void setBackgroundMouseOnColor(final Paint color) {
        backgroundMouseOnFill.set(color);
    }

    public void setStrokeMouseOnColor(final Paint color) {
        strokeMouseOnFill.set(color);
    }

    public void setStrokeColor(final Paint color) {
        strokeFill.set(color);
    }

    public void setClockwise(final boolean clockwise) {
        this.clockwise.set(clockwise);
    }

    /**
     * @return the allowRedraw
     */
    public boolean isAllowRedraw() {
        return allowRedraw;
    }

    /**
     * @param allowRedraw the allowRedraw to set
     */
    public void setAllowRedraw(boolean allowRedraw) {
        this.allowRedraw = allowRedraw;
    }
    //</editor-fold>    
}
