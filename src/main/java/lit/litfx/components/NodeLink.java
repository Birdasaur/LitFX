package lit.litfx.components;

import java.util.Objects;
import javafx.scene.Node;

/**
 *
 * @author phillsm1
 */
public class NodeLink {
    Node startNode;
    Node endNode;
    
    public NodeLink(Node startNode, Node endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.startNode);
        hash = 73 * hash + Objects.hashCode(this.endNode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeLink other = (NodeLink) obj;
        if (!Objects.equals(this.startNode, other.startNode)) {
            return false;
        }
        if (!Objects.equals(this.endNode, other.endNode)) {
            return false;
        }
        return true;
    }
}
