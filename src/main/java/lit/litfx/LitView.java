package lit.litfx;

import java.util.Collection;
import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import lit.litfx.components.AnimatedEdge;

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
    private static HashMap<Node, AnimatedEdge> nodeToEdgeMap = new HashMap<>();

    Region litRegion;
    
    public LitView(Region parentToOverlay) {
        litRegion = parentToOverlay;
        prefWidthProperty().bind(litRegion.widthProperty());
        prefHeightProperty().bind(litRegion.heightProperty());
        setMouseTransparent(true);
    }
    
    /**
     * Provides lookup mechanism to find any edge that is currently 
     * managed in the view.
     * @param node the Node that has an AnimatedEdge starting from it.
     * @return The AnimatedEdge that has the Node as a starting point. If no Node
     * exists that is currently being tracked then returns null.
     */
    public static AnimatedEdge lookupByNode(Node node) {
        return nodeToEdgeMap.get(node);
    }    
    /**
     * Add a Node object to be tracked which has an edge anchored to it. 
     * 
     * @param node
     * @param edge
     */     
    public static void addNode(Node node, AnimatedEdge edge) {
        nodeToEdgeMap.put(node, edge);
        //@TODO SMP Add change listeners here
    }    

    /**
     * Remove a Node object with an anchored AnimatedEdge from the view. 
     * 
     * @param node
     */     
    public static void removeNode(Node node) {
        nodeToEdgeMap.remove(node);
        //@TODO SMP Remove change listeners here
    }     
    /**
     * Removes all model objects currently in the mapping. 
     * 
     */     
    public static void clearAll() {
        //Remove All listeners
        nodeToEdgeMap.clear();
    }
    /**
     * Get a list of all the AnimatedEdge objects in the mapping
     * @return 
     */ 
    public static Collection<AnimatedEdge> getAll() {
        return nodeToEdgeMap.values();
    }    
    
}
