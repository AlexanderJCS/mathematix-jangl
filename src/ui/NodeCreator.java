package ui;

import formulas.Formula;
import formulas.node.nodes.Node;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.graphics.textures.Texture;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class NodeCreator {
    private final Map<String, Class<? extends Node>> selectionItems;
    private final Rect rect;

    // TODO: refactor: make an object called TitledRect that contains a text and a rect
    private final List<Rect> itemRects;
    private final List<Text> itemTexts;
    private final Formula formula;
    private boolean visible;


    private static final Texture END_TEXTURE = new Texture(
            "resources/textures/node.png"
    );

    private static final Texture MIDDLE_TEXTURE = new Texture(
            "resources/textures/node_selected.png"
    );

    private static final float HEIGHT_PER_ITEM = 0.05f;
    private static final ShaderProgram BG_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    private static final ShaderProgram SELECT_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.2f, 0.2f, 0.2f, 1.0f))
    );

    public NodeCreator(LinkedHashMap<String, Class<? extends Node>> selectionItems, Formula formula) {
        this.visible = false;
        this.formula = formula;

        this.selectionItems = selectionItems;
        this.rect = new Rect(new WorldCoords(0, 0), 0.2f, HEIGHT_PER_ITEM * (selectionItems.size() + 2));

        this.itemRects = new ArrayList<>();
        for (int i = 0; i < selectionItems.size() + 2; i++) {
            this.itemRects.add(
                    new Rect(
                            new WorldCoords(0, -i * HEIGHT_PER_ITEM),
                            this.rect.getWidth(),
                            HEIGHT_PER_ITEM
                    )
            );
        }

        this.itemTexts = new ArrayList<>();
        this.itemTexts.add(null);  // add empty text for the top to be

        for (int i = 0; i < selectionItems.size(); i++) {
            this.itemTexts.add(
                    new TextBuilder(
                            new Font("resources/font/poppins.fnt", "resources/font/poppins.png"),
                            selectionItems.keySet().toArray(new String[0])[i],
                            new WorldCoords(0, -i * HEIGHT_PER_ITEM - HEIGHT_PER_ITEM)
                    ).toText()
            );
        }
    }

    private float scaledRectWidth() {
        return this.rect.getWidth() * this.rect.getTransform().getScaleX();
    }

    private float scaledRectHeight() {
        return this.rect.getHeight() * this.rect.getTransform().getScaleY();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setPos(WorldCoords pos) {
        WorldCoords delta = this.rect.getTransform().getCenter();
        delta.sub(pos);
        delta.mul(-1, -1);

        delta.sub(-1 * this.scaledRectWidth() / 2, this.scaledRectHeight() / 2);

        this.rect.getTransform().shift(delta);

        for (Rect itemRect : this.itemRects) {
            itemRect.getTransform().shift(delta);
        }

        for (Text itemText : this.itemTexts) {
            if (itemText != null) {
                    itemText.getTransform().shift(delta);
            }
        }
        this.resetTextPos();  // center-justify text
    }

    public void update(List<MouseEvent> events) {
        for (MouseEvent event : events) {
            if (event.action != GLFW.GLFW_PRESS) {
                continue;
            }

            if (event.button == GLFW.GLFW_MOUSE_BUTTON_1) {
                // TODO: refactor by negating the condition, among other things
                if (this.visible) {
                    for (int i = 0; i < this.itemTexts.size(); i++) {
                        if (this.itemTexts.get(i) == null) {
                            continue;
                        }

                        if (Shape.collides(this.itemRects.get(i), Mouse.getMousePosAdjusted())) {
                            Node node = this.createNode(
                                    this.itemTexts.get(i).getText(),
                                    Mouse.getMousePosAdjusted()
                            );

                            if (node != null) {
                                this.formula.addNode(node);
                            }
                        }
                    }
                }

                this.setVisible(false);
            }

            else if (event.button == GLFW.GLFW_MOUSE_BUTTON_2) {
                this.setVisible(true);
                this.setPos(Mouse.getMousePosAdjusted());
            }
        }
    }

    public void draw() {
        if (!this.visible) {
            return;
        }

        this.rect.draw(MIDDLE_TEXTURE);

        for (int i = 0; i < this.itemRects.size(); i++) {
            Rect itemRect = this.itemRects.get(i);

            if (i == 0 || i == this.itemRects.size() - 1) {
                itemRect.draw(END_TEXTURE);
            }

            if (Shape.collides(itemRect, Mouse.getMousePosAdjusted())) {
                itemRect.draw(SELECT_COLOR);
            }
        }

        for (Text itemText : this.itemTexts) {
            if (itemText != null) {
                itemText.draw();
            }
        }
    }

    private void resetTextPos() {
        WorldCoords originalPos = this.rect.getTransform().getCenter();
        originalPos.y += this.scaledRectHeight() / 2;

        for (int i = 0; i < this.itemTexts.size(); i++) {
            if (this.itemTexts.get(i) == null) {
                continue;
            }

            this.itemTexts.get(i).getTransform().setPos(
                    originalPos.x,
                    originalPos.y - i * HEIGHT_PER_ITEM * this.rect.getTransform().getScaleY() - HEIGHT_PER_ITEM * this.rect.getTransform().getScaleY() / 2
            );
        }
    }

    /**
     * Create a node at the given position
     * @param name the name of the node
     * @param pos the position of the node
     * @return the created node, or null if the node could not be created
     */
    private Node createNode(String name, WorldCoords pos) {
        try {
            Class<? extends Node> nodeClass = this.selectionItems.get(name);
            return nodeClass.getDeclaredConstructor(WorldCoords.class).newInstance(pos);
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
