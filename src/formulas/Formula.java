package formulas;

import formulas.node.Attachment;
import formulas.node.Connection;
import formulas.node.nodes.*;
import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.io.keyboard.KeyEvent;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.io.mouse.ScrollEvent;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL41;
import ui.Line;
import ui.NodeCreator;
import ui.drag.Draggable;
import ui.drag.Dragger;

import java.util.*;

public class Formula implements Draggable {
    private final Rect background;
    private final List<Node> nodes;
    private Attachment selected;
    private final Line selectionLine;
    private final NodeCreator nodeCreator;
    private final Dragger dragger;
    private final ShaderProgram bgShader;

    public Formula() {
        this.background = new Rect(
                new WorldCoords(-1000, 1000), 2000, 2000
        );

        BackgroundShader bgShader = new BackgroundShader();
        bgShader.setWidthHeight(new WorldCoords(this.background.getWidth(), this.background.getHeight()));
        this.bgShader = new ShaderProgram(
                new TextureShaderVert(),
                bgShader
        );

        this.selectionLine = new Line(new WorldCoords(0, 0), new WorldCoords(0, 0), Connection.THICKNESS);

        this.nodes = new ArrayList<>();
        this.nodes.add(new GraphNode(new WorldCoords(0.4f, 0.8f)));

        LinkedHashMap<String, Class<? extends Node>> selectionItems = new LinkedHashMap<>();
        selectionItems.put("X", XNode.class);
        selectionItems.put("Value", ValueNode.class);
        selectionItems.put("Add", AddNode.class);
        selectionItems.put("Sub", SubNode.class);
        selectionItems.put("Mul", MulNode.class);
        selectionItems.put("Div", DivNode.class);
        selectionItems.put("Power", PowerNode.class);
        selectionItems.put("Sin", SinNode.class);
        selectionItems.put("Cos", CosNode.class);
        selectionItems.put("Tan", TanNode.class);

        this.nodeCreator = new NodeCreator(selectionItems, this);
        this.dragger = new Dragger(this, true);
    }

    public void addNode(Node node) {
        this.nodes.add(node);
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

    private void updateConnections(List<MouseEvent> mouseEvents) {
        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1) {
                continue;
            }

            boolean selected = false;
            for (Attachment attachment : this.getAttachments()) {
                if (Shape.collides(attachment.circle(), Mouse.getMousePosAdjusted())) {
                    selected = true;
                    this.performAction(attachment, event.action == GLFW.GLFW_PRESS);
                    break;
                }
            }

            if (!selected) {
                this.select(null);
            }
        }
    }

    private void makeConnection(Attachment attachment) {
        if (this.selected == null || this.selected.isInput() == attachment.isInput()) {
            this.select(null);
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

    private void performAction(Attachment attachment, boolean press) {
        // Return, since it's likely a click-and-press instead of a click-and-drag
        if (attachment == this.selected && !press) {
            return;
        }

        if (this.selected == null && press && attachment != null && attachment.getConnection() == null) {
            this.select(attachment);
            return;
        }

        // If you try to connect to a node that already has a connection, and you currently have no selection, then
        // remove the connection
        if (this.selected == null && attachment != null && press) {
            this.removeConnection(attachment);
            return;
        }

        if (this.selected == attachment) {
            this.select(null);
            return;
        }

        // do not double-up on connections
        if (this.selected == null) {
            return;
        }

        boolean bothNoConnection = this.selected.getConnection() == null && attachment.getConnection() == null;

        // do not connect to itself
        boolean differentNode = this.selected.node() != attachment.node();

        if (bothNoConnection && differentNode) {
            this.makeConnection(attachment);
        }

        this.select(null);
    }

    /**
     * Remove all boxes that should be removed when the mouse is clicked.
     */
    private void removeBoxes() {
        for (Node node : this.nodes) {
            if (!node.shouldClose()) {
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

    /**
     * Updates this.selected and the selection line.
     */
    private void select(Attachment attachment) {
        this.selected = attachment;
        this.updateSelectionLine();
    }

    private void updateSelectionLine() {
        if (this.selected != null) {
            this.selectionLine.setStart(this.selected.circle().getTransform().getCenter());
            this.selectionLine.setEnd(Mouse.getMousePosAdjusted());
        }
    }

    private void zoom(float zoom) {
        float newZoom = Camera.getZoom() * zoom;

        if (newZoom < 0.2f || newZoom > 5f) {
            return;
        }

        Camera.setZoom(newZoom);
    }

    private void resetZoomOffset() {
        Camera.setZoomOffset(new WorldCoords(
                (WorldCoords.getTopRight().x - 1) / 2,
                WorldCoords.getMiddle().y
        ));
    }

    public void update(List<KeyEvent> keyEvents, List<MouseEvent> mouseEvents, List<ScrollEvent> scrollEvents) {
        this.resetZoomOffset();
        this.dragger.update();

        this.updateSelectionLine();

        // Handle selecting / removing attachments
        this.updateConnections(mouseEvents);

        // Handle the closing of nodes
        this.removeBoxes();

        for (Node node : this.nodes) {
            node.update(keyEvents, mouseEvents);
        }

        this.nodeCreator.update(mouseEvents);

        // TODO: refactor and move to function
        // Handle dragging the background
        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1) {
                continue;
            }

            if (event.action == GLFW.GLFW_RELEASE) {
                this.dragger.deselect();
                continue;
            }

            if (!Shape.collides(this.background, Mouse.getMousePos())) {
                continue;
            }

            boolean canDrag = true;
            for (Node node : this.nodes) {
                if (Shape.collides(node.getRect(), Mouse.getMousePosAdjusted())) {
                    canDrag = false;
                    break;
                }
            }

            for (Attachment attachment : this.getAttachments()) {
                if (Shape.collides(attachment.circle(), Mouse.getMousePosAdjusted())) {
                    canDrag = false;
                    break;
                }
            }

            if (canDrag) {
                this.dragger.select();
            }
        }

        // Do not zoom or drag if the mouse is not over the formulas area
        if (Mouse.getMousePos().x + 1 > WorldCoords.getTopRight().x) {
            return;
        }

        for (ScrollEvent event : scrollEvents) {
            if (event.yOffset > 0) {
                this.zoom(1 + 0.05f * (float) Math.abs(event.yOffset));
            } else {
                this.zoom(1 - 0.05f * (float) Math.abs(event.yOffset));
            }
        }
    }

    public void draw() {
        // Draw background
        this.background.draw(bgShader);

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

        this.nodeCreator.draw();
    }

    @Override
    public void drag(WorldCoords offset) {
        WorldCoords cameraPos = Camera.getCameraPos();
        cameraPos.sub(offset);
        Camera.setCameraPos(cameraPos);
    }
}
