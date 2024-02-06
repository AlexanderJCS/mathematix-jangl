import formulas.Formula;
import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;
import jangl.io.mouse.Mouse;
import jangl.time.Clock;

import java.util.Arrays;

public class Mathematix {
    private final Graph graph;
    private final Formula formulaGui;

    public Mathematix() {
        this.graph = new Graph();
        this.formulaGui = new Formula();
    }

    private void draw() {
        Window.clear();
        this.graph.draw();
        this.formulaGui.draw();
    }

    private void update() {
        this.formulaGui.update(Mouse.getEvents());
    }

    public void run() {
        while (Window.shouldRun()) {
            int[] data = this.formulaGui.getGraphData();
            System.out.println(data.length);
            System.out.println(Arrays.toString(data));

            this.graph.setGraphData(this.formulaGui.getGraphData());

            this.draw();
            this.update();

            Window.setTitle("Mathematix - " + Math.round(Clock.getSmoothedFps()) + " FPS");

            Jangl.update();
        }
    }
}
