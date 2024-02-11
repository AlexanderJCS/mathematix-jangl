package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;

public class PowerNode extends Node {

    public PowerNode(WorldCoords pos) {
        super(pos, 2, 1, "Power");
    }

    @Override
    public float compute(float x) {
        for (Attachment attachment : this.getInputAttachments()) {
            if (attachment.getConnection() == null) {
                return 0;
            }
        }

        float base = this.getInputAttachments().get(0).getConnection().getOut().node().compute(x);
        float exponent = this.getInputAttachments().get(1).getConnection().getOut().node().compute(x);

        return (float) Math.pow(base, exponent);
    }
}
