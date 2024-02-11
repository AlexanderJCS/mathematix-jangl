package ui;

import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.io.keyboard.KeyEvent;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class InputBox {
    private boolean selected;
    private Rect background;
    private final Text text;

    private static final ShaderProgram BG_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    private static final Font FONT = new Font(
            "resources/font/poppins.fnt",
            "resources/font/poppins.png"
    );

    public InputBox(WorldCoords pos, String label, float width, float height) {
        // TODO: add label
        this.background = new Rect(pos, width, height);
        this.selected = false;
        this.text = new TextBuilder(FONT, "", pos).setHeight(height).toText();
    }

    public void setCenter(WorldCoords center) {
        this.background.getTransform().setPos(center);

    }

    /**
     * Clamps the text position to the background
     */
    private void refreshTextPos() {
        WorldCoords center = this.background.getTransform().getCenter();
        float bgHalfScaledWidth = this.background.getWidth() * this.background.getTransform().getScaleX() / 2;
        float bgHalfScaledHeight = this.background.getHeight() * this.background.getTransform().getScaleY() / 2;

        this.text.setCoords(
                new WorldCoords(
                        center.x - bgHalfScaledWidth,
                        center.y + bgHalfScaledHeight
                )
        );
    }

    private void select(List<MouseEvent> mouseEvents) {
        for (MouseEvent event : mouseEvents) {
            if (event.button == GLFW.GLFW_MOUSE_BUTTON_1 && event.action == GLFW.GLFW_PRESS) {
                this.selected = Shape.collides(this.background, Mouse.getMousePos());
            }
        }
    }

    private void addText(List<KeyEvent> keyEvents) {
        for (KeyEvent event : keyEvents) {
            if (event.action != GLFW.GLFW_PRESS) {
                continue;
            }

            if (event.key == GLFW.GLFW_KEY_BACKSPACE) {
                if (this.text.getText().isEmpty()) {
                    continue;
                }

                this.text.setText(this.text.getText().substring(0, this.text.getText().length() - 1));
                continue;
            }

            if (!Character.isDigit(event.key) && event.key != '.') {
                continue;
            }

            this.text.setText(this.text.getText() + event.key);
        }
    }

    public void setScale(float scale) {
        this.text.getTransform().setScale(scale);
        this.background.getTransform().setScale(scale);
        this.refreshTextPos();
    }

    public void drag(WorldCoords delta) {
        this.background.getTransform().shift(delta);
        this.refreshTextPos();
    }

    public float getValue() {
        try {
            return Float.parseFloat(this.text.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void update(List<MouseEvent> mouseEvents, List<KeyEvent> keyEvents) {
        this.select(mouseEvents);

        if (this.selected) {
            this.addText(keyEvents);
        }
    }

    public void draw() {
        this.background.draw(BG_COLOR);
        this.text.draw();
    }
}
