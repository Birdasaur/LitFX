package lit.litfx.core;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import jfxtras.labs.util.ShapeConverter;
import lit.litfx.core.components.EdgePoint;
import lit.litfx.core.components.LineOfSight;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author Birdasaur
 * Provides a layer that simulates shadow effects and light sources.
 */
public class ShadowView extends Region {
    public static final double DEFAULT_AMBIENT_OPACITY = 0.2;
    public static final Color DEFAULT_SHADOW_COLOR = Color.BLACK;
    public static final Color DEFAULT_LIGHT_COLOR = Color.TRANSPARENT;
    public static final Color DEFAULT_INTERSECTION_COLOR = Color.CYAN;
    public static final Color DEFAULT_WIREFRAME_COLOR = Color.BLUE;
    private Region shadowedRegion;

    public DoubleProperty ambientLightIntensity;
    public ObjectProperty<Color> shadowColor;
    public ObjectProperty<Color> lightColor;
    public ObjectProperty<Color> intersectionColor;
    public ObjectProperty<Color> wireframeColor;

    public BooleanProperty shadowEnabled;
    public BooleanProperty lightEnabled;
    public BooleanProperty lightColorEnabled;
    public BooleanProperty intersectionEnabled;
    public BooleanProperty wireframeEnabled;

    public List<LineOfSight> losList;

    private List<Line> nodeLines;    
    private Canvas shadowCanvas;
    private GraphicsContext gc;

    public BooleanProperty animating = new SimpleBooleanProperty(false);
    public LongProperty animationSleepMilli = new SimpleLongProperty(33);

    public ShadowView(Region parentToOverlay) {
        shadowedRegion = parentToOverlay;
        prefWidthProperty().bind(shadowedRegion.widthProperty());
        prefHeightProperty().bind(shadowedRegion.heightProperty());
        setMouseTransparent(true);

        losList = FXCollections.observableArrayList();
        scanShadowRegion();
        initCanvas();
        initColorProperties();
        initLosTask();
    }
    private void drawShapes() {
        //create a shape that covers the canvas
        Rectangle rect = new Rectangle(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        rect.setFill(shadowColor.get());

        //create "lighting" shape
        Path losShape = drawLosShape();
        // punch hole in rectangle based on the shape of the light
        Shape newShape;
        if(lightEnabled.get())
            newShape = Shape.subtract(rect, losShape);
        else
            newShape = rect;
        String svgShape = ShapeConverter.shapeToSvgString(newShape);
        
        //clear screen
        gc.clearRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        // draw rectangle
        if(shadowEnabled.get())
            gc.setFill(shadowColor.get());
        else
            gc.setFill(Color.TRANSPARENT);
        
        // punch hole in rectangle
        gc.beginPath();
        gc.appendSVGPath(svgShape);
        gc.closePath();
        gc.fill();
        
        if(intersectionEnabled.get())
            drawIntersections();
        if(wireframeEnabled.get())
            drawWireframe();
        
        //redraw the line of sight shape (in case the "light" has a color)
        if(lightColorEnabled.get()) {
            gc.setFill(lightColor.get());
            gc.beginPath();
            gc.appendSVGPath(ShapeConverter.shapeToSvgString(losShape));
            gc.closePath();
            gc.fill();
        }
    }

    private void drawWireframe() {
        gc.setStroke(wireframeColor.get().deriveColor(1, 1, 1, 0.25));
//        gc.setFill(wireframeColor.get().deriveColor(1, 1, 1, 0.25));
        gc.setLineWidth(3.0);
        for(Line line : nodeLines) {
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
        gc.setLineWidth(1.0); //default
    }
    
    private void drawIntersections() {
        gc.setStroke(intersectionColor.get());
        gc.setFill(intersectionColor.get().deriveColor(1, 1, 1, 0.25));
        
        //draw intersection points
        double w = 2;
        double h = w;
        for(LineOfSight los : losList) {
            for(EdgePoint point: los.intersections) {
                gc.strokeOval(point.getX() - w / 2, point.getY() - h / 2, w, h);
                gc.fillOval(point.getX() - w / 2, point.getY() - h / 2, w, h);
            }
        }        
    }
    
    private void initLosTask() {
        animating.set(true);
        Task animationTask = new Task() {
            @Override
            protected Void call() throws Exception {
                double scalar = 360.0 * 2.0;
                double scanlineStep = 360.0 / scalar; //degrees 
                
                //long time = System.nanoTime();
                while(animating.get()) {
                    //System.out.print("animating...");
                    //time = System.nanoTime();
                    //Thread.onSpinWait();
                    losList.forEach(los -> los.updateScan(nodeLines, scanlineStep));
                    Platform.runLater(() -> drawShapes());
                    //How fast could we do this?
                    //Utils.printTotalTime(time);
                    
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
    }
    
    private Path drawLosShape() {
        Path losShape = new Path();       
        //go through each line of sight object and draw it to a path
        for(LineOfSight los : losList) {
            int count = 0;
            for(EdgePoint point: los.intersections) {
                if( count == 0) {
                    losShape.getElements().add(new MoveTo(point.getX(), point.getY()));
                } else {
                    losShape.getElements().add(new LineTo(point.getX(), point.getY()));
                }
                count++;
            }
        }
        losShape.getElements().add( new ClosePath());
        losShape.setFill(lightColor.get());
        return losShape;
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

        lightColor  = new SimpleObjectProperty<>(deriveShadow(DEFAULT_LIGHT_COLOR));
        intersectionColor  = new SimpleObjectProperty<>(DEFAULT_INTERSECTION_COLOR);
        wireframeColor  = new SimpleObjectProperty<>(DEFAULT_WIREFRAME_COLOR);
        
        shadowEnabled = new SimpleBooleanProperty(true);
        lightEnabled = new SimpleBooleanProperty(true);
        lightColorEnabled = new SimpleBooleanProperty(false);
        intersectionEnabled = new SimpleBooleanProperty(true);
        wireframeEnabled = new SimpleBooleanProperty(false);
    }
    private void shadowChange() {
        Color color = deriveShadow(shadowColor.get());
        shadowColor.set(color);
    }
    
    private Color deriveShadow(Color color) {
        Color newColor = new Color(
            color.getRed(), color.getGreen(), 
            color.getBlue(), 1.0 - ambientLightIntensity.get());        
        return newColor;
    }
    public void setShadowColor(Color color) {
        shadowColor.set(deriveShadow(color));        
    }
    public void scanShadowRegion() {
        //get the Node children in traversal order
        //This way is ... pretty close... but JavaFX currently does not publically
        //expose a method or means to find the true focus traversable order. :-(
        nodeLines = NodeTools.getAllChildren(shadowedRegion).stream()
                .filter(node -> node.isFocusTraversable())
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