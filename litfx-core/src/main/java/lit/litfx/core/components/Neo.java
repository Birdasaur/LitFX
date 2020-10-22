package lit.litfx.core.components;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.BiFunction;

/**
 * Matrix effect drawn on a canvas.
 * TODO parameterize colors fonts etc.
 * Ported from https://dev.to/gnsp/making-the-matrix-effect-in-javascript-din
 * @author cpdea
 */
public class Neo {
    private int fontSize = 20; // width pixels
    private Canvas canvas;

    private AnimationTimer animationTimer;

    public Neo(Canvas canvas) {
        this.canvas = canvas;
        init();
    }
    private int[] resize(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.save();


        gc.setFill(Color.web("#000"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int cols = (int) Math.floor(canvas.getWidth() / fontSize) + 1;
        int[] ypos = new int[cols];
        return ypos;

    }
    private void init() {
        GraphicsContext gc = canvas.getGraphicsContext2D();


        int[] initYpos = resize(gc);

        // generate
        this.animationTimer = new AnimationTimer() {
            long lastTimerCall = 0;
            final long NANOS_PER_MILLI = 1000000; //nanoseconds in a millisecond
            final long ANIMATION_DELAY = 30 * NANOS_PER_MILLI; //>>>>>>parameterize
            int prevWidth = (int) canvas.getWidth();
            int prevHeight = (int) canvas.getHeight();
            int[] ypos = initYpos;
            @Override
            public void handle(long now) {
                if (now > lastTimerCall + ANIMATION_DELAY) {
                    lastTimerCall = now;    //update for the next animation

                    int w, h;
                    w = (int) canvas.getWidth();
                    h = (int) canvas.getHeight();
                    if (w != prevWidth || h != prevHeight) {
                        System.out.println("resizing " + w + " prevw " + prevWidth);
                        ypos = resize(gc);
                        prevWidth = w;
                        prevHeight = h;
                    }
                    // Draw a semitransparent black rectangle on top of previous drawing
                    gc.setFill(Color.web("#0001"));
                    gc.fillRect(0, 0, w, h);

                    // Set color to green and font to 15pt monospace in the drawing context
                    gc.setFill(Color.web("#0f0")); // >>>>>>parameterize
                    gc.setFont(new Font("monospace", 15));

                    // for each column put a random character at the end
                    for (int i = 0; i < ypos.length; i++) {
                        // generate a random character
                        String text = Character.toString((int) (Math.random() * 128));

                        // x coordinate of the column, y coordinate is already given
                        double x = i * fontSize;
                        int y = ypos[i];
                        // render the character at (x, y)
                        gc.fillText(text, x, ypos[i]);

                        // randomly reset the end of the column if it's at least 100px high
                        if (y > 100 + Math.random() * 10000) {
                            ypos[i] = 0;
                        } else {
                            // otherwise just move the y coordinate for the column 20px down,
                            ypos[i] = y + fontSize;
                        }

                    }
                }
            }
        };

    }

    public void start() {
        this.animationTimer.start();
    }
    public void stop() {
        this.animationTimer.stop();
    }

    private static BiFunction<String, Object, String> row = (label, value) ->
            String.format(" %s: %s\n", label, value);

    private static String out(String name, Object value) {
        if (value instanceof Object[]) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Object obj : (Object[]) value) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(obj);
                i++;
            }
            return row.apply(name, sb.toString());
        }
        return row.apply(name, value);
    }

    private static String sb(String... args) {
        StringBuilder sb = new StringBuilder();
        for (String pair : args) {
            sb.append(pair);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return sb(
//                out("bandColorProperty", bandColor.get())
        );
    }
}
