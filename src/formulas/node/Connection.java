package formulas.node;

import ui.Line;

public class Connection {
    public static final float THICKNESS = 0.005f;

    private final Attachment out;
    private final Attachment in;
    private final Line line;

    public Connection(Attachment out, Attachment in) {
        this.out = out;
        this.in = in;

        this.line = new Line(
                this.out.circle().getTransform().getCenter(),
                this.in.circle().getTransform().getCenter(),
                THICKNESS
        );
    }

    /**
     * Updates the position of the line based on the position of the attachments
     */
    public void update() {
        this.line.setEnds(
                this.out.circle().getTransform().getCenter(),
                this.in.circle().getTransform().getCenter()
        );
    }

    public Attachment getOut() {
        return out;
    }

    public Attachment getIn() {
        return in;
    }

    public void draw() {
        this.line.draw();
    }
}
