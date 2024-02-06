package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class GraphNode extends Node {
    public GraphNode(WorldCoords pos) {
        super(pos, 1, 0, "Graph", 200, 0);
    }

    @Override
    public float getOutput() {
        return 0;
    }
}
