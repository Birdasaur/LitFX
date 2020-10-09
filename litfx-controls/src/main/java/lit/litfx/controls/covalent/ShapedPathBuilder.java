package lit.litfx.controls.covalent;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static lit.litfx.controls.covalent.BindablePointBuilder.bindXToWidth;
import static lit.litfx.controls.covalent.BindablePointBuilder.bindYToHeight;
import static lit.litfx.controls.covalent.BindablePointBuilder.xTo;
import static lit.litfx.controls.covalent.BindablePointBuilder.yTo;

public class ShapedPathBuilder {
    private ShapedPath shapedPath;
    private ShapedPathBuilder(Stage stage) {
        shapedPath = new ShapedPath(stage);
    }
    private ShapedPathBuilder(Pane pane) {
        shapedPath = new ShapedPath(pane);
    }

    public static ShapedPathBuilder create(Stage stage) {
        return new ShapedPathBuilder(stage);
    }
    public static ShapedPathBuilder create(Pane pane) {
        return new ShapedPathBuilder(pane);
    }

    public ShapedPathBuilder moveTo(double x, double y) {
        shapedPath.moveTo(x, y);
        return this;
    }
    public ShapedPathBuilder moveTo(BindablePoint bindablePoint) {
        shapedPath.moveTo(bindablePoint);
        return this;
    }

    public ShapedPathBuilder horzSeg(BindablePoint bindablePoint) {
        shapedPath.horzSeg(bindablePoint);
        return this;
    }
    public ShapedPathBuilder horzSeg(double x) {
        shapedPath.horzSeg(x);
        return this;
    }
    public ShapedPathBuilder vertSeg(BindablePoint bindablePoint) {
        shapedPath.vertSeg(bindablePoint);
        return this;
    }
    public ShapedPathBuilder vertSeg(double y) {
        shapedPath.vertSeg(y);
        return this;
    }
    public ShapedPathBuilder lineSeg(BindablePoint bindablePoint) {
        shapedPath.lineSeg(bindablePoint);
        return this;
    }
    public ShapedPathBuilder lineSeg(double x, double y) {
        shapedPath.lineSeg(xTo(x).yTo(y));
        return this;
    }
    public ShapedPathBuilder closeSeg() {
        shapedPath.closeSeg();
        return this;
    }
    public ShapedPathBuilder addStyleClass(String... names) {
        shapedPath.getStyleClass().addAll(names);
        return this;
    }
    public ShapedPath build() {
        return shapedPath;
    }
// vertical
    public static void main(String[] args) {
        ShapedPath fp = ShapedPathBuilder.create(new Stage())
                .addStyleClass("someClass")
                .moveTo(20, 0)
                .horzSeg(bindXToWidth(-10)) // 0
                .lineSeg(bindXToWidth().yTo(10))     // 1
                .vertSeg(bindYToHeight(-10)) // 2
                .lineSeg(bindXToWidth(-10).bindYToHeight()) // 3
                .horzSeg(bindXToWidth(-200)) // 4
                .lineSeg(bindXToWidth(-210).bindYToHeight(-10)) // 5
                .horzSeg(20) // 6
                .lineSeg(xTo(10).bindYToHeight(-20)) // 7
                .vertSeg(yTo(140)) // 8
                .lineSeg(xTo(0).yTo(130)) // 9
                .vertSeg(yTo(30)) // 10
                .lineSeg(xTo(10).yTo(20)) // 11
                .vertSeg(yTo(10)) // 12
                .closeSeg() // 13
                .build();
// .horzSeg(bindXToWidth(-10))
// .horzSeg( xTo, (sp, xTo) -> sp.bindXToWidth(xTo))
    }
}
