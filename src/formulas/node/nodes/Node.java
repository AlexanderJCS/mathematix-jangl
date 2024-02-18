package formulas.node.nodes;

import formulas.node.Attachment;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Justify;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.textures.Texture;
import jangl.io.keyboard.KeyEvent;
import jangl.io.keyboard.Keyboard;
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
    private boolean allowClosing;
    private final Rect dragBar;
    private final Rect rect;
    private final List<Attachment> inputAttachments;
    private final List<Attachment> outputAttachments;
    private final Text nodeTitle;
    private boolean selected;

    private static final Texture NODE_TEXTURE = new Texture("resources/textures/node.png");
    private static final Texture NODE_TEXTURE_SELECTED = new Texture("resources/textures/node_selected.png");

    public Node(WorldCoords pos, int attachmentsIn, int attachmentsOut, String nodeTitle) {
        this.rect = new Rect(pos, 0.2f, 0.2f);
        this.dragBar = new Rect(pos, this.rect.getWidth(), this.rect.getWidth() / 4);
        this.allowClosing = true;
        this.selected = false;

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

        this.dragger = new Dragger(this, true);
    }

    protected void allowClosing(boolean use) {
        this.allowClosing = use;
        this.refreshTextPos();  // since the text pos depends on if there's a close box
    }

    /**
     * Refreshes the position of the text to be at the center of the drag bar
     */
    private void refreshTextPos() {
        Transform dragTransform = this.dragBar.getTransform();

        this.nodeTitle.getTransform().setPos(
                new WorldCoords(
                        dragTransform.getCenter().x,
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

    public boolean shouldClose() {
        return this.allowClosing && this.selected && Keyboard.getKeyDown(GLFW.GLFW_KEY_DELETE);
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void draw() {
        this.rect.draw(this.selected ? NODE_TEXTURE_SELECTED : NODE_TEXTURE);

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

            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1 || !Shape.collides(this.dragBar, Mouse.getMousePosAdjusted())) {
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

        for (Attachment attachment : this.inputAttachments) {
            attachment.circle().getTransform().shift(offset);
        }

        for (Attachment attachment : this.outputAttachments) {
            attachment.circle().getTransform().shift(offset);
        }

        this.updateConnections();
    }

    private void select(List<MouseEvent> mouseEvents) {
        WorldCoords mousePos = Mouse.getMousePosAdjusted();
        // Exit if the mouse collides with any attachments or the drag bar
        if (Shape.collides(this.dragBar, mousePos)) {
            return;
        }

        // TODO: make a getAllAttachments method
        for (Attachment attachment : this.inputAttachments) {
            if (Shape.collides(attachment.circle(), mousePos)) {
                return;
            }
        }

        for (Attachment attachment : this.outputAttachments) {
            if (Shape.collides(attachment.circle(), mousePos)) {
                return;
            }
        }

        // Check for click events
        for (MouseEvent event : mouseEvents) {
            if (event.action == GLFW.GLFW_PRESS && event.button == GLFW.GLFW_MOUSE_BUTTON_1) {
                this.selected = Shape.collides(this.rect, mousePos);
            }
        }
    }

    public void update(List<KeyEvent> keyEvents, List<MouseEvent> mouseEvents) {
        this.updateSelectionData(mouseEvents);
        this.select(mouseEvents);
        this.dragger.update();
    }

    public List<Attachment> getInputAttachments() {
        return new ArrayList<>(this.inputAttachments);
    }

    public List<Attachment> getOutputAttachments() {
        return new ArrayList<>(this.outputAttachments);
    }

    public abstract float compute(float x);

    public Rect getRect() {
        return this.rect;
    }
}
