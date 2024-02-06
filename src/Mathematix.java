import formulas.FormulaGui;
import grapher.Graph;
import jangl.Jangl;
import jangl.io.Window;

public class Mathematix {
    private final Graph graph;
    private final FormulaGui formulaGui;

    public Mathematix() {
        this.graph = new Graph();
        this.formulaGui = new FormulaGui();
    }

    public void draw() {
        Window.clear();
        this.graph.draw();
        this.formulaGui.draw();
    }

    public void run() {
        while (Window.shouldRun()) {
            this.draw();
            Jangl.update();
        }
    }
}
