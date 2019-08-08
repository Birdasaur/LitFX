package lit.litfx.components;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import lit.litfx.Algorithms;

/**
 *
 * @author phillsm1
 * 
 */
public class BranchLightning extends Group {
    public Bolt primaryBolt;
    public ArrayList<Bolt> branchList;
    Color boltColor = Color.ALICEBLUE;
    Color branchColor = Color.STEELBLUE;
    double boltStrokeWidth = 5;
    double branchStrokeWidth = 2;
    int branches;
    double density;
    double sway;
    double jitter;
    double branchAngleLimit = 20.0;
    double envelopeSize = 0.75;
    double envelopeScaler = 0.1;
    SimpleIntegerProperty pointIndexProperty = new SimpleIntegerProperty(0);
    
    public enum Member {PRIMARYBOLT, BRANCH};
    
    public BranchLightning(Point2D start, Point2D end, 
        double density, double sway, double jitter,
        int branches, double branchAngleLimit) {

        this.density = density;
        this.sway = sway;
        this.jitter = jitter;
        this.branches = branches;
        this.branchAngleLimit = branchAngleLimit;
        
        //create the baseline bolt
        primaryBolt = new Bolt(start, end, density, sway, jitter, 0.75, 0.25);
        primaryBolt.setStroke(boltColor);
        primaryBolt.setStrokeWidth(boltStrokeWidth);
        getChildren().add(primaryBolt);
                
        //Determine randomly where the branches should be
        ArrayList<EdgePoint> branchPoints = randomBranchPoints(branches, primaryBolt);
        //For each branch location, generate a new bolt of lightning
        branchList = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<branchPoints.size(); i++) {
            //Get the starting location for the new branch bolt
            int startIndex = primaryBolt.getEdgePoints().indexOf(branchPoints.get(i));
            //Is it the last bolt point?
            if(startIndex >= primaryBolt.getEdgePoints().size()-1) {
                //back everything up by 1
                startIndex = startIndex - 1;
            } 
            EdgePoint startBoltPoint = primaryBolt.getEdgePoints().get(startIndex);
            //Use the actual end point of the original bolt as a "heading" 
            //This allows reasonable looking divergence of the branches
            EdgePoint endBoltPoint = new EdgePoint(1, end);
            //Calculate the actual angle of the line with regard to the screen
            //add a random angle to diverge from the base bolt
            double deltaAngle = ThreadLocalRandom.current().nextDouble(-branchAngleLimit, branchAngleLimit);
            double finalAngleRadians = Algorithms.divergenceAngle(
                startBoltPoint.toPoint2D(), endBoltPoint.toPoint2D(), deltaAngle);
            //System.out.println("baseAngle: " + baseAngle 
            //    + " deltaAngle: " + deltaAngle + " finalAngleRadians: " + finalAngleRadians);
            //Figure out endpoint based on original bolt distance and angle from normal
            Point2D startPoint2D = new Point2D(startBoltPoint.getX(), startBoltPoint.getY());
            double branchLength = random.nextDouble() * startPoint2D.distance(end);
            Point2D endPoint2D = new Point2D(
                startPoint2D.getX() + branchLength * Math.cos(finalAngleRadians), 
                startPoint2D.getY() + branchLength * Math.sin(finalAngleRadians)
            );
            Bolt branch = new Bolt(startPoint2D, endPoint2D, density, sway, jitter, 0.9, 0.1);
            branch.setStroke(branchColor);
            branch.setStrokeWidth(branchStrokeWidth);
            branchList.add(branch);
        }

        getChildren().addAll(branchList);
    }
    
    private ArrayList<EdgePoint> randomBranchPoints(int branches, Bolt bolt) {
        // pick a bunch of random points on the Bolt
        ArrayList<Integer> positions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < branches; i++) {
            positions.add(
                Math.round(random.nextFloat() * (bolt.getEdgePoints().size()-1)));
        }
        ArrayList<EdgePoint> branchPoints = new ArrayList<>();
        positions.stream().sorted().forEach(
            pos -> branchPoints.add(bolt.getEdgePoints().get(pos)));
        
        return branchPoints;
    }

    public void setBoltThickness(double strokeThickness) {
        boltStrokeWidth = strokeThickness;
        primaryBolt.setStrokeWidth(boltStrokeWidth);
    }
    public void setBranchThickness(double strokeThickness) {
        branchStrokeWidth = strokeThickness;
        branchList.forEach(bolt -> bolt.setStrokeWidth(branchStrokeWidth));
    }
    public void setEffect(Effect effect, Member member) {
        if(member == Member.PRIMARYBOLT) {
            primaryBolt.setEffect(effect);
        } else {
            branchList.forEach(branch -> branch.setEffect(effect));
        }
    }
    public void setOpacity(double opacity, Member member) {
        if(member == Member.PRIMARYBOLT) {
            primaryBolt.setOpacity(opacity);
        } else {
            branchList.forEach(branch -> branch.setOpacity(opacity));
        }
    }    
}