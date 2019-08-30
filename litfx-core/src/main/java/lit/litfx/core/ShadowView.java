package lit.litfx.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Callback;
import lit.litfx.core.components.EdgePoint;
import lit.litfx.core.components.LineOfSight;
import lit.litfx.core.utils.Utils;

/**
 *
 * @author Birdasaur
 * Provides a layer that simulates shadow effects and light sources.
 */
public class ShadowView extends Region {
    public static final double DEFAULT_AMBIENT_OPACITY = 0.2;
    public static final Color DEFAULT_SHADOW_COLOR = Color.BLACK;
    private Region shadowedRegion;
    public SimpleDoubleProperty ambientLightIntensity;
    public SimpleObjectProperty<Color> shadowColor;
    public ObservableList<LineOfSight> losList;

    private List<Line> nodeLines;    
    private Canvas shadowCanvas;
    private GraphicsContext gc;
    ArrayList<Node> shadowedNodes;  
    
    public SimpleBooleanProperty animating = new SimpleBooleanProperty(false);
    public SimpleLongProperty animationSleepMilli = new SimpleLongProperty(33);
    WritableImage srcImage;
    WritableImage srcMask;
    WritableImage dest; 
    
    public ShadowView(Region parentToOverlay) {
        shadowedRegion = parentToOverlay;
        prefWidthProperty().bind(shadowedRegion.widthProperty());
        prefHeightProperty().bind(shadowedRegion.heightProperty());
        setMouseTransparent(true);

        losList = FXCollections.observableArrayList();

        initLosTask();
        
//        losList = FXCollections.observableArrayList((LineOfSight param) -> new Observable[]{
//            param.centerPoint,
//            param.scanLength
//        });
//        losList.addListener((ListChangeListener.Change<? extends LineOfSight> c) -> {
//            while (c.next()) {
//                if (c.wasPermutated()) {
//                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
//                        System.out.println("Permuted: " + i + " " + losList.get(i));
//                    }
//                } else if (c.wasUpdated()) {
//                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
//                        System.out.println("Updated: " + i + " " + losList.get(i));
//                    }
//                } else {
//                    for (LineOfSight removedItem : c.getRemoved()) {
//                        System.out.println("Removed: " + removedItem);
//                    }
//                    for (LineOfSight addedItem : c.getAddedSubList()) {
//                        System.out.println("Added: " + addedItem);
//                    }
//                }
//            }
//        });
        scanShadowRegion();
        initCanvas();
        initColorProperties();
    }
    
