package formulas.node;

import jangl.shapes.Circle;

public record Attachment(Node node, Circle circle, boolean isInput) {
    public void draw() {
        this.circle.draw();
    }
}
