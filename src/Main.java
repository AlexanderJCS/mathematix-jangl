import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;

public class Main {
    public static void main(String[] args) {
        Jangl.init(0.5f, 16f / 9);

        Graph graph = new Graph();

        while (Window.shouldRun()) {
            Window.clear();
            graph.draw();
            Jangl.update();
        }

        Window.close();
    }
}