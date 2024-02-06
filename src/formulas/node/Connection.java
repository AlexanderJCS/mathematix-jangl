package formulas.node;

public class Connection {
    private final Attachment out;
    private final Attachment in;

    public Connection(Attachment out, Attachment in) {
        this.out = out;
        this.in = in;
    }

    public void draw() {
        // TODO: draw a line between the two points
    }
}
