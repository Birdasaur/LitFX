package lit.litfx.core;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author phillsm1
 */
public class AlgorithmsTest {

    /**
     * Test of simpleBres2D method, of class Algorithms.
     */
    @Test
    public void testSimpleBres2D() {
        System.out.println("simpleBres2D");
        int x1 = 1;
        int y1 = 1;
        int x2 = 3;
        int y2 = 5;
        ArrayList<Point2D> expResult = new ArrayList<>();
        expResult.add(new Point2D(1, 1));
        expResult.add(new Point2D(1, 2));
        expResult.add(new Point2D(2, 3));
        expResult.add(new Point2D(2, 4));
        expResult.add(new Point2D(3, 5));
        
        ArrayList<Point2D> result = Algorithms.simpleBres2D(x1, y1, x2, y2);
        assertEquals(expResult, result);
    }
}
