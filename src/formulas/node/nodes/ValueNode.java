package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class ValueNode extends Node {
    private float value;

    public ValueNode(WorldCoords pos, int value) {
        super(pos, 0, 1, "Value", 1, value);

        this.value = value;
    }

    @Override
    public float getOutput() {
        return this.value;
    }
}
