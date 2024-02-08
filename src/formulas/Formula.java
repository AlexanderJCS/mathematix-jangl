package formulas;

import formulas.node.Attachment;
import formulas.node.Connection;
import formulas.node.nodes.*;
import jangl.coords.WorldCoords;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL41;
import ui.Line;

import java.util.ArrayList;
import java.util.List;

public class Formula {
    private final List<Node> nodes;
    private Attachment selected;
    private final Line selectionLine;

    public Formula() {
        this.selectionLine = new Line(new WorldCoords(0, 0), new WorldCoords(0, 0), 0.01f);

        this.nodes = new ArrayList<>();
        this.nodes.add(new GraphNode(new WorldCoords(0.7f, 0.8f)));
        this.nodes.add(new MulNode(new WorldCoords(0.4f, 0.8f)));
        this.nodes.add(new XNode(new WorldCoords(0.1f, 0.95f)));
        this.nodes.add(new XNode(new WorldCoords(0.1f, 0.45f)));
    }

    private Node getGraphNode() {
        for (Node node : this.nodes) {
            if (node instanceof GraphNode) {
                return node;
            }
        }

        return null;
    }

    public void uploadUniforms(float start, float end, int n, String uniformName, int programID) {
        float[] yValues = new float[n];

        float step = (end - start) / n;
        for (int i = 0; i < yValues.length; i++) {
            float xValue = start + step * i;

            Node graphNode = this.getGraphNode();
            if (graphNode == null) {
                yValues[i] = 0;
                continue;
            }

            yValues[i] = graphNode.compute(xValue);
        }

        for (int i = 0; i < yValues.length; i++) {
            int location = GL41.glGetUniformLocation(programID, uniformName + "[" + i + "]");
            GL41.glUniform1f(location, yValues[i]);
        }
    }

    private List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<>();

        for (Node node : this.nodes) {
            attachments.addAll(node.getInputAttachments());
            attachments.addAll(node.getOutputAttachments());
        }

        return attachments;
    }

    private void updateAttachments(List<MouseEvent> mouseEvents) {
        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1 || event.action != GLFW.GLFW_RELEASE) {
                continue;
            }

            boolean selected = false;
            for (Attachment attachment : this.getAttachments()) {
                if (Shape.collides(attachment.circle(), Mouse.getMousePos())) {
                    selected = true;
                    this.performAction(attachment);
                    break;
                }
            }

            if (!selected) {
                this.selected = null;
            }
        }
    }

    private void makeConnection(Attachment attachment) {
        if (this.selected == null || this.selected.isInput() == attachment.isInput()) {
            this.selected = null;
            return;
        }

        Connection connection = new Connection(
                this.selected.isInput() ? attachment : this.selected,
                attachment.isInput() ? attachment : this.selected
        );

        this.selected.setConnection(connection);
        attachment.setConnection(connection);
    }

    private void removeConnection(Attachment attachment) {
        Connection connection = attachment.getConnection();

        if (connection == null) {
            return;
        }

        connection.getIn().setConnection(null);
        connection.getOut().setConnection(null);
    }

    private void performAction(Attachment attachment) {
        if (this.selected == null && attachment != null && attachment.getConnection() == null) {
            this.selected = attachment;
            return;
        }

        // If you try to connect to a node that already has a connection, and you currently have no selection, then
        // remove the connection
        if (this.selected == null && attachment != null) {
            this.removeConnection(attachment);
            return;
        }

        if (this.selected == attachment) {
            this.selected = null;
            return;
        }

        if (this.selected.getConnection() == null && attachment.getConnection() == null) {
            this.makeConnection(attachment);
        }

        this.selected = null;
    }

    /**
     * Remove all boxes that should be removed when the mouse is clicked.
     */
    private void removeBoxes(List<MouseEvent> mouseEvents) {
        boolean clicked = false;
        for (MouseEvent event : mouseEvents) {
            if (event.button == GLFW.GLFW_MOUSE_BUTTON_1 && event.action == GLFW.GLFW_RELEASE) {
                clicked = true;
                break;
            }
        }

        if (!clicked) {
            return;
        }

        for (Node node : this.nodes) {
            if (!node.shouldClose(Mouse.getMousePos())) {
                continue;
            }

            for (Attachment attachment : node.getInputAttachments()) {
                this.removeConnection(attachment);
            }

            for (Attachment attachment : node.getOutputAttachments()) {
                this.removeConnection(attachment);
            }

            this.nodes.remove(node);
            break;
        }
    }

    public void update(List<MouseEvent> mouseEvents) {
        // Update the selection line
        if (this.selected != null) {
            this.selectionLine.setStart(this.selected.circle().getTransform().getCenter());
            this.selectionLine.setEnd(Mouse.getMousePos());
        }

        // Handle selecting / removing attachments
        this.updateAttachments(mouseEvents);

        // Handle the closing of nodes
        this.removeBoxes(mouseEvents);

        for (Node node : this.nodes) {
            node.update(mouseEvents);
        }
    }

    public void draw() {
        // Draw nodes
        for (Node node : this.nodes) {
            node.draw();
        }

        // Draw connections
        for (Node node : this.nodes) {
            for (Attachment attachment : node.getInputAttachments()) {
                Connection connection = attachment.getConnection();
                if (connection != null) {
                    connection.draw();
                }
            }
        }

        if (this.selected != null) {
            this.selectionLine.draw();
        }
    }
}
