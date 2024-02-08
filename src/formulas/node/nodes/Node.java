package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Circle;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    private final SelectionData selectionData;
    // TODO: make fields private that can be private
    private final Rect closeBox;
    protected boolean useCloseBox;
    private final Rect dragBar;
    private final Rect rect;
    private final List<Attachment> inputAttachments;
    private final List<Attachment> outputAttachments;
    private final Text nodeTitle;
    public final int nodeType;
    public final Float nodeValue;
    public final int uniqueID;
    private static int uniqueIDCounter = 0;

    private static final ShaderProgram BAR_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.2f, 0.2f, 0.2f, 1.0f))
    );

    private static final ShaderProgram NODE_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    private static final ShaderProgram CLOSE_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.8f, 0.2f, 0.2f, 1.0f))
    );

    private class SelectionData {
        boolean selected = false;
        WorldCoords lastMousePos = new WorldCoords(0, 0);
    }

    public Node(WorldCoords pos, int attachmentsIn, int attachmentsOut, String nodeTitle, int nodeType, Float nodeValue) {
        this.rect = new Rect(pos, 0.2f, 0.3f);
        this.dragBar = new Rect(pos, this.rect.getWidth(), this.rect.getWidth() / 5);
        this.closeBox = new Rect(new WorldCoords(pos.x + this.dragBar.getWidth() - this.dragBar.getHeight(), pos.y), this.dragBar.getHeight(), this.dragBar.getHeight());
        this.useCloseBox = true;

        this.nodeTitle = new TextBuilder(
                new Font("resources/font/arial.fnt", "resources/font/arial.png"),
                nodeTitle
        )
                .setCoords(new WorldCoords(pos.x, pos.y - this.dragBar.getHeight()))
                .setWrapWidth(this.rect.getWidth())
                .setYHeight(0.05f)
                .toText();

        // Both lists must be initialized before calling genAttachments
        this.inputAttachments = new ArrayList<>();
        this.outputAttachments = new ArrayList<>();

        this.genAttachments(attachmentsIn, this.inputAttachments, true);
        this.genAttachments(attachmentsOut, this.outputAttachments, false);

        this.nodeType = nodeType;
        this.nodeValue = nodeValue;

        this.uniqueID = uniqueIDCounter;
        uniqueIDCounter++;

        this.selectionData = new SelectionData();
    }

    private void genAttachments(int numAttachments, List<Attachment> attachments, boolean input) {
        for (int i = 0; i < numAttachments; i++) {
            attachments.add(
                    new Attachment(
                            this,
                            new Circle(new WorldCoords(0, 0), 0.02f, 16),
                            input
                    )
            );
        }

        this.updateAttachmentLocations(attachments, input);
    }

    private void updateAttachmentLocations(List<Attachment> attachments, boolean input) {
        WorldCoords rectCenter = this.rect.getTransform().getCenter();
        float rectWidth = this.rect.getWidth();
        float rectHeight = this.rect.getHeight();

        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);

            int multiplier = input ? -1 : 1;

            attachment.circle().getTransform().setPos(
                    new WorldCoords(
                            rectCenter.x + rectWidth / 2 * multiplier,
                            rectCenter.y + rectHeight / 2 * multiplier * i / attachments.size()
                    )
            );
        }
    }

    public boolean shouldClose(WorldCoords mousePos) {
        return this.useCloseBox && Shape.collides(this.closeBox, mousePos);
    }

    public void draw() {
        this.rect.draw(NODE_COLOR);
        this.dragBar.draw(BAR_COLOR);

        if (this.useCloseBox) {
            this.closeBox.draw(CLOSE_COLOR);
        }

        this.nodeTitle.draw();

        for (Attachment attachment : this.inputAttachments) {
            attachment.draw();
        }

        for (Attachment attachment : this.outputAttachments) {
            attachment.draw();
        }
    }

    private void updateSelectionData(List<MouseEvent> mouseEvents) {
        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1 || !Shape.collides(this.dragBar, Mouse.getMousePos())) {
                continue;
            }

            if (event.action == GLFW.GLFW_PRESS) {
                this.selectionData.selected = true;
                this.selectionData.lastMousePos = Mouse.getMousePos();

            } else if (event.action == GLFW.GLFW_RELEASE) {
                this.selectionData.selected = false;
            }
        }
    }

    private void drag() {
        WorldCoords mousePos = Mouse.getMousePos();
        WorldCoords offset = new WorldCoords(
                mousePos.x - this.selectionData.lastMousePos.x,
                mousePos.y - this.selectionData.lastMousePos.y
        );

        this.nodeTitle.getTransform().shift(offset);
        this.rect.getTransform().shift(offset);
        this.dragBar.getTransform().shift(offset);
        this.closeBox.getTransform().shift(offset);

        for (Attachment attachment : this.inputAttachments) {
            attachment.circle().getTransform().shift(offset);

            if (attachment.getConnection() != null) {
                attachment.getConnection().update();
            }
        }

        for (Attachment attachment : this.outputAttachments) {
            attachment.circle().getTransform().shift(offset);

            if (attachment.getConnection() != null) {
                attachment.getConnection().update();
            }
        }
    }

    public void update(List<MouseEvent> mouseEvents) {
        this.updateSelectionData(mouseEvents);

        if (this.selectionData.selected) {
            this.drag();
            this.selectionData.lastMousePos = Mouse.getMousePos();
        }
    }

    public List<Attachment> getInputAttachments() {
        return new ArrayList<>(this.inputAttachments);
    }

    public List<Attachment> getOutputAttachments() {
        return new ArrayList<>(this.outputAttachments);
    }

    public static int getUniqueIDCounter() {
        return uniqueIDCounter;
    }

    public abstract float compute(float x);
}
