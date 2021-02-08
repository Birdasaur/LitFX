package lit.litfx.core.components.targeting;

import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

/**
 *
 * @author Birdasaur
 */
public class SpinningReticule extends Group {
    public Circle c1;
    public Circle c2;
    public Circle c3;
    public Circle center;    
    public double c1RadiusModifier = 0.75;
    public double c2RadiusModifier = 0.85;
    public double c1StrokeModifier = 0.50; 
    public double c2StrokeModifier = 0.75;
    public double c1DashSpacing = 50;
    public double c2DashSpacing = 75;
    public double c3DashSpacing = 100;
    Line horizontalLeftLine = new Line(0, 0, 0, 0);
    Line horizontalRightLine = new Line(0, 0, 0, 0);   
    Line verticalTopLine = new Line(0, 0, 0, 0);
    Line verticalBottomLine = new Line(0, 0, 0, 0);    
    Group reticuleLines = new Group(
        horizontalLeftLine, horizontalRightLine, verticalTopLine, verticalBottomLine);
    
    public SpinningReticule(double outerRadius, double outerStrokeWidth, 
        double c1RadiusModifier, double c2RadiusModifier, 
        double c1StrokeModifier, double c2StrokeModifier,
        double c1DashSpacing, double c2DashSpacing, double c3DashSpacing)  {
        c1 = dashedCircle(outerRadius * c1RadiusModifier, outerStrokeWidth * c1StrokeModifier, c1DashSpacing, Color.LIGHTSTEELBLUE);
        c2 = dashedCircle(outerRadius * c2RadiusModifier, outerStrokeWidth * c2StrokeModifier, c2DashSpacing, Color.STEELBLUE);
        c3 = dashedCircle(outerRadius, outerStrokeWidth, c3DashSpacing, Color.DARKBLUE);
        center = new Circle(8, Color.CRIMSON);
        horizontalLeftLine.setFill(Color.LIGHTSKYBLUE);
        horizontalRightLine.setFill(Color.LIGHTSKYBLUE);
        verticalTopLine.setFill(Color.LIGHTSKYBLUE);
        verticalBottomLine.setFill(Color.LIGHTSKYBLUE);
        horizontalLeftLine.setStroke(Color.LIGHTSKYBLUE);
        horizontalRightLine.setStroke(Color.LIGHTSKYBLUE);
        verticalTopLine.setStroke(Color.LIGHTSKYBLUE);
        verticalBottomLine.setStroke(Color.LIGHTSKYBLUE);
        getChildren().addAll(c1, c2, c3, center, reticuleLines);        
    }    
    public SpinningReticule(double outerRadius, double outerStrokeWidth)  {
        this(outerRadius, outerStrokeWidth, 0.75, 0.85, 0.50, 0.75, 25, 50, 75);
    }
    public void setCenterRadius(double radius) {
        center.setRadius(radius);
    }
    public void showCenterPoint(boolean show) {
        center.setVisible(show);
    }
    public void setReticuleEndPoints(
        double leftStartX, double leftStartY, double leftEndX, double leftEndY,
        double rightStartX, double rightStartY, double rightEndX, double rightEndY,
        double topStartX, double topStartY, double topEndX, double topEndY,
        double bottomStartX, double bottomStartY, double bottomEndX, double bottomEndY) {
        horizontalLeftLine.setStartX(leftStartX);
        horizontalLeftLine.setStartY(leftStartY);
        horizontalLeftLine.setEndX(leftEndX);
        horizontalLeftLine.setEndY(leftEndY);
        
        horizontalRightLine.setStartX(rightStartX);
        horizontalRightLine.setStartY(rightStartY);
        horizontalRightLine.setEndX(rightEndX);
        horizontalRightLine.setEndY(rightEndY);
        
        verticalTopLine.setStartX(topStartX);
        verticalTopLine.setStartY(topStartY);
        verticalTopLine.setEndX(topEndX);
        verticalTopLine.setEndY(topEndY);

        verticalBottomLine.setStartX(bottomStartX);
        verticalBottomLine.setStartY(bottomStartY);
        verticalBottomLine.setEndX(bottomEndX);
        verticalBottomLine.setEndY(bottomEndY);
    }
            
    public void showReticuleLines(boolean show) {
        center.setVisible(show);
    }
    
    public void rotateCircle(Circle c, int angle, int duration) {
        RotateTransition rotate = getCircleRotate(c, angle, duration);
        rotate.playFromStart();
    }
    public RotateTransition getCircleRotate(Circle c, int angle, double duration) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(duration), c);
        rotate.setByAngle(angle);
        rotate.setRate(3);
        rotate.setCycleCount(1);
        return rotate;
    }
    
    private Circle dashedCircle(double radius, double strokeWidth, 
            double dashSpacing,  Color strokeColor) {
        Glow glow = new Glow(10);
        
        Circle c = new Circle(radius, strokeColor.deriveColor(1, 1, 1, 0.05));
        c.setEffect(glow);
        c.setStroke(strokeColor);
        c.setStrokeWidth(strokeWidth);
        c.setStrokeLineJoin(StrokeLineJoin.MITER);
        c.setStrokeMiterLimit(50);
        c.getStrokeDashArray().addAll(dashSpacing);
        c.setMouseTransparent(true);
        return c;
    } 
}