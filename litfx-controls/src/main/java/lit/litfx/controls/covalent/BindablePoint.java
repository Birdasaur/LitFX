package lit.litfx.controls.covalent;

/**
 * This represents a point where the x and y component is either an absolute value or an offset. When using
 * coordinates as an absolute value the bindWidth or bindHeight flag is set to false. An example is when
 * a point or (end point of a path element [LineTo) is bound and with an offset from the width.
 *
 */
public class BindablePoint {
    private double xOrOffset;
    private double yOrOffset;
    private boolean bindWidth;
    private boolean bindHeight;
    private boolean bindPrevX;
    private boolean bindPrevY;

    public BindablePoint(){}

    public BindablePoint(double x, double y){
        this.xOrOffset = x;
        this.yOrOffset = y;
    }

    public BindablePoint(double x, boolean bindWidth, double y, boolean bindHeight){
        this(x, y);
        this.bindWidth = bindWidth;
        this.bindHeight = bindHeight;
    }

    public BindablePoint xTo(double x) {
        bindWidth = false;
        this.xOrOffset = x;
        return this;
    }

    public BindablePoint yTo(double y) {
        bindHeight = false;
        this.yOrOffset = y;
        return this;
    }

    public BindablePoint bindXToWidth(double offset) {
        bindWidth = true;
        this.xOrOffset = offset;
        return this;
    }
    public BindablePoint bindXToWidth() {
        return bindXToWidth(0);
    }

    public BindablePoint bindYToHeight(double offset) {
        bindHeight = true;
        this.yOrOffset = offset;
        return this;
    }
    public BindablePoint bindYToHeight() {
        return bindYToHeight(0);
    }

    public double getXOrOffset() {
        return xOrOffset;
    }

    public void setXOrOffset(double xOrOffset) {
        this.xOrOffset = xOrOffset;
    }

    public double getYOrOffset() {
        return yOrOffset;
    }

    public void setYOrOffset(double yOrOffset) {
        this.yOrOffset = yOrOffset;
    }

    public boolean isBindWidth() {
        return bindWidth;
    }

    public void setBindWidth(boolean bindWidth) {
        this.bindWidth = bindWidth;
    }

    public boolean isBindHeight() {
        return bindHeight;
    }

    public void setBindHeight(boolean bindHeight) {
        this.bindHeight = bindHeight;
    }

    public boolean isBindPrevX() {
        return bindPrevX;
    }

    public void setBindPrevX(boolean bindPrevX) {
        this.bindPrevX = bindPrevX;
    }

    public boolean isBindPrevY() {
        return bindPrevY;
    }

    public void setBindPrevY(boolean bindPrevY) {
        this.bindPrevY = bindPrevY;
    }

    public BindablePoint clone() {
        BindablePoint clone = new BindablePoint(this.xOrOffset, this.bindWidth, this.yOrOffset, this.bindHeight);
        clone.setBindPrevX(this.bindPrevX);
        clone.setBindPrevY(this.bindPrevY);
        return clone;
    }
}
