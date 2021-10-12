/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lit.litfx.controls.covalent;

import javafx.scene.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author phillsm1
 */
public enum CursorMappings {
    INSTANCE;
    /**
     * The 8 cursor directions to resize the current window.
     */
    public enum RESIZE_DIRECTION {
        NONE, NW, N, NE, E, SE, S, SW, W
    }    
    public static Map<RESIZE_DIRECTION, Cursor> cursorMap = new HashMap<>();
    static {
        cursorMap.put(RESIZE_DIRECTION.NW, Cursor.NW_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.N, Cursor.N_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.NE, Cursor.NE_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.E, Cursor.E_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.SE, Cursor.SE_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.S, Cursor.S_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.SW, Cursor.SW_RESIZE);
        cursorMap.put(RESIZE_DIRECTION.W, Cursor.W_RESIZE);
    }

    public static Cursor findCursorType(RESIZE_DIRECTION direction) {
        return cursorMap.get(direction);
    }
}
