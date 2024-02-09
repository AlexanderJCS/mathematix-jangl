package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class ValueNode extends Node {

    public ValueNode(WorldCoords pos) {
        super(pos, 0, 1, "Value",  0f);
    }

    @Override
    public float compute(float x) {
        return this.nodeValue;
    }
}
