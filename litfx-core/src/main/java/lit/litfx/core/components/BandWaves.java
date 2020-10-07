package lit.litfx.core.components;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.function.BiFunction;

/**
 * Band Waves is an animation of arcs drawn on a canvas.
 * @author cpdea
 */
public class BandWaves {

    private Canvas canvas;

    private ObjectProperty<Color> bandColor = new SimpleObjectProperty<>(Color.AZURE);

    private DoubleProperty glowLevel = new SimpleDoubleProperty(1.0);

    // This get generated in the constructor and is derived by the bandColor
    private Color[] shades;

    // This BandWave object will be cloned as the current state.
    private BandWaves state;

    private double centerX;
    private double centerY;

    private int initialRadius; // begin arc
    private int maxRadius;     // end arc distance from center to max

    private double directionAngle; // determine the vector direction angle in radians
    private double angleWidth = Math.PI / 6; // wedge size

    private double bandThickness = 2; // band stroke width
    private double gapSpace = 5; // space between each band.

    public BandWaves(Canvas canvas) {
        this.canvas = canvas;
        this.canvas.setEffect(new Glow(glowLevel.doubleValue()));

        buildColorShades(); // update shades array of colors

        bandColor.addListener(obs -> buildColorShades());
    }

    public BandWaves(Canvas canvas,
                     double[] centerPt,
                     int initialRadius,
                     int maxRadius,
                     double directionAngle,
                     double angleWidthRad,
                     double bandThickness,
                     double gapSpace) {
        this(canvas);
        centerX = centerPt[0];
        centerY = centerPt[1];
        this.initialRadius = initialRadius;
        this.maxRadius = maxRadius;
        this.directionAngle = directionAngle;
        if (angleWidthRad > -1) {
            this.angleWidth = angleWidthRad;
        }
        if (bandThickness > -1) {
            this.bandThickness = bandThickness;
        }
        if (gapSpace > -1) {
            this.gapSpace = gapSpace;
        }


    }

    public BandWaves(Canvas canvas,
                     double[] centerPt,
                     int initialRadius,
                     int maxRadius,
                     double directionAngle) {
        this(canvas, centerPt, initialRadius, maxRadius, directionAngle, -1, -1, -1);
    }

    public void buildColorShades() {
        Color highestGlow = bandColor.get();
        Color mediumDark = highestGlow.darker();
        Color mediumDarker = mediumDark.darker();
        Color darkest = mediumDarker.darker();
        shades = new Color[]{darkest, mediumDarker, mediumDark, highestGlow};
    }

    public void animate() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        BandWaves state = getState();

        // zero out state's maxRadius to later animate
        state.setMaxRadius(state.getInitialRadius());

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();

        gc.setEffect(new Glow(state.getGlowLevel()));

