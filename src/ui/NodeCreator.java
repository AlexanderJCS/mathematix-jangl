package ui;

import formulas.node.nodes.Node;
import jangl.color.ColorFactory;
import jangl.coords.WorldCoords;
import jangl.graphics.font.Font;
import jangl.graphics.font.Text;
import jangl.graphics.font.TextBuilder;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
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
    private final List<Node> nodes;
    private boolean visible;

    private static final float HEIGHT_PER_ITEM = 0.05f;
    private static final ShaderProgram BG_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.4f, 0.4f, 0.4f, 1.0f))
    );

    private static final ShaderProgram SELECT_COLOR = new ShaderProgram(
            new ColorShader(ColorFactory.fromNorm(0.2f, 0.2f, 0.2f, 1.0f))
    );

    public NodeCreator(LinkedHashMap<String, Class<? extends Node>> selectionItems, List<Node> nodes) {
        this.visible = false;
        this.nodes = nodes;

        this.selectionItems = selectionItems;
        this.rect = new Rect(new WorldCoords(0, 0), 0.2f, HEIGHT_PER_ITEM * selectionItems.size());

        this.itemRects = new ArrayList<>();
        for (int i = 0; i < selectionItems.size(); i++) {
            this.itemRects.add(
                    new Rect(
                            new WorldCoords(0, -i * HEIGHT_PER_ITEM),
                            this.rect.getWidth(),
                            HEIGHT_PER_ITEM
                    )
            );
        }

        this.itemTexts = new ArrayList<>();

        for (int i = 0; i < selectionItems.size(); i++) {
            this.itemTexts.add(
                    new TextBuilder(
                            new Font("resources/font/arial.fnt", "resources/font/arial.png"),
                            selectionItems.keySet().toArray(new String[0])[i],
                            new WorldCoords(0, -i * HEIGHT_PER_ITEM)
                    ).toText()
            );
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setPos(WorldCoords pos) {
        WorldCoords delta = this.rect.getTransform().getCenter();
        delta.sub(pos);
        delta.mul(-1, -1);

        delta.sub(-1 * this.rect.getWidth() / 2, this.rect.getHeight() / 2);

        this.rect.getTransform().shift(delta);

        for (Rect itemRect : this.itemRects) {
            itemRect.getTransform().shift(delta);
        }

        for (Text itemText : this.itemTexts) {
            itemText.getTransform().shift(delta);
        }
    }

    public void update(List<MouseEvent> events) {
        for (MouseEvent event : events) {
            if (event.action != GLFW.GLFW_PRESS) {
                continue;
            }

            if (event.button == GLFW.GLFW_MOUSE_BUTTON_1) {
                if (this.visible) {
                    for (int i = 0; i < this.itemRects.size(); i++) {
                        if (Shape.collides(this.itemRects.get(i), Mouse.getMousePos())) {
                            Node node = this.createNode(
                                    this.itemTexts.get(i).getText(),
                                    Mouse.getMousePos()
                            );

                            if (node != null) {
                                this.nodes.add(node);
                            }
                        }
                    }
                }

                this.setVisible(false);
            }

            else if (event.button == GLFW.GLFW_MOUSE_BUTTON_2) {
                this.setVisible(true);
                this.setPos(Mouse.getMousePos());
            }
        }
    }

    public void draw() {
        if (!this.visible) {
            return;
        }

        this.rect.draw(BG_COLOR);

        for (Rect itemRect : this.itemRects) {
            if (Shape.collides(itemRect, Mouse.getMousePos())) {
                itemRect.draw(SELECT_COLOR);
            }
        }

        for (Text itemText : this.itemTexts) {
            itemText.draw();
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
            return null;
        }
    }
}
