package formulas.node.nodes;

import jangl.coords.WorldCoords;

public class ValueNode extends InputBoxNode {
    public ValueNode(WorldCoords pos) {
        super(pos, 0, 1, "Value");
    }

    @Override
    public float compute(float x) {
        return this.getInputValue();
    }
}
