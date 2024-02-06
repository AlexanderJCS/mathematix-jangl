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
    private final List<Circle> inputAttachments;
    private final List<Circle> outputAttachments;
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

        this.genAttachments(attachmentsIn, this.inputAttachments);
        this.genAttachments(attachmentsOut, this.outputAttachments);
    }

    private void genAttachments(int numAttachments, List<Circle> list) {
        for (int i = 0; i < numAttachments; i++) {
            list.add(
                    new Circle(new WorldCoords(0, 0), 0.02f, 16)
            );
        }

        this.updateAttachmentLocation();
    }

    private void updateAttachmentLocation() {
        WorldCoords rectCenter = this.rect.getTransform().getCenter();
        float rectWidth = this.rect.getWidth();
        float rectHeight = this.rect.getHeight();

        for (int i = 0; i < this.inputAttachments.size(); i++) {
            Circle attachment = this.inputAttachments.get(i);

            attachment.getTransform().setPos(
                    new WorldCoords(
                            rectCenter.x - rectWidth / 2,
                            rectCenter.y - rectHeight / 2 * i / this.inputAttachments.size()
                    )
            );
        }

        for (int i = 0; i < this.outputAttachments.size(); i++) {
            Circle attachment = this.outputAttachments.get(i);

            attachment.getTransform().setPos(
                    new WorldCoords(
                            rectCenter.x + rectWidth / 2,
                            rectCenter.y + rectHeight / 2 * i / this.outputAttachments.size()
                    )
            );
        }
    }

    public void draw() {
        this.rect.draw(this.colorShader);
        this.nodeTitle.draw();

        for (Circle attachment : this.inputAttachments) {
            attachment.draw();
        }

        for (Circle attachment : this.outputAttachments) {
            attachment.draw();
        }
    }
}
