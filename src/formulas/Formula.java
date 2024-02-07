package formulas;

import formulas.node.Attachment;
import formulas.node.Connection;
import formulas.node.nodes.AddNode;
import formulas.node.nodes.GraphNode;
import formulas.node.nodes.XNode;
import formulas.node.nodes.gpugraph.GpuGraph;
import jangl.coords.WorldCoords;
import formulas.node.nodes.Node;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;
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
        this.nodes.add(new AddNode(new WorldCoords(0.4f, 0.8f)));
        this.nodes.add(new XNode(new WorldCoords(0.1f, 0.95f)));
        this.nodes.add(new XNode(new WorldCoords(0.1f, 0.45f)));
    }

    public GpuGraph getGpuGraph() {
        return this.nodes.get(0).getGpuGraph();
    }

    public List<Node> getNodes() {
        return new ArrayList<>(this.nodes);
    }

    private List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<>();

        for (Node node : this.nodes) {
            attachments.addAll(node.getInputAttachments());
            attachments.addAll(node.getOutputAttachments());
        }

        return attachments;
    }

    private void selectAttachment(List<MouseEvent> mouseEvents) {
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

    public void update(List<MouseEvent> mouseEvents) {
        if (this.selected != null) {
            this.selectionLine.setStart(this.selected.circle().getTransform().getCenter());
            this.selectionLine.setEnd(Mouse.getMousePos());
        }

        this.selectAttachment(mouseEvents);
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
