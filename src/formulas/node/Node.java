package formulas.node;

import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.shapes.Circle;
import jangl.shapes.Rect;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final Rect rect;
    private final List<Attachment> inputAttachments;
    private final List<Attachment> outputAttachments;
    private final Text nodeTitle;

    private final ShaderProgram colorShader = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    public Node(WorldCoords pos, int attachmentsIn, int attachmentsOut, String nodeTitle) {
        this.rect = new Rect(pos, 0.2f, 0.4f);

        this.nodeTitle = new TextBuilder(
                new Font("resources/font/arial.fnt", "resources/font/arial.png"),
                nodeTitle
        )
                .setCoords(new WorldCoords(pos.x, pos.y))
                .setWrapWidth(this.rect.getWidth())
                .setYHeight(0.05f)
                .toText();

        // Both lists must be initialized before calling genAttachments
        this.inputAttachments = new ArrayList<>();
        this.outputAttachments = new ArrayList<>();

        this.genAttachments(attachmentsIn, this.inputAttachments, true);
        this.genAttachments(attachmentsOut, this.outputAttachments, false);
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
                            rectCenter.y + rectHeight / 2 * multiplier * i / this.inputAttachments.size()
                    )
            );
        }
    }

    public void draw() {
        this.rect.draw(this.colorShader);
        this.nodeTitle.draw();

        for (Attachment attachment : this.inputAttachments) {
            attachment.draw();
        }

        for (Attachment attachment : this.outputAttachments) {
            attachment.draw();
        }
    }

    public float getOutput() {
        // TODO: make this abstract
        return 0;
    }
}
