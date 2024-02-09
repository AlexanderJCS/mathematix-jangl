package ui;

import formulas.node.nodes.Node;
import jangl.shapes.Rect;

import java.util.Map;

public class NodeCreator {
    private final Map<String, Class<? extends Node>> selectionItems;
    private final Rect rect;

    public NodeCreator(Map<String, Class<? extends Node>> selectionItems) {
        this.selectionItems = selectionItems;
    }


}
