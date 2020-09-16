package lit.litfx.core.components;

/**
 *
 * @author Birdasaur
 */
public class SlopeVector {

    private double rise;
    private double run;
    private double magnitude;

    public SlopeVector(double run, double rise, double magnitude) {
        this.rise = rise;
        this.run = run;
        this.magnitude = magnitude;
    }
    /**
     * @return the rise
     */
    public double getRise() {
        return rise;
    }

    /**
     * @param rise the rise to set
     */
    public void setRise(double rise) {
        this.rise = rise;
    }

    /**
     * @return the run
     */
    public double getRun() {
        return run;
    }

    /**
     * @param run the run to set
     */
    public void setRun(double run) {
        this.run = run;
    }

    /**
     * @return the magnitude
     */
    public double getMagnitude() {
        return magnitude;
    }

    /**
     * @param magnitude the magnitude to set
     */
    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }
}
