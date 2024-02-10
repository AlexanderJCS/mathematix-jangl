import formulas.Formula;
import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.Scroll;
import jangl.time.Clock;

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
        this.formulaGui.update(Mouse.getEvents(), Scroll.getEvents());
    }

    public void run() {
        while (Window.shouldRun()) {
            this.graph.setFormula(this.formulaGui);

            this.draw();
            this.update();

            Window.setTitle("Mathematix | " + Math.round(Clock.getSmoothedFps()) + " FPS | " + (Math.round(100000 / Clock.getSmoothedFps()) / 1000f) + " ms");

            Jangl.update();
        }
    }
}
