package lit.litfx.core.components;

import javafx.util.Duration;

/**
 *
 * @author Birdasaur
 */
public interface AnimatedEffect {
    public void animate(Duration milliseconds);
    public void updateLength(int length); 
    public boolean isAnimating();
    public void stop();
}