        AnimationTimer at = new AnimationTimer() {
            long lastTimerCall = 0;
            final long NANOS_PER_MILLI = 1000000; //nanoseconds in a millisecond
            final long ANIMATION_DELAY = 16 * NANOS_PER_MILLI;
            int bandNumber = 0;
            int lastRadius = state.getInitialRadius();

            @Override
            public void handle(long now) {
                if (now > lastTimerCall + ANIMATION_DELAY) {
                    lastTimerCall = now;    //update for the next animation

                    // this makes it grow from initial radius to max
                    state.increaseMaxRadiusByGapSpace();
                    if (state.getMaxRadius() > getMaxRadius()) {
                        System.out.println("Done.");
                        // clear area.
                        //gc.setEffect(null);
                        gc.restore(); // will restore when effects was null
                        this.stop();
                        return;
                    }

                    // clear area.
                    //gc.clearRect(0,0, glassOverlay.getWidth(), glassOverlay.getHeight());

                    // stroke color and thickness
                    Color[] shades = state.getShades();
                    Color shade = shades[bandNumber % shades.length];
                    gc.setStroke(shade);
                    gc.setLineWidth(state.getBandThickness());


                    // draw one arc (wave/band)
                    int radius = state.getInitialRadius();
                    int diameter = radius * 2;
                    gc.strokeArc(
                            state.getCenterX() - radius, // upper left x coord
                            state.getCenterY() - radius, // upper left y coord
                            diameter,              // width
                            diameter,              // height
                            state.startAngle() * (180 / Math.PI),
                            state.getAngleWidth() * (180 / Math.PI),
                            ArcType.OPEN);

                    bandNumber++; // this helps it determine colors

                    // increment to not redraw previous arc
                    state.increaseInitialRadiusByGapSpace();
                }
            }
        };
        at.start();
    }

    public BandWaves getState() {
        if (state == null) {
            copyState();
        }
        return state;
    }

    /**
     * Copy current state of this object. Used for animating (interpolation) begin to end states.
     */
    public void copyState() {
        double[] centerPt = new double[2];
        centerPt[0] = this.centerX;
        centerPt[1] = this.centerY;

        int initialRadius = this.initialRadius; // begin arc
        int maxRadius = this.maxRadius;     // end arc distance from center to max

        double directionAngle = this.directionAngle; // determine the vector direction angle in radians
        double angleWidth = this.angleWidth; // wedge size

        double bandThickness = this.bandThickness; // band stroke width

        double gapSpace = this.gapSpace; // space between each band.

        state = new BandWaves(this.canvas,
                centerPt,
                initialRadius,
                maxRadius,
                directionAngle,
                angleWidth,
                bandThickness,
                gapSpace);
        state.bandColor.set(this.bandColor.get());
        state.setGlowLevel(this.glowLevel.get());
    }

    /**
     * Zero degrees is at the 3 o'clock position. Going clockwise from zero degrees would be
     * negative degrees to start. When going in a counter clockwise direction the angle would
     * be positive value in degrees relative to zero degrees (at the 3 o'clock position).
     * This is like geometry class the first quadrant from zero degrees is counter clockwise
     * direction or a positive degrees. This will take the angle's width divide by half from
     * center of arc (direction of mouse click).
     * <p>
     * ie. You want an arc drawn with an arc width of 40 degrees centered at the zero degree position.
     * If a mouse clicked on the zero degrees position, an arc angle width of 40 degrees will begin
     * at -20 degrees to +20 degrees in the counter clockwise.
     *
     * @return double
     */
    public double startAngle() {
        return directionAngle - (angleWidth / 2);
    }

    public Color[] getShades() {
        return shades;
    }

    public double endAngle() {
        return directionAngle + (angleWidth / 2);
    }

    public Color getBandColor() {
        return bandColor.get();
    }

    public ObjectProperty<Color> bandColorProperty() {
        return bandColor;
    }

    public void setBandColor(Color bandColor) {
        this.bandColor.set(bandColor);
    }

    public double getGlowLevel() {
        return glowLevel.get();
    }

    public DoubleProperty glowLevelProperty() {
        return glowLevel;
    }

    public void setGlowLevel(double glowLevel) {
        this.glowLevel.set(glowLevel);
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public int getInitialRadius() {
        return initialRadius;
    }

    public void setInitialRadius(int initialRadius) {
        this.initialRadius = initialRadius;
    }

    public int getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
    }

    public double getDirectionAngle() {
        return directionAngle;
    }

    public void setDirectionAngle(double directionAngle) {
        this.directionAngle = directionAngle;
    }

    public double getAngleWidth() {
        return angleWidth;
    }

    public void setAngleWidth(double angleWidth) {
        this.angleWidth = angleWidth;
    }

    public double getBandThickness() {
        return bandThickness;
    }

    public void setBandThickness(double bandThickness) {
        this.bandThickness = bandThickness;
    }

    public double getGapSpace() {
        return gapSpace;
    }

    public void setGapSpace(double gapSpace) {
        this.gapSpace = gapSpace;
    }

    private static BiFunction<String, Object, String> row = (label, value) ->
            String.format(" %s: %s\n", label, value);

    private static String out(String name, Object value) {
        if (value instanceof Object[]) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Object obj : (Object[]) value) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(obj);
                i++;
            }
            return row.apply(name, sb.toString());
        }
        return row.apply(name, value);
    }

    private static String sb(String... args) {
        StringBuilder sb = new StringBuilder();
        for (String pair : args) {
            sb.append(pair);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return sb(
                out("bandColorProperty", bandColor.get()),
                out("shades", shades),
                out("centerX", centerX),
                out("centerY", centerY),
                out("initialRadius", initialRadius),
                out("maxRadius", maxRadius),
                out("directionAngle", directionAngle),
                out("angleWidth", angleWidth),
                out("bandThickness", bandThickness),
                out("gapSpace", gapSpace)
        );
    }

    public void increaseMaxRadiusByGapSpace() {
        maxRadius += gapSpace;
    }

    public void increaseInitialRadiusByGapSpace() {
        initialRadius += gapSpace;
    }
}
