package formulas.node.nodes;

import formulas.node.Attachment;
import formulas.node.Connection;
import formulas.node.nodes.gpugraph.GpuGraph;
import formulas.node.nodes.gpugraph.GpuGraphVertex;
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

public abstract class Node {
    private final Rect rect;
    private final List<Attachment> inputAttachments;
    private final List<Attachment> outputAttachments;
    private final Text nodeTitle;
    public final int nodeType;
    public final int nodeValue;
    public final int uniqueID;
    private static int uniqueIDCounter = 0;

    private final ShaderProgram colorShader = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    public Node(WorldCoords pos, int attachmentsIn, int attachmentsOut, String nodeTitle, int nodeType, int nodeValue) {
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

        this.nodeType = nodeType;
        this.nodeValue = nodeValue;

        this.uniqueID = uniqueIDCounter;
        uniqueIDCounter++;
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

    public List<Attachment> getInputAttachments() {
        return new ArrayList<>(this.inputAttachments);
    }

    public List<Attachment> getOutputAttachments() {
        return new ArrayList<>(this.outputAttachments);
    }

    private void addGraphVertex(GpuGraph graph) {
        int[] inputIDs = new int[this.inputAttachments.size()];
        for (int i = 0; i < this.inputAttachments.size(); i++) {
            inputIDs[i] = this.inputAttachments.get(i).node().uniqueID;
        }

        graph.addVertex(new GpuGraphVertex(inputIDs, this.nodeType, this.nodeValue), this.uniqueID);

        for (Attachment attachment : this.inputAttachments) {
            Connection connection = attachment.getConnection();

            if (connection == null || connection.getIn().node().uniqueID == this.uniqueID) {
                continue;
            }

            connection.getIn().node().addGraphVertex(graph);
        }
    }

    public GpuGraph getGpuGraph() {
        GpuGraph graph = new GpuGraph(this.uniqueID);
        this.addGraphVertex(graph);

        return graph;
    }

    public static int getUniqueIDCounter() {
        return uniqueIDCounter;
    }

    public abstract float getOutput();
}