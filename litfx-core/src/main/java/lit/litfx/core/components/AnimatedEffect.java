package lit.litfx.core.components;

import javafx.util.Duration;

/**
 *
 * @author phillsm1
 */
public interface AnimatedEffect {
    public void animate(Duration milliseconds);
    public void updateLength(int length);    
}
