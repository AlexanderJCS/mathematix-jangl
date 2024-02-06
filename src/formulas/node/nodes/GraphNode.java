package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class GraphNode extends Node {
    public GraphNode(WorldCoords pos) {
        super(pos, 1, 0, "GpuGraph", 2, 0);
    }

    @Override
    public float getOutput() {
        return 0;
    }
}
