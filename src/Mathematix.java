import formulas.Formula;
import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.io.mouse.Scroll;
import jangl.io.mouse.ScrollEvent;
import jangl.time.Clock;

import java.util.List;

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
        List<MouseEvent> mouseEvents = Mouse.getEvents();
        List<ScrollEvent> scrollEvents = Scroll.getEvents();

        this.formulaGui.update(mouseEvents, scrollEvents);
        this.graph.update(scrollEvents);
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
