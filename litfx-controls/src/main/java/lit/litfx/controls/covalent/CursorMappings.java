/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lit.litfx.controls.covalent;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;

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
    public static RESIZE_DIRECTION[] cursorSegmentArray = new RESIZE_DIRECTION[14];
    static {
        cursorSegmentArray[0] = RESIZE_DIRECTION.N;
        cursorSegmentArray[1] = RESIZE_DIRECTION.NE;
        cursorSegmentArray[2] = RESIZE_DIRECTION.E;
        cursorSegmentArray[3] = RESIZE_DIRECTION.SE;
        cursorSegmentArray[4] = RESIZE_DIRECTION.S;
        cursorSegmentArray[5] = RESIZE_DIRECTION.S;
        cursorSegmentArray[6] = RESIZE_DIRECTION.S;
        cursorSegmentArray[7] = RESIZE_DIRECTION.SW;
        cursorSegmentArray[8] = RESIZE_DIRECTION.W;
        cursorSegmentArray[9] = RESIZE_DIRECTION.W;
        cursorSegmentArray[10] = RESIZE_DIRECTION.W;
        cursorSegmentArray[11] = RESIZE_DIRECTION.NW;
        cursorSegmentArray[12] = RESIZE_DIRECTION.NW;
        cursorSegmentArray[13] = RESIZE_DIRECTION.NW;
    }    
}
