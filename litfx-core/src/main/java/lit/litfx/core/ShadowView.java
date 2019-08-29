package lit.litfx.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Callback;
import lit.litfx.core.components.EdgePoint;
import lit.litfx.core.components.LineOfSight;

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
    
    public ShadowView(Region parentToOverlay) {
        shadowedRegion = parentToOverlay;
        prefWidthProperty().bind(shadowedRegion.widthProperty());
        prefHeightProperty().bind(shadowedRegion.heightProperty());
        setMouseTransparent(true);

        losList = FXCollections.observableArrayList((LineOfSight param) -> new Observable[]{
            param.centerPoint,
            param.scanLength
        });
        losList.addListener((ListChangeListener.Change<? extends LineOfSight> c) -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        System.out.println("Permuted: " + i + " " + losList.get(i));
                    }
                } else if (c.wasUpdated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        System.out.println("Updated: " + i + " " + losList.get(i));
                    }
                } else {
                    for (LineOfSight removedItem : c.getRemoved()) {
                        System.out.println("Removed: " + removedItem);
                    }
                    for (LineOfSight addedItem : c.getAddedSubList()) {
                        System.out.println("Added: " + addedItem);
                    }
                }
            }
        });
        scanShadowRegion();
        initCanvas();
        initColorProperties();
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        scanShadowRegion();        
        refreshView();
    }
    
    public void refreshView() {
        Color shadow = shadowColor.get();
        gc.clearRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
        gc.setFill(shadow);
        gc.setStroke(shadow);
        gc.fillRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
        drawLoSList();
    }
    
    private void drawLoSList() {
        for(LineOfSight los : losList) {
            drawLoS(los);
        }
    }
    private void drawLoS(LineOfSight los) {
        List<Line> scanLines = los.createScanLines(0, 360, 1);
        System.out.println("Scan Lines count: " + scanLines.size());
        List<EdgePoint> intersections = los.getIntersectionPoints(scanLines, nodeLines);
        System.out.println("Intersections In Range: " + intersections.size());
        gc.setStroke(Color.RED);
        gc.setFill(Color.RED.deriveColor(1, 1, 1, 0.5));

        double w = 2;
        double h = w;
        for( EdgePoint point: intersections) {
            gc.strokeOval(point.getX() - w / 2, point.getY() - h / 2, w, h);
            gc.fillOval(point.getX() - w / 2, point.getY() - h / 2, w, h);
        }
        
        
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