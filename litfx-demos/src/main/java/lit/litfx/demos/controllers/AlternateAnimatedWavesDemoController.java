package lit.litfx.demos.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

/**
 * FXML Controller class
 *
 * @author cpdea
 */
public class AlternateAnimatedWavesDemoController implements Initializable {
    @FXML
    Pane centerPane;

    @FXML
    Group droidGroup;

    @FXML
    SVGPath droidHead; // make glow

    @FXML
    SVGPath gear1; // rotate clockwise

    @FXML
    SVGPath gear2; // rotate counter clockwise

    @FXML
    TabPane tabPane;

    //Dynamics
    @FXML
    private Slider angleWidthSlider;
    @FXML
    private Slider bandThicknessSlider;
    @FXML
    private Slider gapSpaceSlider;
    @FXML
    Slider glowSlider; // @TODO not wired up.. will fix.
    @FXML
    private ColorPicker beamColorChooser;

    /**
     * Canvas overlay draws the waves on top (parent is a StackPane).
     */
    ResizableCanvas glassOverlay;

    Glow glow = new Glow(1);
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Canvas wave draw area
        // @TODO bug: Can't decrease size of screen. fix: Override layoutChildren() resizable is broke.
        glassOverlay = new ResizableCanvas();
        glassOverlay.widthProperty().bind(centerPane.widthProperty());
        glassOverlay.heightProperty().bind(centerPane.heightProperty());
        glassOverlay.setPickOnBounds(false); // allows you to click to pass through.
        glassOverlay.setMouseTransparent(true);
        glassOverlay.setEffect(glow);

        // add glass overlay
        centerPane.getChildren().add(glassOverlay);

        // animate pulsing droid head
        Glow headGlow = new Glow(0);
        droidGroup.setEffect(headGlow);

        // Let svg path to pass through to Group parent. (fixes hover mouse enter confusion).
        droidHead.setMouseTransparent(false);
        droidHead.setPickOnBounds(true); // allows you to click to pass through.

        // Pulsing head
        KeyValue glowLevel = new KeyValue(headGlow.levelProperty(), 1);
        KeyFrame glowFrame = new KeyFrame(Duration.millis(500), glowLevel);
        Timeline pulseAnim = new Timeline(glowFrame);
        pulseAnim.setCycleCount(Animation.INDEFINITE);
        pulseAnim.setAutoReverse(true);

        // Mouse hover animate
        droidGroup.setOnMouseEntered(me -> pulseAnim.playFromStart());

        // Mouse exits stop animation
        droidGroup.setOnMouseExited(me -> {
            pulseAnim.stop();
            headGlow.setLevel(0);
        });

        // When user clicks on stack pane surface animate the following:
        // gear 1, gear 2,
        centerPane.setOnMouseClicked((MouseEvent event) -> {

            KeyValue gear1Rotate = new KeyValue(gear1.rotateProperty(), 180);
            KeyValue gear2Rotate = new KeyValue(gear2.rotateProperty(), -180);
            KeyFrame gearFrame = new KeyFrame(Duration.millis(500), gear1Rotate, gear2Rotate);
            Timeline gearAnim = new Timeline(gearFrame);
            gearAnim.setCycleCount(2);
            gearAnim.setAutoReverse(true);

            // animate droid head
            gearAnim.playFromStart();

            // generate a brain emission
            generateBrainActivity(event);
        });

        // init with the tron blue
        beamColorChooser.setValue(Color.web("#66dbff"));

