package ui.drag;

import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.io.mouse.Mouse;

public class Dragger {
    private final Draggable draggable;
    private boolean selected;
    private WorldCoords lastMousePos;
    private final boolean adjustForZoom;

    public Dragger(Draggable draggable, boolean adjustForZoom) {
        this.draggable = draggable;
        this.selected = false;
        this.adjustForZoom = adjustForZoom;
        this.lastMousePos = this.mousePos();
    }

    private WorldCoords mousePos() {
        WorldCoords mousePos = Mouse.getMousePos();

        if (this.adjustForZoom) {
            mousePos.div(Camera.getZoom(), Camera.getZoom());
        }

        return mousePos;
    }

    public void update() {
        WorldCoords mousePos = this.mousePos();

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
