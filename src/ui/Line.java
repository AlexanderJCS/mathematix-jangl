package ui;

import jangl.coords.WorldCoords;
import jangl.shapes.Rect;

public class Line {
    private final Rect rect;
    private WorldCoords start;
    private WorldCoords end;

    public Line(WorldCoords start, WorldCoords end, float thickness) {
        this.rect = new Rect(
                new WorldCoords(0, 0),
                1,
                thickness
        );

        this.start = start;
        this.end = end;

        this.refresh();
    }

    private void refresh() {
        this.rect.getTransform().setWidth(length(this.start, this.end), 1);
        this.rect.getTransform().setPos(midpoint(this.start, this.end));
        this.rect.getTransform().setRotation(angle(this.start, this.end));
    }

    public void setStart(WorldCoords start) {
        this.start = new WorldCoords(start);
        this.refresh();
    }

    public void setEnd(WorldCoords end) {
        this.end = new WorldCoords(end);
        this.refresh();
    }

    public void setEnds(WorldCoords start, WorldCoords end) {
        this.setStart(start);
        this.setEnd(end);
        this.refresh();
    }

    public WorldCoords getStart() {
        return new WorldCoords(this.start);
    }

    public WorldCoords getEnd() {
        return new WorldCoords(this.end);
    }

    private static float angle(WorldCoords start, WorldCoords end) {
        return (float) Math.atan2(end.y - start.y, end.x - start.x);
    }

    private static float length(WorldCoords start, WorldCoords end) {
        return (float) Math.sqrt(
                Math.pow(end.x - start.x, 2) +
                Math.pow(end.y - start.y, 2)
        );
    }

    private static WorldCoords midpoint(WorldCoords start, WorldCoords end) {
        return new WorldCoords(
                (start.x + end.x) / 2,
                (start.y + end.y) / 2
        );
    }

    public void draw() {
        this.rect.draw();
    }
}
