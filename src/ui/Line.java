package ui;

import jangl.coords.WorldCoords;
import jangl.shapes.Rect;

public class Line {
    private final Rect rect;

    public Line(WorldCoords start, WorldCoords end, float thickness) {
        this.rect = new Rect(
                midpoint(start, end),
                length(start, end),
                thickness
        );

        this.rect.getTransform().rotate(angle(start, end));
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

    }
}
