package lit.litfx.core;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Birdasaur
 * Provides a layer that simulates and manages flame effects.
 */
public class FireView extends Region {
    private Region engulfedRegion;
//    private List<Line> nodeLines;
    public SimpleBooleanProperty classic = new SimpleBooleanProperty(true);
    private int shift1 = 16;
    private int shift2 = 8;
    private int shift3 = 0;
    int[] paletteAsInts; //this will contain the color palette
    // Y-coordinate first because we use horizontal scanlines
    int[] fire;  //this buffer will contain the fire
    int[] fireBuf; //TEMP
    IntBuffer intBuffer;
    PixelFormat<IntBuffer> pixelFormat;
    PixelBuffer<IntBuffer> pixelBuffer;
    WritableImage writableImage;
    GraphicsContext gc;
    Canvas canvas;
    AnimationTimer at;
    Task fireTask;
    public BooleanProperty animating = new SimpleBooleanProperty(false);
    public LongProperty animationSleepMilli = new SimpleLongProperty(33);
    long workTimesMillis = 0;

    PixelReader prBuffer;
    PixelWriter pwBuffer;
    
    public FireView(Region parentToOverlay) {
        engulfedRegion = parentToOverlay;
        prefWidthProperty().bind(engulfedRegion.widthProperty());
        prefHeightProperty().bind(engulfedRegion.heightProperty());
        setMouseTransparent(true);
        canvas = new Canvas(getWidth(), getHeight());
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();
//        losList = FXCollections.observableArrayList();
//        scanFlameRegion();
    }
    public void stop() {
        animating.set(false);
        fireTask.cancel(true);
        at.stop();
    }
    public void start() {
        initBuffers();
        initFireTask();
        initAnimation();
    }
    private void initBuffers() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Y-coordinate first because we use horizontal scanlines
        fire = new int[getCanvasHeight() * getCanvasWidth()];  //this buffer will contain the fire

        //TEMP for testing against pixel buffer method
        fireBuf = new int[getCanvasHeight() * getCanvasWidth()];  //this buffer will contain the fire

        // new way to store image Shared pixel buffer.
        intBuffer = ByteBuffer.allocateDirect(4 * getCanvasWidth() * getCanvasHeight()).asIntBuffer();
        pixelFormat = PixelFormat.getIntArgbPreInstance();
        pixelBuffer = new PixelBuffer<>(getCanvasWidth(), getCanvasHeight(), intBuffer, pixelFormat);
        writableImage = new WritableImage(pixelBuffer);
        
        //TEMP for testing against pixel buffer method
        prBuffer = writableImage.getPixelReader();
        pwBuffer = writableImage.getPixelWriter();
        
