/**
 * Below is the original source code license header from the original version
 * found in JFXtras Labs. 
 * 
 * RadialMenuItem.java
 *
 * Copyright (c) 2011-2015, JFXtras
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
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
 * Adapted From Mr. LoNee's awesome RadialMenu example. Source for original 
 * prototype can be found in JFXtras-labs project.
 * https://github.com/JFXtras/jfxtras-labs
 */
package lit.litfx.controls.menus;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class LitRadialMenuItem extends Group implements ChangeListener<Object> {

    protected DoubleProperty startAngle = new SimpleDoubleProperty();
    protected DoubleProperty menuSize = new SimpleDoubleProperty(45);
    protected DoubleProperty innerRadius = new SimpleDoubleProperty();
    protected DoubleProperty radius = new SimpleDoubleProperty();
    protected DoubleProperty offset = new SimpleDoubleProperty();
    protected ObjectProperty<Paint> backgroundMouseOnColor = new SimpleObjectProperty<Paint>();
    protected ObjectProperty<Paint> backgroundColor = new SimpleObjectProperty<Paint>();
    protected BooleanProperty backgroundVisible = new SimpleBooleanProperty(true);
    protected BooleanProperty strokeVisible = new SimpleBooleanProperty(true);
    protected ObjectProperty<Paint> strokeColor = new SimpleObjectProperty<Paint>();
    protected ObjectProperty<Paint> strokeMouseOnColor = new SimpleObjectProperty<Paint>();
    protected BooleanProperty clockwise = new SimpleBooleanProperty();
    //ADDING STROKE WIDTH and Other cool stuff support
    protected DoubleProperty strokeWidth = new SimpleDoubleProperty();
    protected ObjectProperty<Effect> effect = new SimpleObjectProperty<Effect>();
    //Outline Support (TRON effects)
    protected BooleanProperty outlineStrokeVisible = new SimpleBooleanProperty(true);
    protected ObjectProperty<Paint> outlineStrokeColor = new SimpleObjectProperty<Paint>();
    protected ObjectProperty<Paint> outlineStrokeMouseOnColor = new SimpleObjectProperty<Paint>();
    protected DoubleProperty outlineStrokeWidth = new SimpleDoubleProperty();
    protected ObjectProperty<Effect> outlineEffect = new SimpleObjectProperty<Effect>();
    //Path Rendering objects
    protected MoveTo moveTo;
    protected ArcTo arcToInner;
    protected ArcTo arcTo;
    protected LineTo lineTo;
    protected LineTo lineTo2;
    protected double innerStartX;
    protected double innerStartY;
    protected double innerEndX;
    protected double innerEndY;
    protected boolean innerSweep;
    protected double startX;
    protected double startY;
    protected double endX;
    protected double endY;

    protected DoubleProperty startXProperty = new SimpleDoubleProperty();
    protected DoubleProperty startYProperty = new SimpleDoubleProperty();
    protected DoubleProperty endXProperty = new SimpleDoubleProperty();
    protected DoubleProperty endYProperty = new SimpleDoubleProperty();
    protected DoubleProperty translateXProperty = new SimpleDoubleProperty();
    protected DoubleProperty translateYProperty = new SimpleDoubleProperty();

    protected boolean sweep;
    protected double graphicX;
    protected double graphicY;
    protected double translateX;
    protected double translateY;
    protected boolean mouseOn = false;
    protected BooleanProperty mouseOnProperty = new SimpleBooleanProperty(mouseOn);    
    protected Path path;
    protected Path outlinePath; //For creating sharply contrasting outline effects
    protected Node graphic;
    protected String text;

    public LitRadialMenuItem() {
        menuSize = new SimpleDoubleProperty(45);
        menuSize.addListener(this);
        innerRadius.addListener(this);
	radius.addListener(this);
	offset.addListener(this);
	backgroundVisible.addListener(this);
	strokeVisible.addListener(this);
	clockwise.addListener(this);
	backgroundColor.addListener(this);
	strokeColor.addListener(this);
	strokeWidth.addListener(this);
	effect.addListener(this);
	backgroundMouseOnColor.addListener(this);
	strokeMouseOnColor.addListener(this);
	startAngle.addListener(this);
        
        //TRON effects
        outlineStrokeVisible.addListener(this);
        outlineStrokeColor.addListener(this);
        outlineStrokeMouseOnColor.addListener(this);
        outlineStrokeWidth.addListener(this);
        outlineEffect.addListener(this);        
        
	path = new Path();
	moveTo = new MoveTo();
	arcToInner = new ArcTo();
	arcTo = new ArcTo();
	lineTo = new LineTo();
	lineTo2 = new LineTo();

	path.getElements().add(moveTo);
	path.getElements().add(arcToInner);
	path.getElements().add(lineTo);
	path.getElements().add(arcTo);
	path.getElements().add(lineTo2);

        outlinePath = new Path();
        outlinePath.getElements().add(moveTo);
	outlinePath.getElements().add(arcToInner);
	outlinePath.getElements().add(lineTo);
	outlinePath.getElements().add(arcTo);
	outlinePath.getElements().add(lineTo2);

	getChildren().addAll(path, outlinePath);

	setOnMouseEntered(event -> {
            mouseOn = true;
            mouseOnProperty.set(mouseOn);                
            redraw();
	});

	setOnMouseExited(event -> {
            mouseOn = false;
            mouseOnProperty.set(mouseOn);                
            redraw();
	});
    }

    public LitRadialMenuItem(final double menuSize, final Node graphic) {
	this();
        this.menuSize.set(menuSize);
	this.graphic = graphic;
	if (graphic != null)
	    getChildren().add(graphic);
	redraw();
    }

    public LitRadialMenuItem(final double menuSize, final Node graphic,
	    final EventHandler<ActionEvent> actionHandler) {
	this(menuSize, graphic);
	addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            actionHandler.handle(new ActionEvent(event.getSource(), event.getTarget()));
        });
	redraw();
    }

    public LitRadialMenuItem(final double menuSize, final String text, final Node graphic) {
	this(menuSize, graphic);
	this.text = text;
	redraw();
    }

    public LitRadialMenuItem(final double menuSize, final String text,
	    final Node graphic, final EventHandler<ActionEvent> actionHandler) {
	this(menuSize, graphic, actionHandler);
	this.text = text;
	redraw();
    }
    //<editor-fold defaultstate="collapsed" desc="Properties">
    public Path getPath() {
        return path;
    }
    
    public double getMenuSize() {
        return menuSize.get();
    }

    public void setMenuSize(final double width) {
        menuSize.set(width);
    }

    public DoubleProperty menuSizeProperty() {
        return menuSize;
    } 

    DoubleProperty innerRadiusProperty() {
	return innerRadius;
    }

    DoubleProperty radiusProperty() {
	return radius;
    }

    DoubleProperty offsetProperty() {
	return offset;
    }

    ObjectProperty<Paint> backgroundMouseOnColorProperty() {
	return backgroundMouseOnColor;
    }

    ObjectProperty<Paint> backgroundColorProperty() {
	return backgroundColor;
    }

    public BooleanProperty clockwiseProperty() {
	return clockwise;
    }

    ObjectProperty<Paint> strokeMouseOnColorProperty() {
	return strokeMouseOnColor;
    }

    ObjectProperty<Paint> strokeColorProperty() {
	return strokeColor;
    }
    
    DoubleProperty strokeWidthProperty() {
	return strokeWidth;
    }
    
    ObjectProperty<Effect> effect() {
	return effect;
    }    

    ObjectProperty<Paint> outlineStrokeMouseOnColorProperty() {
	return outlineStrokeMouseOnColor;
    }

    ObjectProperty<Paint> outlineStrokeColorProperty() {
	return outlineStrokeColor;
    }
    
    DoubleProperty outlineStrokeWidthProperty() {
	return outlineStrokeWidth;
    }
    
    ObjectProperty<Effect> outlineEffectProperty() {
	return outlineEffect;
    }    
    
    public BooleanProperty strokeVisibleProperty() {
	return strokeVisible;
    }

    public BooleanProperty backgroundVisibleProperty() {
	return backgroundVisible;
    }

    public BooleanProperty outlineStrokeVisibleProperty() {
	return outlineStrokeVisible;
    }

    public Node getGraphic() {
	return graphic;
    }

    public void setStartAngle(final double angle) {
	startAngle.set(angle);
    }

    public DoubleProperty startAngleProperty() {
	return startAngle;
    }

    public void setGraphic(final Node graphic) {
	if (graphic != null) {
	    getChildren().remove(this.graphic);
	}
	this.graphic = graphic;
	if (this.graphic != null) {
	    getChildren().add(graphic);
	}
	redraw();
    }

    public void setText(final String text) {
	this.text = text;
	redraw();
    }

    public String getText() {
	return text;
    }
    //</editor-fold>
    protected void redraw() {
	path.setFill(backgroundVisible.get() ? (mouseOn
            && backgroundMouseOnColor.get() != null ? backgroundMouseOnColor
            .get() : backgroundColor.get())
            : Color.TRANSPARENT);
	path.setStroke(strokeVisible.get() ? (mouseOn
            && strokeMouseOnColor.get() != null ? strokeMouseOnColor
            .get() : strokeColor.get())
            : Color.TRANSPARENT);
        
        path.setStrokeWidth(strokeWidth.get());
        path.setEffect(effect.get());
	path.setFillRule(FillRule.EVEN_ODD);

        outlinePath.setFill(Color.TRANSPARENT);
	outlinePath.setStroke(outlineStrokeVisible.get() ? (mouseOn
            && outlineStrokeMouseOnColor.get() != null ? outlineStrokeMouseOnColor.get() 
            : outlineStrokeColor.get()) : Color.TRANSPARENT);
        outlinePath.setStrokeWidth(outlineStrokeWidth.get());
        outlinePath.setEffect(outlineEffect.get());
        
        computeCoordinates();
	update();
    }

    protected void update() {
	final double innerRadiusValue = innerRadius.get();
	final double radiusValue = radius.get();

	moveTo.setX(innerStartX + translateX);
	moveTo.setY(innerStartY + translateY);

	arcToInner.setX(innerEndX + translateX);
	arcToInner.setY(innerEndY + translateY);
	arcToInner.setSweepFlag(innerSweep);
	arcToInner.setRadiusX(innerRadiusValue);
	arcToInner.setRadiusY(innerRadiusValue);

	lineTo.setX(startX + translateX);
	lineTo.setY(startY + translateY);

	arcTo.setX(endX + translateX);
	arcTo.setY(endY + translateY);
	arcTo.setSweepFlag(sweep);
	arcTo.setRadiusX(radiusValue);
	arcTo.setRadiusY(radiusValue);

	lineTo2.setX(innerStartX + translateX);
	lineTo2.setY(innerStartY + translateY);

	if (graphic != null) {
	    graphic.setTranslateX(graphicX + translateX);
	    graphic.setTranslateY(graphicY + translateY);
	}
    }

    protected void computeCoordinates() {
	final double innerRadiusValue = innerRadius.get();
	final double startAngleValue = startAngle.get();

	final double graphicAngle = startAngleValue + (menuSize.get() / 2.0);
	final double radiusValue = radius.get();

	final double graphicRadius = innerRadiusValue
		+ (radiusValue - innerRadiusValue) / 2.0;

	final double offsetValue = offset.get();

	if (!clockwise.get()) {
	    innerStartX = innerRadiusValue
		    * Math.cos(Math.toRadians(startAngleValue));
	    innerStartY = -innerRadiusValue
		    * Math.sin(Math.toRadians(startAngleValue));
	    innerEndX = innerRadiusValue
		    * Math.cos(Math.toRadians(startAngleValue + menuSize.get()));
	    innerEndY = -innerRadiusValue
		    * Math.sin(Math.toRadians(startAngleValue + menuSize.get()));
	    innerSweep = false;

	    startX = radiusValue * Math.cos(Math.toRadians(startAngleValue + menuSize.get()));
	    startY = -radiusValue * Math.sin(Math.toRadians(startAngleValue + menuSize.get()));
	    endX = radiusValue * Math.cos(Math.toRadians(startAngleValue));
	    endY = -radiusValue * Math.sin(Math.toRadians(startAngleValue));

	    sweep = true;

	    if (graphic != null) {
		graphicX = graphicRadius
			* Math.cos(Math.toRadians(graphicAngle))
			- graphic.getBoundsInParent().getWidth() / 2.0;
		graphicY = -graphicRadius
			* Math.sin(Math.toRadians(graphicAngle))
			- graphic.getBoundsInParent().getHeight() / 2.0;

	    }
	    translateX = offsetValue
		    * Math.cos(Math.toRadians(startAngleValue + (menuSize.get() / 2.0)));
	    translateY = -offsetValue
		    * Math.sin(Math.toRadians(startAngleValue + (menuSize.get() / 2.0)));

	} else if (clockwise.get()) {
	    innerStartX = innerRadiusValue
		    * Math.cos(Math.toRadians(startAngleValue));
	    innerStartY = innerRadiusValue
		    * Math.sin(Math.toRadians(startAngleValue));
	    innerEndX = innerRadiusValue
		    * Math.cos(Math.toRadians(startAngleValue + menuSize.get()));
	    innerEndY = innerRadiusValue
		    * Math.sin(Math.toRadians(startAngleValue + menuSize.get()));

	    innerSweep = true;

	    startX = radiusValue
		    * Math.cos(Math.toRadians(startAngleValue + menuSize.get()));
	    startY = radiusValue
		    * Math.sin(Math.toRadians(startAngleValue + menuSize.get()));
	    endX = radiusValue * Math.cos(Math.toRadians(startAngleValue));
	    endY = radiusValue * Math.sin(Math.toRadians(startAngleValue));

	    sweep = false;

	    if (graphic != null) {
		graphicX = graphicRadius
			* Math.cos(Math.toRadians(graphicAngle))
			- graphic.getBoundsInParent().getWidth() / 2.0;
		graphicY = graphicRadius
			* Math.sin(Math.toRadians(graphicAngle))
			- graphic.getBoundsInParent().getHeight() / 2.0;
	    }

	    translateX = offsetValue
		    * Math.cos(Math.toRadians(startAngleValue + (menuSize.get() / 2.0)));
	    translateY = offsetValue
		    * Math.sin(Math.toRadians(startAngleValue + (menuSize.get() / 2.0)));
	}
        //Update coordinate dependent properties
        startXProperty.set(startX);
        startYProperty.set(startY);
        endXProperty.set(endX);
        endYProperty.set(endY);
        translateXProperty.set(translateX);
        translateYProperty.set(translateY);
        
    }
    
    @Override
    public void changed(final ObservableValue<? extends Object> arg0,
	    final Object arg1, final Object arg2) {
	redraw();
    }

    void setSelected(final boolean selected) {

    }

    boolean isSelected() {
	return false;
    }
}
