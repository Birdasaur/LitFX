package lit.litfx.core;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

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
    
    private Canvas shadowCanvas;
    private GraphicsContext gc;
    
    public ShadowView(Region parentToOverlay) {
        shadowedRegion = parentToOverlay;
        prefWidthProperty().bind(shadowedRegion.widthProperty());
        prefHeightProperty().bind(shadowedRegion.heightProperty());
        setMouseTransparent(true);
        
        initCanvas();
        
        initColorProperties();
        
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        updateView();
    }
    
    private void updateView() {
        Color shadow = shadowColor.get();
        gc.clearRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
        gc.setFill(shadow);
        gc.setStroke(shadow);
        gc.fillRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
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
        updateView();
    }
    
    private Color deriveShadow(Color color) {
        Color newColor = new Color(
            color.getRed(), color.getGreen(), 
            color.getBlue(), 1.0 - ambientLightIntensity.get());        
        return newColor;
    }
}