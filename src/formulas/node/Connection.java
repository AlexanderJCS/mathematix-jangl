package formulas.node;

import ui.Line;

public class Connection {
    private final Attachment out;
    private final Attachment in;
    private final Line line;

    public Connection(Attachment out, Attachment in) {
        this.out = out;
        this.in = in;

        this.line = new Line(
                this.out.circle().getTransform().getCenter(),
                this.in.circle().getTransform().getCenter(),
                0.05f
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
