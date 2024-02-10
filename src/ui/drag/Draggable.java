package ui.drag;

import jangl.coords.WorldCoords;

public interface Draggable {
    void drag(WorldCoords offset);
}
