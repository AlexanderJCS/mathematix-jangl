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

import java.util.ArrayList;
import java.util.List;

public class Formula {
    private final List<Node> nodes;
    private Attachment selected;

    public Formula() {
        this.nodes = new ArrayList<>();
        this.nodes.add(new GraphNode(new WorldCoords(0.7f, 0.8f)));
        this.nodes.add(new AddNode(new WorldCoords(0.4f, 0.8f)));
        this.nodes.add(new XNode(new WorldCoords(0.1f, 0.95f)));
        this.nodes.add(new XNode(new WorldCoords(0.1f, 0.45f)));
    }

    private List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<>();

        for (Node node : this.nodes) {
            attachments.addAll(node.getInputAttachments());
            attachments.addAll(node.getOutputAttachments());
        }

        return attachments;
    }

    private Connection makeConnection(Attachment attachment1, Attachment attachment2) {
        if (attachment1.isInput() == attachment2.isInput()) {
            return null;
        }

        return new Connection(
                attachment1.isInput() ? attachment2 : attachment1,
                attachment2.isInput() ? attachment2 : attachment1
        );
    }

    public GpuGraph getGpuGraph() {
        return this.nodes.get(0).getGpuGraph();
    }

    public List<Node> getNodes() {
        return new ArrayList<>(this.nodes);
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



    private void performAction(Attachment attachment) {
        if (this.selected == null) {
            this.selected = attachment;
            return;
        }

        if (this.selected.isInput() != attachment.isInput()) {
            Connection connection = this.makeConnection(this.selected, attachment);

            if (connection != null) {
                this.selected.setConnection(connection);
                attachment.setConnection(connection);
            }
        }

        this.selected = null;
    }

    public void update(List<MouseEvent> mouseEvents) {
        this.selectAttachment(mouseEvents);
    }

    public void draw() {
        for (Node node : this.nodes) {
            for (Attachment attachment : node.getInputAttachments()) {
                Connection connection = attachment.getConnection();
                if (connection != null) {
                    connection.draw();
                }
            }

            node.draw();
        }
    }
}
