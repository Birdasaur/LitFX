package lit.litfx.controls.covalent.events;

import javafx.event.Event;
import javafx.event.EventType;
import lit.litfx.controls.covalent.PathPane;

/**
 *
 * @author phillsm1
 */
public class CovalentPaneEvent extends Event{
    public static final EventType<CovalentPaneEvent> ANY  = new EventType("COVALENT_PANE_ANY");
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_SHOWING  = new EventType("COVALENT_PANE_SHOWING");
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_SHOWN  = new EventType("COVALENT_PANE_SHOWN");
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_HIDING  = new EventType("COVALENT_PANE_HIDING");
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_HIDDEN  = new EventType("COVALENT_PANE_HIDDEN");
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_MINIMIZE  = new EventType("COVALENT_PANE_MINIMIZE");   
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_MAXIMIZE  = new EventType("COVALENT_PANE_MAXIMIZE");   
    public static final EventType<CovalentPaneEvent> COVALENT_PANE_CLOSE  = new EventType("COVALENT_PANE_CLOSE");   
    
    public PathPane pathPane;
    public CovalentPaneEvent(EventType<? extends Event> arg0, PathPane pathPane) {
        super(arg0);
        this.pathPane = pathPane;
    }
}
