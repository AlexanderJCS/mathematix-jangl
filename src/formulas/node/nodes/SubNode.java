package formulas.node.nodes;

import formulas.node.Attachment;
import formulas.node.Connection;
import jangl.coords.WorldCoords;

import java.util.List;

public class SubNode extends Node {
    public SubNode(WorldCoords pos) {
            super(pos, 2, 1, "Sub");
        }

    @Override
    public float compute(float x) {
        List<Attachment> inputs = this.getInputAttachments();

        Connection con = inputs.get(0).getConnection();
        float sub = con != null ? con.getOut().node().compute(x) : 0;

        for (int i = 1; i < inputs.size(); i++) {
            con = inputs.get(i).getConnection();
            sub -= con != null ? con.getOut().node().compute(x) : 0;
        }

        return sub;
    }
}
