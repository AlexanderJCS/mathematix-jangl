package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class SinNode extends Node {
    public SinNode(WorldCoords pos) {
        super(pos, 1, 1, "Sin");
    }

    @Override
    public float compute(float x) {
        if (this.getInputAttachments().get(0).getConnection() == null) {
            return 0;
        }

        return (float) Math.sin(this.getInputAttachments().get(0).getConnection().getOut().node().compute(x));
    }
}
