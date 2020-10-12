package lit.litfx.controls.output;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
public class AnimatedText extends Text {
    public static String DEFAULT_FONT = "Consolas Bold";
    public static double DEFAULT_FONT_SIZE = 16.0;
    public static Color DEFAULT_COLOR = Color.GREEN;
    public static enum ANIMATION_STYLE {TYPED} //exploding, particle, fadein
    private ANIMATION_STYLE animationStyle;
    private String textString;
    private double animationTimeMS = 30;
    
    public AnimatedText() {
        this("", new Font(DEFAULT_FONT, DEFAULT_FONT_SIZE), DEFAULT_COLOR, ANIMATION_STYLE.TYPED);
    }
    public AnimatedText(String textString, Font font, Color color, ANIMATION_STYLE animationStyle) {
        super(textString);
        this.textString = textString;
        this.animationStyle = animationStyle;
        setFont(font);
        setFill(color);
        getStyleClass().add("litlog-text");        
    }
    public void animate() {
        switch(getAnimationStyle()) {
            case TYPED: animateTyped();
        }
    }
    private void animateTyped() {
        
        final IntegerProperty i = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        String animatedString = getText();
        KeyFrame keyFrame1 = new KeyFrame( Duration.millis(getAnimationTimeMS()), event -> {
            if (i.get() > animatedString.length()) {
                timeline.stop();
            } else {
                setText(animatedString.substring(0, i.get()));
                i.set(i.get() + 1);
            }
        });
        timeline.getKeyFrames().addAll(keyFrame1);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }    

    /**
     * @return the animationStyle
     */
    public ANIMATION_STYLE getAnimationStyle() {
        return animationStyle;
    }

    /**
     * @param animationStyle the style to set
     */
    public void setStyle(ANIMATION_STYLE animationStyle) {
        this.animationStyle = animationStyle;
    }

    /**
     * @return the textString
     */
    public String getTextString() {
        return textString;
    }

    /**
     * @param textString the textString to set
     */
    public void setTextString(String textString) {
        this.textString = textString;
    }

    /**
     * @return the animationTimeMS
     */
    public double getAnimationTimeMS() {
        return animationTimeMS;
    }

    /**
     * @param animationTimeMS the animationTimeMS to set
     */
    public void setAnimationTimeMS(double animationTimeMS) {
        this.animationTimeMS = animationTimeMS;
    }
}
