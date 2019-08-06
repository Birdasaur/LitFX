package lit.litfx;

import java.util.Collection;
import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lit.litfx.components.AnimatedEdge;
import lit.litfx.components.Arc;
import lit.litfx.components.Bolt;
import lit.litfx.components.NodeLink;

/**
 *
 * @author phillsm1
 * Manages Lit components & tracks any associated Nodes
 */
public class LitView extends Region {
    /**
     * Provides lookup mechanism to find any Node that is currently 
     * tracked in the system.
     */    
    private HashMap<NodeLink, AnimatedEdge> nodeLinkToEdgeMap = new HashMap<>();
    private Region litRegion;
    private double animationDuration = 100;
    
    public LitView(Region parentToOverlay) {
        litRegion = parentToOverlay;
        prefWidthProperty().bind(litRegion.widthProperty());
        prefHeightProperty().bind(litRegion.heightProperty());
        setMouseTransparent(true);
    }
    
    public void arcNodes(Node node1, Node node2) {
        NodeLink nodeLink = new NodeLink(node1, node2);
        if(nodeLinkToEdgeMap.containsKey(nodeLink)) {
            getChildren().remove(nodeLinkToEdgeMap.get(nodeLink));
        }

        Arc arc = new Arc(node1, node2, 0.1, 80, 15, 0.85, 0.1); 
        setBoltEffects(arc);
        arc.setVisibleLength(0);
        getChildren().add(arc);
        arc.animate(Duration.millis(animationDuration));
        nodeLinkToEdgeMap.put(nodeLink, arc);
    }
    
    private void setBoltEffects(Bolt bolt) {
        bolt.setStroke(Color.ALICEBLUE);
        bolt.setOpacity(0.75);
        bolt.setStrokeWidth(4.0);
        SepiaTone st = new SepiaTone(0.25);
        Bloom bloom = new Bloom(0.25);
        bloom.setInput(st);
        Glow glow = new Glow(0.75);
        glow.setInput(bloom);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.CORNSILK, 10, 0.5, 0, 0);
        shadow.setInput(glow);
        shadow.setRadius(60.0);
        bolt.setEffect(shadow);
    }    
    
    /**
     * Provides lookup mechanism to find any edge that is currently 
     * managed in the view.
     * @param node1 the Node to Node connection that has an AnimatedEdge 
     * @param node2 
     * @return The AnimatedEdge. If no NodeLink exists that matches then returns null.
     */
    public AnimatedEdge lookupByNode(Node node1, Node node2) {
        return nodeLinkToEdgeMap.get(new NodeLink(node1, node2));
    }    

    /**
     * Remove a Node to Node AnimatedEdge from the view. 
     * 
     * @param node1
     * @param node2
     */     
    public void removeArc(Node node1, Node node2) {
        NodeLink nodeLink = new NodeLink(node1, node2);
        if(nodeLinkToEdgeMap.containsKey(nodeLink)) {
            getChildren().remove(nodeLinkToEdgeMap.get(nodeLink));
            nodeLinkToEdgeMap.remove(nodeLink);
            //@TODO SMP Remove change listeners here
        }
    }     
    /**
     * Removes all model objects currently in the mapping. 
     * 
     */     
    public void clearAll() {
        //@TODO SMPRemove All listeners
        nodeLinkToEdgeMap.clear();
        getChildren().clear();
    }
    /**
     * Get a list of all the AnimatedEdge objects in the mapping
     * @return 
     */ 
    public Collection<AnimatedEdge> getArcs() {
        return nodeLinkToEdgeMap.values();
    }    
}