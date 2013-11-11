package amd.tsino.launcher.style;

public class TextFieldStyle {
    public final int x;
    public final int y;
    public final Padding padding;
    public final String font;
    public final int size;
    public final String tooltip;
    public final String color;
    public final String normal;
    public final String hover;

    public TextFieldStyle() {
        this.x = 0;
        this.y = 0;
        this.padding = new Padding();
        this.font = null;
        this.size = 10;
        this.tooltip = null;
        this.color = null;
        this.normal = null;
        this.hover = null;
    }
}
