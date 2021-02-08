package lit.litfx.core.components.fire;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 *
 * @author Birdasaur
 */
public class Ember {
//    private ArrayList<Point2D> pixels;
//    private ArrayList<Color> colors;

    public Point2D [] pixels;
    public Color [] colors;
        
    public Ember(ArrayList<Point2D> pixels, ArrayList<Color> colors) {
        this.pixels = pixels.toArray(new Point2D[0]);
        this.colors = colors.toArray(new Color[0]);
    }

//    /**
//     * @return the pixels
//     */
//    public ArrayList<Point2D> getPixels() {
//        return pixels;
//    }
//
//    /**
//     * @param pixels the pixels to set
//     */
//    public void setPixels(ArrayList<Point2D> pixels) {
//        this.pixels = pixels;
//    }
//
//    /**
//     * @return the colors
//     */
//    public ArrayList<Color> getColors() {
//        return colors;
//    }
//
//    /**
//     * @param colors the colors to set
//     */
//    public void setColors(ArrayList<Color> colors) {
//        this.colors = colors;
//    }
}