        tabPane.setOnMouseEntered(event -> tabPane.requestFocus());
    }    

    protected void generateBrainActivity(MouseEvent event) {
        // get the center of droid head
        double[] centerPt = getCenterOfNodeFromParent(droidGroup);

        // start offset away from droid head
        int initialRadius = (int) Math.max(
                droidGroup.getBoundsInLocal().getWidth(),
                droidGroup.getBoundsInLocal().getHeight());

        int maxRadius = (int) new Point2D(centerPt[0], centerPt[1]).distance(event.getX(), event.getY());
        double vx = event.getX() - centerPt[0];
        double vy = centerPt[1] - event.getY();
        double directionAngle = Math.atan2(vy, vx);

        BrainWaves brainWaves = new BrainWaves(
                centerPt,
                initialRadius,
                maxRadius,
                directionAngle,
                angleWidthSlider.getValue() * (Math.PI/180),
                bandThicknessSlider.getValue(),
                gapSpaceSlider.getValue());

        System.out.println(brainWaves);

        renderWaves(brainWaves);
    }

    double[] getCenterOfNodeFromParent(Node node) {
        double [] centerPt = new double[2];
        centerPt[0] = node.getBoundsInParent().getCenterX();
        centerPt[1] = node.getBoundsInParent().getCenterY();
        return centerPt;
    }

    public static double arcLength(double radius, double angleRad) {
        return radius * angleRad;
    }

    protected void renderWaves(BrainWaves brainWaves) {
        // create a canvas the size of the wave area.
        GraphicsContext gc = glassOverlay.getGraphicsContext2D();

        // clone current state
        BrainWaves currentState = brainWaves.getState();

        // zero out state's maxRadius to later animate
        currentState.maxRadius = currentState.initialRadius;

        gc.clearRect(0,0, glassOverlay.getWidth(), glassOverlay.getHeight());
        gc.save();

        final Color highestGlow = beamColorChooser.getValue();
        final Color mediumDark = highestGlow.darker();
        final Color mediumDarker = mediumDark.darker();
        final Color darkest = mediumDarker.darker();
        final Color[] shades = new Color[] {darkest, mediumDarker, mediumDark, highestGlow};

        gc.setEffect(glow);
        AnimationTimer at = new AnimationTimer() {
            long lastTimerCall = 0;
            final long NANOS_PER_MILLI = 1000000; //nanoseconds in a millisecond
            final long ANIMATION_DELAY = 16 * NANOS_PER_MILLI;
            int bandNumber = 0;
            @Override
            public void handle(long now) {
                if(now > lastTimerCall + ANIMATION_DELAY) {
                    lastTimerCall = now;    //update for the next animation

                    // this makes it grow from initial radius to max
                    currentState.maxRadius += currentState.gapSpace;

                    if (currentState.maxRadius >= brainWaves.maxRadius) {
                        System.out.println("Done.");
                        // clear area.
                        //gc.setEffect(null);
                        gc.restore(); // will restore when effects was null
                        this.stop();
                        return;
                    }

                    // clear area.
                    gc.clearRect(0,0, glassOverlay.getWidth(), glassOverlay.getHeight());

                    // stroke color and thickness
                    Color shade = shades[bandNumber % 3];
                    gc.setStroke(shade);
                    gc.setLineWidth(currentState.bandThickness);

                    // draw one arc (wave/band)
                    int radius = currentState.initialRadius;
                    int diameter = radius * 2;
                    gc.strokeArc(
                        currentState.centerX - radius, // upper left x coord
                        currentState.centerY - radius, // upper left y coord
                        diameter,              // width
                        diameter,              // height
                        currentState.startAngle() * (180/Math.PI),
                        currentState.angleWidth * (180/Math.PI),
                        ArcType.OPEN);

                    bandNumber++; // this helps it determine colors

                    // increment to not redraw previous arc
                    currentState.initialRadius += currentState.gapSpace;
                }
            }
        };
        at.start();
    }
}


class BrainWaves {
    BrainWaves state;

    double centerX;
    double centerY;

    int initialRadius; // begin arc
    int maxRadius;     // end arc distance from center to max

    double directionAngle; // determine the vector direction angle in radians
    double angleWidth = Math.PI/6; // wedge size

    double bandThickness = 2; // band stroke width
    double gapSpace = 5; // space between each band.

    public BrainWaves(double [] centerPt,
                      int initialRadius,
                      int maxRadius,
                      double directionAngle,
                      double angleWidthRad,
                      double bandThickness,
                      double gapSpace) {
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

    public BrainWaves(double [] centerPt,
                      int initialRadius,
                      int maxRadius,
                      double directionAngle) {
        this(centerPt, initialRadius, maxRadius, directionAngle, -1, -1, -1);
    }

    public BrainWaves getState() {
        if (state == null) {
            copyState();
        }
        return state;
    }

    public void copyState() {
        double [] centerPt = new double[2];
        centerPt[0] = this.centerX;
        centerPt[1] = this.centerY;

        int initialRadius = this.initialRadius; // begin arc
        int maxRadius = this.maxRadius;     // end arc distance from center to max

        double directionAngle = this.directionAngle; // determine the vector direction angle in radians
        double angleWidth = this.angleWidth; // wedge size

        double bandThickness = this.bandThickness; // band stroke width

        double gapSpace = this.gapSpace; // space between each band.

        state = new BrainWaves(centerPt,
                initialRadius,
                maxRadius,
                directionAngle,
                angleWidth,
                bandThickness,
                gapSpace);
    }
    /**
     * Zero degrees is at 3 o'clock. Going clockwise is negative degrees as start.
     * Going counter clockwise is positive degrees.
     * This is like geometry class the first quadrant from zero degrees is counter clockwise direction
     * or a positive degrees. This will take the angle's width divide by half from center of beam
     * or arc (direction of mouse click)
     * @return
     */
    public double startAngle() {
        return directionAngle - (angleWidth/2);
    }

    public double endAngle() {
        return directionAngle + (angleWidth/2);
    }

    @Override
    public String toString(){
        BiFunction<String, Object, String> row = (label, value) ->
                String.format(" %s: %s\n", label, String.valueOf(value));

        return new StringBuilder()
                .append(row.apply("centerX", centerX))
                .append(row.apply("centerY", centerY))
                .append(row.apply("initialRadius", initialRadius))
                .append(row.apply("maxRadius", maxRadius))
                .append(row.apply("directionAngle", directionAngle))
                .append(row.apply("angleWidth", angleWidth))
                .toString();

    }
}

class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
