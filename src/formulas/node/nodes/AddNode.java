package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;

import java.util.List;

public class AddNode extends Node {
    public AddNode(WorldCoords pos) {
        super(pos, 2, 1, "Add", 4, 0);
    }

    @Override
    public float getOutput() {
        List<Attachment> inputs = this.getInputAttachments();

        float sum = 0;
        for (Attachment attachment : inputs) {
            if (attachment.getConnection() == null) {
                return 0;
            }

            sum += attachment.getConnection().getIn().node().getOutput();
        }

        return sum;
    }
}
