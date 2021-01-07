package lit.litfx.core.components.fire;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Debouncing upon change on a property. A common use case is
 * a search text field as the user is typing characters to begin
 * query when the user pauses typing a specified time.
 * When a JavaFX property is invalidated or changing value
 * kill other pending delayedActions and spawn a new timer task.
 * @author cpdea
 */
public class DebounceDispatcher<T> implements ChangeListener<T> {
    static long counter;
    private Queue<Timeline> delayedActions = new ConcurrentLinkedQueue<>();
    private Runnable action;
    private long delay = 500l; // half a second

    /**
     * Constructor to debounce action to be performed.
     * This reads as 'Perform action only if 100 milliseconds
     * have passed without it being called'.
     * @param action action to perform when within a display.
     * @param delay delay to perform action.
     */
    public DebounceDispatcher(Runnable action, long delay){
        this.action = action;
        this.delay = delay;
    }
    public DebounceDispatcher(){
    }
    public DebounceDispatcher(long delay){
        this.delay = delay;
    }

    public DebounceDispatcher onAction(Runnable action) {
        this.action = action;
        return this;
    }
    public DebounceDispatcher delayMillis(long delay) {
        this.delay = delay;
        return this;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        // Kill all delayed actions
        delayedActions.forEach(delayedAction -> {
            delayedAction.pause();
            delayedAction.stop();
        });
        delayedActions.clear();

        // Create a delayed action
        KeyFrame delayedAction = new KeyFrame(Duration.millis(getDelay()), "workTask #" + counter++, event -> {
            if (getAction() != null) {
                getAction().run();
            }
        });
        Timeline timeline = new Timeline(delayedAction);

        // spawn latest task.
        delayedActions.add(timeline);
        timeline.playFromStart();
    }
}
