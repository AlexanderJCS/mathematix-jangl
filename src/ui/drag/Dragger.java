package ui.drag;

import jangl.coords.WorldCoords;
import jangl.io.mouse.Mouse;

public class Dragger {
    private final Draggable draggable;
    private boolean selected;
    private WorldCoords lastMousePos;

    public Dragger(Draggable draggable) {
        this.draggable = draggable;
        this.selected = false;
        this.lastMousePos = Mouse.getMousePos();
    }

    public void update() {
        WorldCoords mousePos = Mouse.getMousePos();

        if (this.selected) {
            WorldCoords offset = new WorldCoords(
                    mousePos.x - this.lastMousePos.x,
                    mousePos.y - this.lastMousePos.y
            );

            this.draggable.drag(offset);
        }

        this.lastMousePos = mousePos;
    }

    public void select() {
        this.selected = true;
    }

    public void deselect() {
        this.selected = false;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
