package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;

import java.util.List;

public class MulNode extends Node {
    public MulNode(WorldCoords pos) {
        super(pos, 2, 1, "Mul");
    }

    @Override
    public float compute(float x) {
        List<Attachment> inputs = this.getInputAttachments();

        float product = 1;

        for (Attachment input : inputs) {
            if (input.getConnection() == null) {
                return 0;
            }

            product *= input.getConnection().getOut().node().compute(x);
        }

        return product;
    }
}
