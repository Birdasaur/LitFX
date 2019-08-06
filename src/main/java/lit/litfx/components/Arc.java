package lit.litfx.components;

import javafx.scene.Node;
import lit.litfx.NodeTools;

/**
 *
 * @author phillsm1
 * 
 */
public class Arc extends Bolt {

    Node startNode;
    Node endNode;
    
    public Arc(Node startNode, Node endNode, 
        double density, double sway, double jitter,
        double envelopeSize, double envelopeScalar) {
        super(NodeTools.shortArcPoints(startNode, endNode).get(0).toPoint2D(),
            NodeTools.shortArcPoints(startNode, endNode).get(1).toPoint2D(),
            density, sway, jitter, envelopeSize, envelopeScalar
        );

        this.startNode = startNode;
        this.endNode = endNode;

    }

    //@TODO SMP  Need to add optional listener support for when the nodes change position
}