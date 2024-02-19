package formulas.node.nodes;

import formulas.node.Connection;
import jangl.coords.WorldCoords;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class DivNode extends Node {

    public DivNode(WorldCoords pos) {
        super(pos, 2, 1, "Divide");
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

    @Override
    public List<Vector2f> getInvalidRanges() {
        Connection divisorConn = this.getInputAttachments().get(1).getConnection();

        if (divisorConn == null) {
            return new ArrayList<>();
        }

        float asymptote = -1 * divisorConn.getOut().node().compute(0);

        return new ArrayList<>(List.of(new Vector2f(asymptote, asymptote)));
    }
}
