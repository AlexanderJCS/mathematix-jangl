import formulas.Formula;
import grapher.Graph;
import jangl.Jangl;
import jangl.graphics.Camera;
import jangl.io.Window;
import jangl.io.keyboard.KeyEvent;
import jangl.io.keyboard.Keyboard;
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
        this.formulaGui.draw();
        this.graph.draw();
    }

    private void update() {
        List<KeyEvent> keyEvents = Keyboard.getEvents();
        List<MouseEvent> mouseEvents = Mouse.getEvents();
        List<ScrollEvent> scrollEvents = Scroll.getEvents();

        this.formulaGui.update(keyEvents, mouseEvents, scrollEvents);
        this.graph.update(mouseEvents, scrollEvents);
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
