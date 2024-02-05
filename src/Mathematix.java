import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;

public class Mathematix {
    private final Graph graph;

    public Mathematix() {
        this.graph = new Graph();
    }

    public void draw() {
        Window.clear();
        this.graph.draw();
    }

    public void run() {
        while (Window.shouldRun()) {
            this.draw();
            Jangl.update();
        }
    }
}
