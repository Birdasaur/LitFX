
package lit.litfx.core.components;

/**
 *
 * @author Birdasaur
 */
public abstract class QuadBandCreator {
    private int polygonPoints;
    private double edgeVariation;
    private double generatorCenterX;
    private double generatorCenterY;
    private double velocity;

    public QuadBandCreator(int polygonPoints, double edgeVariation, 
        double generatorCenterX, double generatorCenterY, double velocity) {
        this.polygonPoints = polygonPoints;
        this.edgeVariation = edgeVariation;
        this.generatorCenterX = generatorCenterX;
        this.generatorCenterY = generatorCenterY;
        this.velocity = velocity;
    }
    public abstract Band createQuadBand();

    /**
     * @return the polygonPoints
     */
    public int getPolygonPoints() {
        return polygonPoints;
    }

    /**
     * @param polygonPoints the polygonPoints to set
     */
    public void setPolygonPoints(int polygonPoints) {
        this.polygonPoints = polygonPoints;
    }

    /**
     * @return the edgeVariation
     */
    public double getEdgeVariation() {
        return edgeVariation;
    }

    /**
     * @param edgeVariation the edgeVariation to set
     */
    public void setEdgeVariation(double edgeVariation) {
        this.edgeVariation = edgeVariation;
    }

    /**
     * @return the generatorCenterX
     */
    public double getGeneratorCenterX() {
        return generatorCenterX;
    }

    /**
     * @param generatorCenterX the generatorCenterX to set
     */
    public void setGeneratorCenterX(double generatorCenterX) {
        this.generatorCenterX = generatorCenterX;
    }

    /**
     * @return the generatorCenterY
     */
    public double getGeneratorCenterY() {
        return generatorCenterY;
    }

    /**
     * @param generatorCenterY the generatorCenterY to set
     */
    public void setGeneratorCenterY(double generatorCenterY) {
        this.generatorCenterY = generatorCenterY;
    }

    /**
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
}
