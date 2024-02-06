import formulas.FormulaGui;
import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;
import jangl.io.mouse.Mouse;

public class Mathematix {
    private final Graph graph;
    private final FormulaGui formulaGui;

    public Mathematix() {
        this.graph = new Graph();
        this.formulaGui = new FormulaGui();
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
            this.draw();
            this.update();

            Jangl.update();
        }
    }
}
