package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class CosNode extends Node {
    public CosNode(WorldCoords pos) {
        super(pos, 1, 1, "Cos");
    }

    @Override
    public float compute(float x) {
        if (this.getInputAttachments().get(0).getConnection() == null) {
            return 0;
        }

        return (float) Math.cos(this.getInputAttachments().get(0).getConnection().getOut().node().compute(x));
    }
}
