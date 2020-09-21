package lit.litfx.core.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.shape.Path;
import lit.litfx.core.utils.Utils;

/**
 *
 * @author Birdasaur
 */
public class PathQuadBandCreator extends QuadBandCreator{
    
    List<Point2D> points;
    public PathQuadBandCreator(Path path, double edgeVariation, 
        double generatorCenterX, double generatorCenterY, double velocity) {
        super(0, edgeVariation, generatorCenterX, generatorCenterY, velocity);
        points = Utils.pathToPoints(path);
        setPolygonPoints(points.size());

    }

    @Override
    public Band createQuadBand() {
        //create a random polygon around the center point
        Random random = new Random();
        double [] doubles = new double[getPolygonPoints()*2];
        Point2D point2D;
        //get a randomly perturbed length based on a set radius
//        randomLength = getEdgeVariation() * random.nextGaussian(); 
        //@DEBUG SMP create a circle showing the first point
        //getChildren().add(new Circle(doubles[0], doubles[1], 5.0, 
        //    Color.GREEN.deriveColor(1, 1, 1, 0.7)));
        //For each point, compute the coordinats    
        for(int i=1;i<getPolygonPoints();i++) {
            //add a new random length
            point2D = points.get(i).add(
                getEdgeVariation() * random.nextGaussian(), 
                getEdgeVariation() * random.nextGaussian());
            //create new end point
            doubles[2*i] = point2D.getX();
            doubles[2*i+1] = point2D.getY();
            //@DEBUG SMP Test circle to visualize points
            //getChildren().add(new Circle(doubles[2*i], doubles[2*i+1], 3.0, 
            //    Color.ALICEBLUE.deriveColor(1, 1, 1, 0.7)));
        }
        return new Band(getGeneratorCenterX(), getGeneratorCenterY(), getVelocity(), doubles);
    }
}