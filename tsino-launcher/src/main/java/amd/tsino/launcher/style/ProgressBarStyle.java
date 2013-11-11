package amd.tsino.launcher.style;

public class ProgressBarStyle extends ImagePanelStyle {
    public final Padding padding;
    public final String start;
    public final String end;
    public final String body;

    public ProgressBarStyle() {
        this.padding = new Padding();
        this.start = null;
        this.end = null;
        this.body = null;
    }
}
