package lit.litfx.core.components;

import java.util.Random;

/**
 *
 * @author Birdasaur
 */
public class CircleQuadBandCreator extends QuadBandCreator{

    private double initialRadius;    
    public CircleQuadBandCreator(int initialRadius) {
        this(10, 5, 0, 0, 1.0, initialRadius);
    }
    public CircleQuadBandCreator(int polygonPoints, double edgeVariation, 
        double generatorCenterX, double generatorCenterY, double velocity, double initialRadius) {
        super(polygonPoints, edgeVariation, generatorCenterX, generatorCenterY, velocity);
        this.initialRadius = initialRadius;
    }

    @Override
    public Band createQuadBand() {
        //create a random polygon around the center point
        Random random = new Random();
        double [] doubles = new double[getPolygonPoints()*2];
        double angleRadiansSlice  = 6.283185 / getPolygonPoints(); 
        double randomLength, angleRadians = 0.0;

        //get a randomly perturbed length based on a set radius
        randomLength = getInitialRadius() + (getEdgeVariation() * random.nextGaussian()); 
        //setup first segment with a radian angle of 0.0
        doubles[0] = getGeneratorCenterX() + (Math.cos(angleRadians) * randomLength);
        doubles[1] = getGeneratorCenterY() + (Math.sin(angleRadians) * randomLength);
        //@DEBUG SMP create a circle showing the first point
        //getChildren().add(new Circle(doubles[0], doubles[1], 5.0, 
        //    Color.GREEN.deriveColor(1, 1, 1, 0.7)));
        //For each point, compute the coordinats    
        for(int i=1;i<getPolygonPoints();i++) {
            //jog the angle forward
            angleRadians += angleRadiansSlice;
            //get a new random length
            randomLength = getInitialRadius() + (getEdgeVariation() * random.nextGaussian()); 
            //create new end point
            doubles[2*i] = getGeneratorCenterX() + (Math.cos(angleRadians) * randomLength);
            doubles[2*i+1] = getGeneratorCenterY() + (Math.sin(angleRadians) * randomLength);
            //@DEBUG SMP Test circle to visualize points
            //getChildren().add(new Circle(doubles[2*i], doubles[2*i+1], 3.0, 
            //    Color.ALICEBLUE.deriveColor(1, 1, 1, 0.7)));
        }
        return new Band(getGeneratorCenterX(), getGeneratorCenterY(), getVelocity(), doubles);
    }

    /**
     * @return the intialRadius
     */
    public double getInitialRadius() {
        return initialRadius;
    }

    /**
     * @param initialRadius the intialRadius to set
     */
    public void setInitialRadius(double initialRadius) {
        this.initialRadius = initialRadius;
    }
}