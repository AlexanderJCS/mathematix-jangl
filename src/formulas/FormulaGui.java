package formulas;

import jangl.coords.WorldCoords;
import formulas.node.Node;

import java.util.ArrayList;
import java.util.List;

public class FormulaGui {
    private final List<Node> nodes;

    public FormulaGui() {
        this.nodes = new ArrayList<>();
        this.nodes.add(new Node(WorldCoords.getMiddle(), 2, 1, "Test"));
    }

    public void draw() {
        for (Node node : this.nodes) {
            node.draw();
        }
    }
}
