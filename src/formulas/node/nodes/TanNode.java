package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class TanNode extends Node {
    public TanNode(WorldCoords pos) {
        super(pos, 1, 1, "Tan");
    }

    @Override
    public float compute(float x) {
        if (this.getInputAttachments().get(0).getConnection() == null) {
            return 0;
        }

        return (float) Math.tan(this.getInputAttachments().get(0).getConnection().getOut().node().compute(x));
    }
}
