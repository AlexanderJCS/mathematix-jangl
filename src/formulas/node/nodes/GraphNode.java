package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class GraphNode extends Node {
    public GraphNode(WorldCoords pos) {
        super(pos, 1, 0, "Graph");

        this.allowClosing(false);  // do not close the graph node
    }

    @Override
    public float compute(float x) {
        if (this.getInputAttachments().get(0).getConnection() != null) {
            return this.getInputAttachments().get(0).getConnection().getOut().node().compute(x);
        }

        return 0;
    }
}
