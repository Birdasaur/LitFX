/**
 * Below is the original source code license header from the original version
 * found in JFXtras Labs. 
 * 
 * RadialContainerMenuItem.java
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
 * Adapted From Mr. LoNee's awesome RadialMenu example. Source for original 
 * prototype can be found in JFXtras-labs project.
 * https://github.com/JFXtras/jfxtras-labs
 */
package lit.litfx.controls.menus;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class LitRadialContainerMenuItem extends LitRadialMenuItem {

    protected boolean selected = false;
    private final Group childAnimGroup = new Group();
    private FadeTransition fadeIn = null;
    private FadeTransition fadeOut = null;
    protected List<LitRadialMenuItem> items = new ArrayList<LitRadialMenuItem>();
    protected Polyline arrow = new Polyline(-5.0, -5.0, 5.0, 0.0, -5.0, 5.0, -5.0, -5.0);

    public LitRadialContainerMenuItem(final double menuSize, final Node graphic) {
	super(menuSize, graphic);
	initialize();
    }

    public LitRadialContainerMenuItem(final double menuSize, final String text,
	    final Node graphic) {
	super(menuSize, text, graphic);
	initialize();
    }

    private void initialize() {
        arrow.setFill(Color.GRAY);
        arrow.setStroke(null);
	childAnimGroup.setVisible(false);
	visibleProperty().addListener(new ChangeListener<Boolean>() {
	    @Override
	    public void changed(final ObservableValue<? extends Boolean> arg0,
		    final Boolean arg1, final Boolean arg2) {
		if (!arg0.getValue()) {
		    childAnimGroup.setVisible(false);
		    setSelected(false);
		}
	    }
	});
	getChildren().add(childAnimGroup);
	fadeIn = new FadeTransition(Duration.millis(400), childAnimGroup);
	fadeIn.setFromValue(0.0);
	fadeIn.setToValue(1.0);
	fadeOut = new FadeTransition(Duration.millis(400), childAnimGroup);
	fadeOut.setFromValue(0.0);
	fadeOut.setToValue(1.0);
	fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                childAnimGroup.setVisible(false);
            }
        });
	getChildren().add(arrow);
    }

    public void addMenuItem(final LitRadialMenuItem item) {
	item.backgroundColorProperty().bind(backgroundColor);
	item.backgroundMouseOnColorProperty().bind(backgroundMouseOnColor);
	item.innerRadiusProperty().bind(radius);
	item.radiusProperty().bind(
		radius.multiply(2).subtract(innerRadius));
	item.offsetProperty().bind(offset.multiply(2.0));
	item.strokeColorProperty().bind(strokeColor);
        item.strokeWidthProperty().bind(strokeWidth);
	item.clockwiseProperty().bind(clockwise);
	item.backgroundVisibleProperty().bind(backgroundVisible);
	item.strokeVisibleProperty().bind(strokeVisible);
	items.add(item);
	childAnimGroup.getChildren().add(item);
	double currentOffset = 0;
	for (final LitRadialMenuItem it : items) {
	    it.startAngleProperty().bind(startAngleProperty().add(currentOffset));
	    currentOffset += it.getMenuSize();
	}
    }

    public void removeMenuItem(final LitRadialMenuItem item) {
	items.remove(item);
	childAnimGroup.getChildren().remove(item);
	item.backgroundColorProperty().unbind();
	item.backgroundMouseOnColorProperty().unbind();
	item.innerRadiusProperty().unbind();
	item.radiusProperty().unbind();
	item.offsetProperty().unbind();
	item.strokeColorProperty().unbind();
        item.strokeWidthProperty().unbind();        
	item.clockwiseProperty().unbind();
	item.backgroundVisibleProperty().unbind();
	item.strokeVisibleProperty().unbind();
    }

    public void removeMenuItem(final int itemIndex) {
	final LitRadialMenuItem item = items.get(itemIndex);
	removeMenuItem(item);
    }

    @Override
    protected void redraw() {
	super.redraw();
	if (selected) {
	    path.setFill(backgroundVisible.get() ? (selected
                && backgroundMouseOnColor.get() != null ? backgroundMouseOnColor
                .get() : backgroundColor.get())
                : null);
	}
	if (arrow != null) {
	    arrow.setFill(backgroundVisible.get() ? (mouseOn
		    && strokeColor.get() != null ? strokeColor.get()
		    : strokeColor.get()) : null);
	    arrow.setStroke(strokeVisible.get() ? strokeColor.get() : null);
	    if (!clockwise.get()) {
		arrow.setRotate(-(startAngle.get() + menuSize.get() / 2.0));
		arrow.setTranslateX((radius.get() - arrow
			.getBoundsInLocal().getWidth() / 2.0)
			* Math.cos(Math.toRadians(startAngle.get()
				+ menuSize.get() / 2.0)) + translateX);
		arrow.setTranslateY(-(radius.get() - arrow
			.getBoundsInLocal().getHeight() / 2.0)
			* Math.sin(Math.toRadians(startAngle.get()
				+ menuSize.get() / 2.0)) + translateY);
	    } else {
		arrow.setRotate(startAngle.get() + menuSize.get()
			/ 2.0);
		arrow.setTranslateX((radius.get() - arrow
			.getBoundsInLocal().getWidth() / 2.0)
			* Math.cos(Math.toRadians(startAngle.get()
				+ menuSize.get() / 2.0)) + translateX);
		arrow.setTranslateY((radius.get() - arrow
			.getBoundsInLocal().getHeight() / 2.0)
			* Math.sin(Math.toRadians(startAngle.get()
				+ menuSize.get() / 2.0)) + translateY);
	    }
	}
    }

    @Override
    void setSelected(final boolean selected) {
	this.selected = selected;
	if (selected) {
	    double startOpacity = 0;
	    if (fadeOut.getStatus() == Status.RUNNING) {
		fadeOut.stop();
		startOpacity = childAnimGroup.getOpacity();
	    }
	    // draw Children
	    childAnimGroup.setOpacity(startOpacity);
	    childAnimGroup.setVisible(true);
	    fadeIn.fromValueProperty().set(startOpacity);
	    fadeIn.playFromStart();
	} else {
	    // draw Children
	    double startOpacity = 1.0;
	    if (fadeIn.getStatus() == Status.RUNNING) {
		fadeIn.stop();
		startOpacity = childAnimGroup.getOpacity();
	    }
	    fadeOut.fromValueProperty().set(startOpacity);
	    fadeOut.playFromStart();
	}
	redraw();
    }

    @Override
    boolean isSelected() {
	return selected;
    }

}
