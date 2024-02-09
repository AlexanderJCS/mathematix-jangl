package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;

import java.util.List;

public class AddNode extends Node {
    public AddNode(WorldCoords pos) {
        super(pos, 2, 1, "Add",  null);
    }

    @Override
    public float compute(float x) {
        List<Attachment> inputs = this.getInputAttachments();

        float sum = 0;

        for (Attachment input : inputs) {
            if (input.getConnection() == null) {
                continue;
            }

            sum += input.getConnection().getOut().node().compute(x);
        }

        return sum;
    }
}
