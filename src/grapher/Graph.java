package grapher;

import formulas.Formula;
import jangl.coords.WorldCoords;
import jangl.graphics.Camera;
import jangl.graphics.shaders.ShaderProgram;
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
        this.shader = new ShaderProgram(
                new TextureShaderVert(),
                new GraphShaderFrag()
        );
        this.dragger = new Dragger(this, false);
    }

    private GraphShaderFrag getShader() {
        return (GraphShaderFrag) this.shader.getFragmentShader();
    }

    public void setFormula(Formula gpuGraph) {
        this.getShader().setFormula(gpuGraph);
    }

    private void zoom(float x, float y, float amount) {
        GraphShaderFrag shader = this.getShader();

        Vector2f xRange = shader.getXRange();
        Vector2f yRange = shader.getYRange();

        // Get the current center
        float xRangeCenter = (xRange.x + xRange.y) / 2;
        float yRangeCenter = (yRange.x + yRange.y) / 2;

        // Adjust the new range to be centered around the given coordinate
        xRange.sub(new Vector2f(x + xRangeCenter));
        yRange.sub(new Vector2f(y + yRangeCenter));

        // Adjust the new range to be scaled by the given amount
        xRange.mul(amount);
        yRange.mul(amount);

        // Adjust the new range to be centered around the given coordinate
        xRange.add(new Vector2f(x + xRangeCenter));
        yRange.add(new Vector2f(y + yRangeCenter));

        shader.setXRange(xRange);
        shader.setYRange(yRange);
    }

    /**
     * Clamps the graph to the right side of the screen, ensuring that it does not shift when the window is resized
     * or camera is zoomed.
     */
    private void clampToRight() {
        float widthHeight = this.rect.getWidth() / Camera.getZoom();
        this.rect.getTransform().setWidth(widthHeight, this.rect.getWidth());
        this.rect.getTransform().setHeight(widthHeight, this.rect.getHeight());

        this.rect.getTransform().setPos(
                WorldCoords.getTopRight().x / 2 + 0.5f * WorldCoords.getTopRight().x / Camera.getZoom() - widthHeight / 2,
                this.rect.getTransform().getCenter().y
        );
    }

    private void zoomAroundMouse(float amount) {
        WorldCoords mousePos = Mouse.getMousePosAdjusted();
        WorldCoords delta = new WorldCoords(mousePos);
        delta.sub(this.rect.getTransform().getCenter());

        float xAdjusted = delta.x / this.rect.getWidth() * this.getShader().getXRange().y;
        float yAdjusted = delta.y / this.rect.getHeight() * this.getShader().getYRange().y;

        this.zoom(xAdjusted, yAdjusted, amount);
    }

    public void update(List<MouseEvent> mouseEvents, List<ScrollEvent> scrollEvents) {
        this.clampToRight();
        this.dragger.update();

        for (MouseEvent event : mouseEvents) {
            if (event.button != GLFW.GLFW_MOUSE_BUTTON_1) {
                continue;
            }

            if (event.action == GLFW.GLFW_PRESS) {
                if (Shape.collides(this.rect, Mouse.getMousePosAdjusted())) {
                    this.dragger.select();
                }
            } else {
                this.dragger.deselect();
            }
        }

        // Do not zoom if the mouse is not over the graph
        if (!Shape.collides(this.rect, Mouse.getMousePosAdjusted())) {
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
