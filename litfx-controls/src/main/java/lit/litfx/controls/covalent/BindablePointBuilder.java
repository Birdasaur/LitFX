package lit.litfx.controls.covalent;

public class BindablePointBuilder {

    public static BindablePoint pt(double x, double y) {
        return new BindablePoint(x, y);
    }

    public static BindablePoint pt() {
        return new BindablePoint();
    }
    public static BindablePoint bindXToPrevX(double offset) {
        BindablePoint pt = pt();
        pt.setXOrOffset(offset);
        pt.setBindPrevX(true);
        return pt;
    }
    public static BindablePoint bindXToWidth(double offset) {
        BindablePoint pt = pt();
        pt.setBindWidth(true);
        pt.setXOrOffset(offset);
        return pt;
    }
    public static BindablePoint bindXToWidth() {
        return bindXToWidth(0);
    }

    public static BindablePoint bindYToPrevY(double offset) {
        BindablePoint pt = pt();
        pt.setYOrOffset(offset);
        pt.setBindPrevY(true);
        return pt;
    }

    public static BindablePoint bindYToHeight(double offset) {
        BindablePoint pt = pt();
        pt.setBindHeight(true);
        pt.setYOrOffset(offset);
        return pt;
    }
    public static BindablePoint bindYToHeight() {
        return bindYToHeight(0);
    }

    public static BindablePoint xTo(double x) {
        BindablePoint pt = pt(x, 0);
        pt.setBindWidth(false);
        pt.setXOrOffset(x);
        return pt;
    }

    public static BindablePoint yTo(double y) {
        BindablePoint pt = pt();
        pt.setBindHeight(false);
        pt.setYOrOffset(y);
        return pt;
    }
}
