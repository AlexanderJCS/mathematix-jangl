package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class XNode extends Node {
    public XNode(WorldCoords pos) {
        super(pos, 0, 1, "X",  null);
    }

    @Override
    public float compute(float x) {
        return x;
    }
}