        gc = canvas.getGraphicsContext2D();
        gc.drawImage(writableImage, 0, 0);
        paletteAsInts = generateArgbPalette(256);
    }
    
    private void initFireTask() {
        animating.set(true);
        fireTask = new Task() {
            @Override
            protected Void call() throws Exception {
                Random rand = new Random();
                long startTime = 0;
                long elapseTime = 0;
                Double startHeight = (canvas.getHeight() - 1) * canvas.getWidth();
                int fireStartHeight = startHeight.intValue();
                int canvasWidth = getCanvasWidth();
                int canvasHeight = getCanvasHeight();
                //start the loop (one frame per loop)
                while(animating.get() && !this.isCancelled() && !this.isDone()) {
                    //System.out.print("animating...");
                    // Start stop watch
                    startTime = System.currentTimeMillis();
                    canvasWidth = getCanvasWidth();
                    canvasHeight = getCanvasHeight();
                    //randomize the bottom row of the fire array.
                    for(int i=fireStartHeight; i<fireStartHeight+canvasWidth; i++) {
                        fire[i] = Math.abs(32768 + rand.nextInt(65536)) % 256;
                    }
                    // each convolution matrix. X is the cell to update. Each 1 is the field to calculate.
                    // If a 1 cell is outside the boundaries use the the x or y's wrapped cell.
                    // (y, x)
                    //     0 1 2   <- x
                    // y +------
                    // 0 | 0 1 0
                    // 1 | 0 X 0
                    // 2 | 1 0 1
                    // 3 | 0 1 0
                    // 4 | 0 1 0
                    //
                    int a, b, row, index, pixel;
                    for (int y = 0; y < canvasHeight - 1; y++) {
                        for (int x = 0; x < canvasWidth; x++) {
                            a = (y + 1) % canvasHeight * canvasWidth;
                            b = x % canvasWidth;
                            row = y * canvasWidth;
                            index = row + x;
                            pixel = fire[index]
                                    = ((fire[a + ((x - 1 + canvasWidth) % canvasWidth)]
                                    + fire[((y + 2) % canvasHeight) * canvasWidth + b]
                                    + fire[a + ((x + 1) % canvasWidth)]
                                    + fire[((y + 3) % canvasHeight * canvasWidth) + b])
                                    * 128) / 513;
                            //TEMP for testing against pixel buffer method
                            fireBuf[index] = getPaletteValue(pixel);
                            intBuffer.put(index, getPaletteValue(pixel));
                        }
                    }
                    //TEMP for testing against pixel buffer method
                    pwBuffer.setPixels(0, 0, canvasWidth, canvasHeight, pixelFormat, fireBuf, 0, canvasWidth);

                    elapseTime = System.currentTimeMillis() - startTime;
                    workTimesMillis = elapseTime;
                    Thread.sleep(17);

                    //How fast could we do this?
                    //Utils.printTotalTime(time);
//                    Thread.sleep(animationSleepMilli.get());
                    if(this.isCancelled() || this.isDone())
                        break;
                }
                return null;
            }
        };
        Thread thread = new Thread(fireTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void initAnimation() {
        at = new AnimationTimer() {
            public long lastTimerCall = 0;
            private final long NANOS_PER_MILLI = 1000000; //nanoseconds in a millisecond
            private final long ANIMATION_DELAY = 16 * NANOS_PER_MILLI;
            private double startTime;
            private double elapseTime;

            //TEMP for testing against pixel buffer method
            PixelWriter pw = gc.getPixelWriter(); //TEMP

            @Override
            public void handle(long now) {
                if(now > lastTimerCall + ANIMATION_DELAY) {
                    startTime = System.nanoTime();

//                    // A rect region to be updated in the writable image
//                    pixelBuffer.updateBuffer((b) -> new Rectangle2D(0, 0, getCanvasWidth(), getCanvasHeight()));
//                    gc.drawImage(writableImage, 0, 0);
                    

                    //TEMP for testing against pixel buffer method
                    pw.setPixels(0, 0, getCanvasWidth(), getCanvasHeight(), prBuffer, 0, 0);


//                    gc.setFill(Color.WHITE);
//                    gc.fillText("Worker time spent: " + workTimesMillis + "ms", 10, 15);

                    lastTimerCall = now;    //update for the next animation
                    elapseTime = (System.nanoTime() - startTime)/1e6;
//                    gc.fillText("UI Render thread takes : " + elapseTime + "ms", 10, 30);
                }
            }
        };
        at.start();        
    }    
    
    private int getCanvasWidth() {
        Double d = canvas.getWidth();
        return d.intValue();
    }
    private int getCanvasHeight() {
        Double d = canvas.getHeight();
        return d.intValue();
    }

    
//    @Override
//    protected void layoutChildren() {
//        super.layoutChildren();
//        scanShadowRegion();        
//    }
    
//    public void scanFlameRegion() {
//        //get the Node children in traversal order
//        //This way is ... pretty close... but JavaFX currently does not publically
//        //expose a method or means to find the true focus traversable order. :-(
//        nodeLines = NodeTools.getAllChildren(engulfedRegion).stream()
//                .filter(node -> node.isFocusTraversable())
//                .map(node -> NodeTools.boundsToLines(node))
//                .flatMap(Collection::stream)
//                .collect(toList());
//        //        System.out.println("Node Lines count: " + nodeLines.size());
//    }
    private int getPaletteValue(int pixelIndex) {
        int value = 0;
        try {
            if (classic.get())
                return paletteAsInts[pixelIndex];
            if (getShift1() > -1)
                value |= paletteAsInts[pixelIndex] << getShift1();
            if (getShift2() > -1)
                value |= paletteAsInts[pixelIndex] << getShift2();
            if (getShift3() > -1)
                value |= paletteAsInts[pixelIndex] << getShift3();
        } catch (Exception e) {
            System.out.println("pixelIndex: " + pixelIndex + "  value " + value + " " + e );
        }
        return value;
    }    
    private int[] generateArgbPalette(int max ) {
        int [] pal = new int[max];
        //generate the palette
        for (int x = 0; x < max; x++) {
            //HSLtoRGB is used to generate colors:
            //Hue goes from 0 to 85: red to yellow
            //Saturation is always the maximum: 255
            //Lightness is 0..255 for x=0..128, and 255 for x=128..255
            //color = HSLtoRGB(ColorHSL(x / 3, 255, std::min(255, x * 2)));
            //set the palette to the calculated RGB value
            //palette[x] = RGBtoINT();
            double brightness = Math.min(255, x*2) / 255.0;
            Color color = Color.hsb(x / 3.0, 1.0, brightness , 1);
            pal[x] = rgbToIntArgb(color);            
        }
        return pal;
    }

    private static int rgbToIntArgb(Color colorRGB) {
      return (int)(colorRGB.getOpacity()*255) << 24 |
             (int)(colorRGB.getRed()    *255) << 16 | 
             (int)(colorRGB.getGreen()  *255) <<  8 | 
             (int)(colorRGB.getBlue()   *255);
    }     

    /**
     * @return the shift1
     */
    public int getShift1() {
        return shift1;
    }

    /**
     * @param shift1 the shift1 to set
     */
    public void setShift1(int shift1) {
        this.shift1 = shift1;
    }

    /**
     * @return the shift2
     */
    public int getShift2() {
        return shift2;
    }

    /**
     * @param shift2 the shift2 to set
     */
    public void setShift2(int shift2) {
        this.shift2 = shift2;
    }

    /**
     * @return the shift3
     */
    public int getShift3() {
        return shift3;
    }

    /**
     * @param shift3 the shift3 to set
     */
    public void setShift3(int shift3) {
        this.shift3 = shift3;
    }

}