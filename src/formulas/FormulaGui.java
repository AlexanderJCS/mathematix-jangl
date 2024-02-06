package formulas;

import formulas.node.Attachment;
import formulas.node.Connection;
import jangl.coords.WorldCoords;
import formulas.node.Node;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class FormulaGui {
    private final List<Node> nodes;
    private final List<Connection> connections;

    public FormulaGui() {
        this.nodes = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.nodes.add(new Node(WorldCoords.getMiddle(), 2, 1, "Test"));
    }

    private List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<>();

        for (Node node : this.nodes) {
            attachments.addAll(node.getInputAttachments());
            attachments.addAll(node.getOutputAttachments());
        }

        return attachments;
    }

    private List<Attachment> getSelectedAttachments() {
        List<Attachment> selected = new ArrayList<>();

        for (Attachment attachment : this.getAttachments()) {
            if (attachment.isSelected()) {
                selected.add(attachment);
            }
        }

        return selected;
    }

    private Connection makeConnection(Attachment attachment1, Attachment attachment2) {
        attachment1.deselect();
        attachment2.deselect();

        if (attachment1.isInput() == attachment2.isInput()) {
            return null;
        }

        return new Connection(
                attachment1.isInput() ? attachment2 : attachment1,
                attachment2.isInput() ? attachment2 : attachment1
        );
    }

    public void update(List<MouseEvent> mouseEvents) {
        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1 || event.action != GLFW.GLFW_RELEASE) {
                continue;
            }

            for (Attachment attachment : this.getAttachments()) {
                if (Shape.collides(attachment.circle(), Mouse.getMousePos())) {
                    attachment.flipSelection();
                }
            }
        }

        List<Attachment> selected = this.getSelectedAttachments();
        if (selected.size() > 1) {
            Connection connection = this.makeConnection(selected.get(0), selected.get(1));

            if (connection != null) {
                this.connections.add(connection);
            }
        }
    }

    public void draw() {
        for (Node node : this.nodes) {
            node.draw();
        }

        for (Connection connection : this.connections) {
            connection.draw();
        }
    }
}
