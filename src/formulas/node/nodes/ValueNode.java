package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class ValueNode extends Node {

    public ValueNode(WorldCoords pos, float value) {
        super(pos, 0, 1, "Value",  value);
    }

    @Override
    public float compute(float x) {
        return this.nodeValue;
    }
}
