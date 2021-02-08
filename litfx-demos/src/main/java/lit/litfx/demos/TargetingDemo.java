package lit.litfx.demos;

import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lit.litfx.core.components.Bolt;
import lit.litfx.core.components.BoltDynamics;
import lit.litfx.core.components.EdgePoint;
import lit.litfx.core.components.targeting.SpinningReticule;

/**
 *
 * @author Birdasaur
 */
public class TargetingDemo extends Application {

    BoltDynamics boltDynamics;
    Bolt bolt;
    Pane centerPane;
    double animationDuration = 100;
    
    @Override
    public void start(Stage primaryStage) {
        SpinningReticule reticule = new SpinningReticule(75, 4, 
            0.75, 0.85, 0.333, 0.666, 15, 20, 25);
        reticule.setCenterRadius(5);
        centerPane = new Pane(reticule);
        BorderPane root = new BorderPane(centerPane);
        root.setBackground(Background.EMPTY);
        Scene scene = new Scene(root, 800, 800, Color.BLACK);
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, e-> {
            reticule.setTranslateX(e.getX());
            reticule.setTranslateY(e.getY());
            reticule.setReticuleEndPoints(-e.getX(), 0, 0, 0, 
                0, 0, centerPane.getWidth(), 0,
                0, -e.getY(),0,0, 
                0, 0, 0, centerPane.getHeight());
        });
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> {
            ParallelTransition pt = new ParallelTransition(
                reticule.getCircleRotate(reticule.c1, 360, 0.5),
                reticule.getCircleRotate(reticule.c2, 180, 1.0),
                reticule.getCircleRotate(reticule.c3, 90, 1.5));
            pt.setOnFinished(finished -> fireBolt(e.getX(), e.getY()));
            pt.play();
        });
        
        boltDynamics = new BoltDynamics.Builder().with(dynamics -> {
            dynamics.density = 0.1; dynamics.sway = 80; dynamics.jitter = 15;
            dynamics.envelopeSize = 0.85; dynamics.envelopeScalar = 0.1;
        }).build();
        
        primaryStage.setTitle("Target with mouse.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void fireBolt(double x, double y) {
        centerPane.getChildren().removeIf(node -> node instanceof Bolt);
        EdgePoint ep0 = new EdgePoint(0, centerPane.getWidth()/2.0, 
            centerPane.getHeight(), 0);
        EdgePoint ep1 = new EdgePoint(1, x, y, 0);
        bolt = new Bolt(ep0, ep1, boltDynamics);
        setBoltEffects(bolt);
        centerPane.getChildren().add(bolt);
        bolt.setVisibleLength(10);
        bolt.animate(Duration.millis(animationDuration));
        
    }
    private void setBoltEffects(Bolt bolt) {
        bolt.setStroke(Color.LIGHTSKYBLUE);
        bolt.setOpacity(0.75);
        bolt.setStrokeWidth(4.0);
        SepiaTone st = new SepiaTone(0.25);
        Bloom bloom = new Bloom(0.25);
        bloom.setInput(st);
        Glow glow = new Glow(0.75);
        glow.setInput(bloom);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.CORNSILK, 10, 0.5, 0, 0);
        shadow.setInput(glow);
        shadow.setRadius(60.0);
        bolt.setEffect(shadow);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}