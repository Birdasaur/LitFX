package lit.litfx.components;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lit.litfx.NodeTools;

/**
 *
 * @author phillsm1
 * 
 */
public class ChainLightning extends Group implements AnimatedEffect {

    ArrayList<Bolt> boltList;
    BoltDynamics boltDynamics;
    BoltDynamics loopDynamics;
    
    public ChainLightning(ArrayList<Node> nodeList, BoltDynamics boltDynamics, BoltDynamics loopDynamics) {
        this.boltDynamics = boltDynamics;
        this.loopDynamics = loopDynamics;
        boltList = new ArrayList<>();
        //Should we loop back around the starting Node?
        if(loopDynamics.loopStartNode) {
            ArrayList<EdgePoint> shortArcPoints = NodeTools.shortArcPoints(nodeList.get(0), nodeList.get(1));
            boltList.addAll(buildLoop(shortArcPoints.get(0), nodeList.get(0)));
        }
        //Progress through each Node
        for(int i=0; i<nodeList.size()-1;i++) {
            //transfer bolt
            boltList.add(NodeTools.arcNodes(nodeList.get(i), nodeList.get(i+1), boltDynamics));
            //loop bolts
            ArrayList<EdgePoint> shortPoints = NodeTools.shortArcPoints(nodeList.get(i), nodeList.get(i+1));
            boltList.addAll(buildLoop(shortPoints.get(1), nodeList.get(i)));
            //Stop if we are at the end
            if((i+1) >= nodeList.size())
                break;
        }
        //Should we loop around the end Node?
        if(loopDynamics.loopEndNode) {
            ArrayList<EdgePoint> shortArcPoints = NodeTools.shortArcPoints(
                nodeList.get(nodeList.size()-2), nodeList.get(nodeList.size()-1));
            boltList.addAll(buildLoop(shortArcPoints.get(0), nodeList.get(nodeList.size()-1)));
        }
        getChildren().addAll(boltList);
    }
    
    private ArrayList<Bolt> buildLoop(EdgePoint startPoint, Node node) {
        ArrayList<Bolt> loopBoltList = new ArrayList<>();
        //get list of points
        ArrayList<EdgePoint> startLoopPoints = NodeTools.nodeLoopPoints(startPoint, node);
        //Build a bolt for each point combination
        for(int i=0; i<startLoopPoints.size()-1;i++) {
            loopBoltList.add(new Bolt(startLoopPoints.get(i), startLoopPoints.get(i+1), loopDynamics)); 
            if((i+1)>=startLoopPoints.size())
                break;
        }
        //Complete the circuit
        loopBoltList.add(new Bolt(startLoopPoints.get(startLoopPoints.size()-1), startLoopPoints.get(0), loopDynamics)); 
        return loopBoltList;
    }

    @Override
    public void animate(Duration milliseconds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateLength(int length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setStroke(Color color) {
        boltList.forEach(bolt -> bolt.setStroke(color));
    }

    public void setStrokeWidth(double strokeWidth) {
        boltList.forEach(bolt -> bolt.setStrokeWidth(strokeWidth));
    }
    

}