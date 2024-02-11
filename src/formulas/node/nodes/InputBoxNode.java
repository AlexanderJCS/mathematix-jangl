package formulas.node.nodes;

import jangl.coords.WorldCoords;
import jangl.io.keyboard.KeyEvent;
import jangl.io.mouse.MouseEvent;
import ui.InputBox;

import java.util.List;

public abstract class InputBoxNode extends Node {
    private final InputBox inputBox;

    public InputBoxNode(WorldCoords pos, int attachmentsIn, int attachmentsOut, String name) {
        super(pos, attachmentsIn, attachmentsOut, name);

        this.inputBox = new InputBox(
                new WorldCoords(0, 0),
                0.1f, 0.05f
        );

        this.inputBox.setCenter(this.getRect().getTransform().getCenter());
    }

    public float getInputValue() {
        return this.inputBox.getValue();
    }

    public InputBox getInputBox() {
        return this.inputBox;
    }

    @Override
    public void update(List<KeyEvent> keyEvents, List<MouseEvent> mouseEvents) {
        super.update(keyEvents, mouseEvents);
        this.inputBox.update(mouseEvents, keyEvents);
    }


    @Override
    public void draw() {
        super.draw();
        this.inputBox.draw();
    }

    @Override
    public void drag(WorldCoords delta) {
        super.drag(delta);
        this.inputBox.drag(delta);
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        this.inputBox.setScale(scale);
    }
}
