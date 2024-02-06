package formulas.node;

import formulas.node.nodes.Node;
import jangl.shapes.Circle;

public class Attachment {
    private final Node node;
    private final Circle circle;
    private final boolean isInput;
    private boolean selected;
    private Connection connection;

    public Attachment(Node node, Circle circle, boolean isInput) {
        this.node = node;
        this.circle = circle;
        this.isInput = isInput;
        this.selected = false;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void select() {
        this.selected = true;
    }

    public void deselect() {
        this.selected = false;
    }

    public void flipSelection() {
        this.selected = !this.selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Node node() {
        return this.node;
    }

    public Circle circle() {
        return this.circle;
    }

    public boolean isInput() {
        return this.isInput;
    }

    public void draw() {
        this.circle.draw();
    }
}
