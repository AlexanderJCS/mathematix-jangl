package grapher;

import formulas.Formula;
import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.VertexShader;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.io.mouse.ScrollEvent;
import jangl.shapes.Rect;
import jangl.shapes.Shape;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import ui.drag.Draggable;
import ui.drag.Dragger;

import java.util.List;

public class Graph implements Draggable {
    private final Rect rect;
    private final ShaderProgram shader;
    private final Dragger dragger;

    public Graph() {
        this.rect = new Rect(new WorldCoords(WorldCoords.getTopRight().x - 1, 1), 1, 1);

        VertexShader vert = new TextureShaderVert();
        vert.setObeyCamera(false);
        this.shader = new ShaderProgram(
                vert,
                new GraphShaderFrag()
        );

        this.dragger = new Dragger(this, false);
    }

    private GraphShaderFrag getShader() {
        return (GraphShaderFrag) this.shader.getFragmentShader();
    }

    public void setFormula(Formula formula) {
        this.getShader().setFormula(formula);
    }

    private void zoom(float x, float y, float amount) {
        GraphShaderFrag shader = this.getShader();

        Vector2f xRange = shader.getXRange();
        Vector2f yRange = shader.getYRange();

        float zoomedXMin = x - (x - xRange.x) * amount;
        float zoomedXMax = x + (xRange.y - x) * amount;

        float zoomedYMin = y - (y - yRange.x) * amount;
        float zoomedYMax = y + (yRange.y - y) * amount;

        shader.setXRange(new Vector2f(zoomedXMin, zoomedXMax));
        shader.setYRange(new Vector2f(zoomedYMin, zoomedYMax));
    }

    private void zoomAroundMouse(float amount) {
        WorldCoords delta = Mouse.getMousePos();
        delta.sub(this.rect.getTransform().getCenter());
        delta.add(
                this.rect.getWidth() * this.rect.getTransform().getScaleX() / 2,
                this.rect.getHeight() * this.rect.getTransform().getScaleY() / 2
        );

        Vector2f xRange = this.getShader().getXRange();
        Vector2f yRange = this.getShader().getYRange();

        float xAdjusted = delta.x / this.rect.getWidth() * (xRange.y - xRange.x) + xRange.x;
        float yAdjusted = delta.y / this.rect.getHeight() * (yRange.y - yRange.x) + yRange.x;

        this.zoom(xAdjusted, yAdjusted, amount);
    }

    public void update(List<MouseEvent> mouseEvents, List<ScrollEvent> scrollEvents) {
        this.dragger.update();

        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1) {
                continue;
            }

            if (event.action == GLFW.GLFW_PRESS) {
                if (Shape.collides(this.rect, Mouse.getMousePos())) {
                    this.dragger.select();
                }
            } else {
                this.dragger.deselect();
            }
        }

        // Do not zoom if the mouse is not over the graph
        if (!Shape.collides(this.rect, Mouse.getMousePos())) {
            return;
        }

        for (ScrollEvent event : scrollEvents) {
            if (event.yOffset < 0) {
                this.zoomAroundMouse(1 + 0.05f * (float) Math.abs(event.yOffset));
            } else {
                this.zoomAroundMouse(1 - 0.05f * (float) Math.abs(event.yOffset));
            }
        }
    }

    public void draw() {
        this.shader.bind();
        this.rect.draw();
        this.shader.unbind();
    }

    @Override
    public void drag(WorldCoords offset) {
        Vector2f xRange = this.getShader().getXRange();
        Vector2f yRange = this.getShader().getYRange();

        float multiplier = Math.abs(xRange.y - xRange.x) / this.rect.getWidth();
        offset.mul(multiplier, multiplier);

        this.getShader().setXRange(
                xRange.add(-offset.x, -offset.x)
        );

        this.getShader().setYRange(
                yRange.add(-offset.y, -offset.y)
        );
    }
}
