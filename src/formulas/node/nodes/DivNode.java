package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;

public class DivNode extends Node {

    public DivNode(WorldCoords pos) {
        super(pos, 2, 1, "Divide", null);
    }

    @Override
    public float compute(float x) {
        if (this.getInputAttachments().get(0).getConnection() == null) {
            return 0;
        }

        if (this.getInputAttachments().get(1).getConnection() == null) {
            return 0;
        }

        float dividend = this.getInputAttachments().get(0).getConnection().getOut().node().compute(x);
        float divisor = this.getInputAttachments().get(1).getConnection().getOut().node().compute(x);

        return dividend / divisor;
    }
}
