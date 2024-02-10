package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Justify;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Circle;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import jangl.shapes.Transform;
import org.lwjgl.glfw.GLFW;
import ui.drag.Draggable;
import ui.drag.Dragger;

import java.util.ArrayList;
import java.util.List;

public abstract class Node implements Draggable {
    private final Dragger dragger;
    private final Rect closeBox;
    private boolean useCloseBox;
    private final Rect dragBar;
    private final Rect rect;
    private final List<Attachment> inputAttachments;
    private final List<Attachment> outputAttachments;
    private final Text nodeTitle;
    protected Float nodeValue;

    private static final ShaderProgram BAR_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.2f, 0.2f, 0.2f, 1.0f))
    );

    private static final ShaderProgram NODE_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    private static final ShaderProgram CLOSE_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.8f, 0.2f, 0.2f, 1.0f))
    );

    public Node(WorldCoords pos, int attachmentsIn, int attachmentsOut, String nodeTitle, Float nodeValue) {
        this.rect = new Rect(pos, 0.2f, 0.2f);
        this.dragBar = new Rect(pos, this.rect.getWidth(), this.rect.getWidth() / 5);
        this.closeBox = new Rect(pos, this.dragBar.getHeight(), this.dragBar.getHeight());
        this.refreshCloseBoxPos();
        this.useCloseBox = true;

        this.nodeTitle = new TextBuilder(
                new Font("resources/font/poppins.fnt", "resources/font/poppins.png"),
                nodeTitle
        )
                .setWrapWidth(this.rect.getWidth())
                .setJustification(Justify.CENTER)
                .setHeight(this.dragBar.getHeight() * 0.9f)
                .toText();

        this.refreshTextPos();

        // Both lists must be initialized before calling genAttachments
        this.inputAttachments = new ArrayList<>();
        this.outputAttachments = new ArrayList<>();

        this.genAttachments(attachmentsIn, this.inputAttachments, true);
        this.genAttachments(attachmentsOut, this.outputAttachments, false);
        this.nodeValue = nodeValue;

        this.dragger = new Dragger(this);
    }

    protected void useCloseBox(boolean use) {
        this.useCloseBox = use;
        this.refreshTextPos();  // since the text pos depends on if there's a close box
    }

    /**
     * Refreshes the position of the close box to be at the top right of the drag bar.
     */
    private void refreshCloseBoxPos() {
        WorldCoords dragBarPos = this.dragBar.getTransform().getCenter();
        float dragBarHalfWidth = this.dragBar.getWidth() / 2 * this.dragBar.getTransform().getScaleX();

        this.closeBox.getTransform().setPos(
                new WorldCoords(
                        dragBarPos.x + dragBarHalfWidth - this.closeBox.getWidth() * this.closeBox.getTransform().getScaleX() / 2,
                        dragBarPos.y
                )
        );
    }

    /**
     * Refreshes the position of the drag bar to be at the top of the node.
     */
    private void refreshDragBarPos() {
        this.dragBar.getTransform().setPos(
                new WorldCoords(
                        this.rect.getTransform().getCenter().x,
                        this.rect.getTransform().getCenter().y + this.rect.getHeight() / 2 * this.rect.getTransform().getScaleY()
                )
        );
    }

    /**
     * Refreshes the position of the text to be at the center of the drag bar
     */
    private void refreshTextPos() {
        Transform dragTransform = this.dragBar.getTransform();

        float xOffset = 0;
        if (this.useCloseBox) {
            xOffset = -this.closeBox.getWidth() * this.closeBox.getTransform().getScaleX() / 2;
        }

        this.nodeTitle.getTransform().setPos(
                new WorldCoords(
                        dragTransform.getCenter().x + xOffset,
                        dragTransform.getCenter().y
                )
        );
    }

    private void genAttachments(int numAttachments, List<Attachment> attachments, boolean input) {
        for (int i = 0; i < numAttachments; i++) {
            attachments.add(
                    new Attachment(
                            this,
                            new Circle(new WorldCoords(0, 0), 0.015f, 16),
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

            // yOffset by half of the drag bar height so the attachments are centered on the non-drag-bar part
            float yOffset = -this.dragBar.getHeight() / 2 * this.dragBar.getTransform().getScaleY() / attachments.size();
            float multiplier = input ? -this.rect.getTransform().getScaleX() : this.rect.getTransform().getScaleX();

            attachment.circle().getTransform().setPos(
                    new WorldCoords(
                            rectCenter.x + rectWidth / 2 * multiplier,
                            rectCenter.y + rectHeight / 2 * multiplier * i / attachments.size() + yOffset
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
            if (event.action == GLFW.GLFW_RELEASE) {
                this.dragger.deselect();
            }

            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1 || !Shape.collides(this.dragBar, Mouse.getMousePos())) {
                continue;
            }

            if (event.action == GLFW.GLFW_PRESS) {
                this.dragger.select();
            }
        }
    }

    private void updateConnections() {
        for (Attachment attachment : this.inputAttachments) {
            if (attachment.getConnection() != null) {
                attachment.getConnection().update();
            }
        }

        for (Attachment attachment : this.outputAttachments) {
            if (attachment.getConnection() != null) {
                attachment.getConnection().update();
            }
        }
    }

    @Override
    public void drag(WorldCoords offset) {
        this.nodeTitle.getTransform().shift(offset);
        this.rect.getTransform().shift(offset);
        this.dragBar.getTransform().shift(offset);
        this.closeBox.getTransform().shift(offset);

        for (Attachment attachment : this.inputAttachments) {
            attachment.circle().getTransform().shift(offset);
        }

        for (Attachment attachment : this.outputAttachments) {
            attachment.circle().getTransform().shift(offset);
        }

        this.updateConnections();
    }

    public void update(List<MouseEvent> mouseEvents) {
        this.updateSelectionData(mouseEvents);
        this.dragger.update();
    }

    public List<Attachment> getInputAttachments() {
        return new ArrayList<>(this.inputAttachments);
    }

    public List<Attachment> getOutputAttachments() {
        return new ArrayList<>(this.outputAttachments);
    }

    public abstract float compute(float x);

    public void setScale(float scale) {
        // TODO: bug - start dragging the node, then zoom in/out - you can't release the node

        this.rect.getTransform().setScale(scale);
        this.nodeTitle.getTransform().setScale(scale);
        this.dragBar.getTransform().setScale(scale);
        this.closeBox.getTransform().setScale(scale);
        this.inputAttachments.forEach(attachment -> attachment.circle().getTransform().setScale(scale));
        this.outputAttachments.forEach(attachment -> attachment.circle().getTransform().setScale(scale));

        this.updateAttachmentLocations(this.inputAttachments, true);
        this.updateAttachmentLocations(this.outputAttachments, false);

        this.refreshDragBarPos();
        this.refreshCloseBoxPos();
        this.refreshTextPos();

        this.updateConnections();
    }
}
