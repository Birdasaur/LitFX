package lit.litfx.components;

import java.util.ArrayList;
import javafx.scene.Node;
import lit.litfx.NodeTools;

/**
 *
 * @author phillsm1
 * 
 */
public class ChainLightning {

    ArrayList<Bolt> boltList;
    BoltDynamics dynamics;
    
    public ChainLightning(ArrayList<Node> nodeList, BoltDynamics dynamics) {
        this.dynamics = dynamics;
        //Should we loop back around the starting Node?
        if(dynamics.loopStartNode) {
            ArrayList<EdgePoint> shortArcPoints = NodeTools.shortArcPoints(nodeList.get(0), nodeList.get(1));
            boltList.addAll(buildLoop(shortArcPoints.get(0), nodeList.get(0)));
        }
        //Progress through each Node
        for(int i=0; i<nodeList.size();i++) {
            //transfer bolt
            boltList.add(NodeTools.arcNodes(nodeList.get(i), nodeList.get(i+1), dynamics));
            //loop bolts
            ArrayList<EdgePoint> shortPoints = NodeTools.shortArcPoints(nodeList.get(i), nodeList.get(i+1));
            boltList.addAll(buildLoop(shortPoints.get(1), nodeList.get(i)));
            //Stop if we are at the end
            if((i+1) > nodeList.size())
                break;
        }
        //Should we loop around the end Node?
        if(dynamics.loopEndNode) {
            ArrayList<EdgePoint> shortArcPoints = NodeTools.shortArcPoints(
                nodeList.get(nodeList.size()-1), nodeList.get(nodeList.size()));
            boltList.addAll(buildLoop(shortArcPoints.get(0), nodeList.get(0)));
        }
    }
    
    private ArrayList<Bolt> buildLoop(EdgePoint startPoint, Node node) {
        ArrayList<Bolt> loopBoltList = new ArrayList<>();
        //get list of points
        ArrayList<EdgePoint> startLoopPoints = NodeTools.nodeLoopPoints(startPoint, node);
        //Build a bolt for each point combination
        for(int i=0; i<startLoopPoints.size();i++) {
            loopBoltList.add(new Bolt(startLoopPoints.get(i), startLoopPoints.get(i+1), dynamics)); 
            if((i+1)>=startLoopPoints.size())
                break;
        }
        //Complete the circuit
        loopBoltList.add(new Bolt(startLoopPoints.get(startLoopPoints.size()), startLoopPoints.get(0), dynamics)); 
        return loopBoltList;
    }
    

}