package lit.litfx.core.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * A builder class of a BandWaves object.
 * @author cpdea
 */

public class BandWavesBuilder {

    private BandWaves bandWaves;

    private BandWavesBuilder(BandWaves bandWaves) {
        this.bandWaves = bandWaves;
    }

    public static BandWavesBuilder create(Canvas canvas) {
        return new BandWavesBuilder(new BandWaves(canvas));
    }

    public BandWavesBuilder bandColor(Color bandColor) {
        bandWaves.setBandColor(bandColor);
        return this;
    }

    public BandWavesBuilder glowLevel(double level) {
        bandWaves.setGlowLevel(level);
        return this;
    }

    public BandWavesBuilder centerPt(double[] centerPt) {
        bandWaves.setCenterX(centerPt[0]);
        bandWaves.setCenterY(centerPt[1]);
        return this;
    }
    public BandWavesBuilder centerX(double centerX) {
        bandWaves.setCenterX(centerX);
        return this;
    }
    public BandWavesBuilder centerY(double centerY) {
        bandWaves.setCenterY(centerY);
        return this;
    }
    public BandWavesBuilder initialRadius(int initialRadius) {
        bandWaves.setInitialRadius(initialRadius);
        return this;
    }
    public BandWavesBuilder maxRadius(int maxRadius) {
        bandWaves.setMaxRadius(maxRadius);
        return this;
    }

    public BandWavesBuilder directionAngle(double directionAngle) {
        bandWaves.setDirectionAngle(directionAngle);
        return this;
    }
    public BandWavesBuilder angleWidthDegrees(double angleWidth) {
        bandWaves.setAngleWidth(angleWidth * (Math.PI/180));
        return this;
    }
    public BandWavesBuilder angleWidthRadians(double angleWidth) {
        bandWaves.setAngleWidth(angleWidth);
        return this;
    }

    public BandWavesBuilder bandThickness(double bandThickness) {
        bandWaves.setBandThickness(bandThickness);
        return this;
    }
    public BandWavesBuilder gapSpace(double gapSpace) {
        bandWaves.setGapSpace(gapSpace);
        return this;
    }

    public BandWaves build() {
        return bandWaves;
    }
}
