package lit.litfx.demos.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lit.litfx.core.components.BandWaves;
import lit.litfx.core.components.BandWavesBuilder;
import lit.litfx.core.components.CanvasOverlayPane;
import lit.litfx.core.components.DroidGroup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author cpdea
 */
public class AlternateAnimatedWavesDemoController implements Initializable {
    /**
     * center pane is a StackPane to take up available width and height of canvas overlay
     */
    @FXML
    Pane centerPane;

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
    Slider glowSlider;
    @FXML
    private ColorPicker beamColorChooser;

    /**
     * Canvas overlay draws the waves on top (parent is a StackPane).
     */
    CanvasOverlayPane glassOverlay = new CanvasOverlayPane();

    /**
     * Droid Group control
     */
    DroidGroup droidGroup = new DroidGroup();

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // add glass overlay
        centerPane.getChildren().addAll(droidGroup, glassOverlay);

        // When user clicks on stack pane surface animate the following:
        // gear 1, gear 2,
        centerPane.setOnMouseClicked((MouseEvent event) -> {
            // animate droid head
            droidGroup.animateGears();

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

        // find the direction from droid center to mouse click pt
        double vx = event.getX() - centerPt[0];
        double vy = centerPt[1] - event.getY();
        double directionAngle = Math.atan2(vy, vx);

        BandWaves bandWaves = BandWavesBuilder
                .create(glassOverlay.getCanvas())
                .centerPt(centerPt)
                .initialRadius(initialRadius)
                .maxRadius(maxRadius)
                .directionAngle(directionAngle)
                .angleWidthDegrees(angleWidthSlider.getValue())
                .bandThickness(bandThicknessSlider.getValue())
                .gapSpace(gapSpaceSlider.getValue())
                .bandColor(beamColorChooser.getValue())
                .glowLevel(glowSlider.getValue())
                .build();

        System.out.println(bandWaves);

        bandWaves.animate();
    }

    double[] getCenterOfNodeFromParent(Node node) {
        double [] centerPt = new double[2];
        centerPt[0] = node.getBoundsInParent().getCenterX();
        centerPt[1] = node.getBoundsInParent().getCenterY();
        return centerPt;
    }
}


