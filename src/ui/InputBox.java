package ui;

import jangl.coords.WorldCoords;
import jangl.shapes.Rect;

public class InputBox {
    private boolean selected;
    private Rect background;
    


    public InputBox(WorldCoords pos, String label, float width, float height) {
        this.selected = false;
    }
}
