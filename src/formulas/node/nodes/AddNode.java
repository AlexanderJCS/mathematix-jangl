package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;

import java.util.List;

public class AddNode extends Node {
    public AddNode(WorldCoords pos) {
        super(pos, 2, 1, "Add", 4, null);
    }
}