    private void initLosTask() {
        animating.set(true);
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                long time = System.nanoTime();
                while(animating.get()) {
                    System.out.print("animating...");
                    time = System.nanoTime();
                    
                    //Thread.onSpinWait();
                    losList.stream().forEach(los -> los.updateScan(nodeLines));

                    // create canvas engulfed in shadows
                    srcImage = createBaseLayer();
                    //Create a mask 
                    srcMask = createLightMask();   
                    //Blend the images of the base canvas with the mask 
                    dest = blendImages(srcImage, srcMask);
                    
                    //How fast could we do this?
                    Utils.printTotalTime(time);
                    
                    Thread.sleep(animationSleepMilli.get());
                    if(this.isCancelled() || this.isDone())
                        break;
                }
                return null;
            }
        };
        Thread animationThread = new Thread(animationTask);
        animationThread.setDaemon(true);
        animationThread.start();  
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        scanShadowRegion();        
        refreshView();
    }
    
    public void refreshView() {
        //erase what was before
        gc.clearRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
        // fill it with the latest blended image
        gc.drawImage(dest, 0, 0);
    }
    
    private WritableImage blendImages(WritableImage srcImage, WritableImage maskImage) {
        PixelReader maskReader = maskImage.getPixelReader();
        PixelReader imageReader = srcImage.getPixelReader();

        int width = (int) maskImage.getWidth();
        int height = (int) maskImage.getHeight();           
        
        // create dest image
        WritableImage dest = new WritableImage(width, height);
        PixelWriter writer = dest.getPixelWriter();

        // blend image and mask
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color maskColor = maskReader.getColor(x, y);
                Color imageColor = imageReader.getColor(x, y);
                if (maskColor.equals(Color.WHITE)) {
                    writer.setColor(x, y, imageColor);
                } 
//                else {
//                    System.out.println("Not white color!");
//                }
            }
        }
        return dest;
    }
    private WritableImage createBaseLayer() {
        // create canvas engulfed in shadows
        Canvas baseCanvas = new Canvas( shadowCanvas.getWidth(), shadowCanvas.getHeight());
        GraphicsContext canvasGC = baseCanvas.getGraphicsContext2D();
        Color shadow = shadowColor.get();
        canvasGC.setFill(shadow);
        canvasGC.fillRect(0, 0, baseCanvas.getWidth(), baseCanvas.getHeight());
        // get image from canvas
        WritableImage srcImage = new WritableImage((int) baseCanvas.getWidth(), (int) baseCanvas.getHeight());
//        return baseCanvas.snapshot(null, srcImage);
        return baseCanvas.snapshot(null, srcImage);
    }
    private WritableImage createLightMask() {
        // create mask
        Canvas mask = new Canvas( shadowCanvas.getWidth(), shadowCanvas.getHeight());
        GraphicsContext maskGC = mask.getGraphicsContext2D();
        maskGC.setFill(Color.WHITE);
        maskGC.fillRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());

        // draw path
        maskGC.setFill(Color.BLACK);
        maskGC.setStroke(Color.BLACK);
        maskGC.beginPath();
        for(LineOfSight los : losList) {
            drawLoS(los, maskGC);
        }
        maskGC.stroke();
        maskGC.fill();

        // get image from mask
        WritableImage srcMask = new WritableImage((int) shadowCanvas.getWidth(), (int) shadowCanvas.getHeight());
        return mask.snapshot(null, srcMask);        
    }    
    private void drawLoS(LineOfSight los, GraphicsContext gc) {
//        gc.setStroke(Color.YELLOW);
//        gc.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.25));
//        
//        //draw intersection points
//        double w = 2;
//        double h = w;
//        for( EdgePoint point: intersections) {
//            gc.strokeOval(point.getX() - w / 2, point.getY() - h / 2, w, h);
//            gc.fillOval(point.getX() - w / 2, point.getY() - h / 2, w, h);
//        }
        int count = 0;
        for(EdgePoint point: los.intersections) {
            if( count == 0) {
                gc.moveTo(point.getX(), point.getY());
            } else {
                gc.lineTo(point.getX(), point.getY());
            }
            count++;
        }
    }
    
    public void refreshView_OLD() {
        //erase what was before
        gc.clearRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
        //engulf the gui in shadows
        Color shadow = shadowColor.get();
//        gc.setFill(shadow);
//        gc.setStroke(shadow);
//        gc.fillRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());

        //illuminate using the line of sight objects
        Color losColor = new Color(1, 1, 1, 0.2);
        PixelWriter pw = gc.getPixelWriter();

        //make one big path for each LOS object
        gc.beginPath();
//        drawLoSList();
        gc.closePath();
        
        //INSTEAD need to write all los objects to an image buffer first,
        //then raster scan the image buffer 
        //at each pixel, if underlying pixel is not LOS color, set as shadow color 
        
//        for (int y = 0; y <  shadowCanvas.heightProperty().intValue(); y++) {
//            for (int x = 0; x <  shadowCanvas.widthProperty().intValue(); x++) {
//                pw.setColor(x, y, Math.random() < 0.00005 ? Color.YELLOW : Color.BLACK);
////                if(gc.isPointInPath(x,y))
////                    pw.setColor(x, y, losColor);
////                else
////                    pw.setColor(x, y, shadow);
//            }
//        }
        
//        gc.setStroke(losColor);
//        gc.setFill(losColor);
//        drawLoSList();

//        gc.fillRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());

    }


    private void initCanvas() {
        shadowCanvas = new Canvas(getWidth(), getHeight());
        shadowCanvas.widthProperty().bind(widthProperty());
        shadowCanvas.heightProperty().bind(heightProperty());
        getChildren().add(shadowCanvas);
        gc = shadowCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
    }
    private void initColorProperties() {
        ambientLightIntensity = new SimpleDoubleProperty(DEFAULT_AMBIENT_OPACITY);        
        ambientLightIntensity.addListener(event -> shadowChange());
        shadowColor = new SimpleObjectProperty<>(deriveShadow(DEFAULT_SHADOW_COLOR));        
    }
    private void shadowChange() {
        Color color = deriveShadow(shadowColor.get());
        shadowColor.set(color);
        refreshView();
    }
    
    private Color deriveShadow(Color color) {
        Color newColor = new Color(
            color.getRed(), color.getGreen(), 
            color.getBlue(), 1.0 - ambientLightIntensity.get());        
        return newColor;
    }
    public void scanShadowRegion() {
        //get the Node children in traversal order
        shadowedNodes = new ArrayList<>();
        //This way is ... pretty close... but JavaFX currently does not publically
        //expose a method or means to find the true focus traversable order. :-(
        shadowedNodes.addAll(
            NodeTools.getAllChildren(shadowedRegion).stream()
                .filter(node -> node.isFocusTraversable())
                .collect(toList()));
        
        nodeLines = shadowedNodes.stream()
            .map(node -> NodeTools.boundsToLines(node))
            .flatMap(Collection::stream)
            .collect(toList());
        //        System.out.println("Node Lines count: " + nodeLines.size()); 

    }
    
    public void addLoS(LineOfSight los) {
        losList.add(los);
    }
    public void removeLoS(LineOfSight los) {
        losList.remove(los);
    }
    
}