package lit.litfx.core.components;

import java.util.ArrayList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lit.litfx.core.NodeTools;

/**
 *
 * @author Birdasaur
 * 
 */
public class ChainLightning extends Group implements AnimatedEffect {

    ArrayList<Bolt> boltList;
    BoltDynamics boltDynamics;
    BoltDynamics loopDynamics;
    SimpleIntegerProperty boltIndexProperty = new SimpleIntegerProperty(0);
    SimpleBooleanProperty animating = new SimpleBooleanProperty(false);

    public SimpleLongProperty animationSleepMilli = new SimpleLongProperty(10);
    public SimpleDoubleProperty boltAnimationDuration = new SimpleDoubleProperty(10);
    public SimpleLongProperty transitionDelayMilli = new SimpleLongProperty(10);
    public SimpleIntegerProperty tailLength = new SimpleIntegerProperty(4);
    
    Task animationTask;
    
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

    public void animate(Duration boltDurationMilli, long transitionDelayMilli, long animationSleepMilli) {
        this.transitionDelayMilli.set(transitionDelayMilli);
        this.animationSleepMilli.set(animationSleepMilli);
        animate(boltDurationMilli);
    }
    
    @Override
    public void animate(Duration milliseconds) {
        //Since ChainLightning is really just a bunch of bolts, we need call each
        //bolt's animation request in order, and wait for it to finish before
        //proceeding to the next bolt.
        animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                //check if the current animation is finished
                if(!animating.get()) {
                    animating.set(true);
                    //for each bolt in the list
                    for(int i=0; i<boltList.size(); i++){
                        Bolt currentBolt = boltList.get(i);
                        currentBolt.setVisible(true);
                        System.out.println("animating next chainlighting arc...");
                        currentBolt.animate(milliseconds);
                        while(currentBolt.isAnimating())
                            //Thread.onSpinWait();
                            Thread.sleep(animationSleepMilli.get());
                        System.out.println("chainlighting arc animation complete.");
                        if((i - tailLength.get()) >= 0)
                            boltList.get(i - tailLength.get()).setVisible(false);
                        if(this.isCancelled() || this.isDone())
                            break;

                        //How long should we wait until we animate the next bolt?
                        Thread.sleep(transitionDelayMilli.get());                    
                    }
                    animating.set(false);
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();  
    }
    @Override
    public void stop() {
        animationTask.cancel();

    }

    @Override
    public void updateLength(int length) {
        setVisibleLength(length);
    }    

    public void setVisibleLength(int visibleLength) {
        boltIndexProperty.set(visibleLength);
        for(int i=0; i < boltList.size(); i++) {
            boltList.get(i).setVisible(i < visibleLength);
        }        
    }
    public int getVisibleLength() {
        return boltIndexProperty.get();
    }
    
    public void setStroke(Color color) {
        boltList.forEach(bolt -> bolt.setStroke(color));
    }

    public void setStrokeWidth(double strokeWidth) {
        boltList.forEach(bolt -> bolt.setStrokeWidth(strokeWidth));
    }

    @Override
    public boolean isAnimating() {
        return animating.get();
    }
}