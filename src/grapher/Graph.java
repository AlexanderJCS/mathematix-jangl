package grapher;

import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.ScrollEvent;
import jangl.shapes.Rect;
import formulas.Formula;
import org.joml.Vector2f;

import java.util.List;

public class Graph {
    private final Rect rect;
    private final ShaderProgram shader;

    public Graph() {
        this.rect = new Rect(new WorldCoords(WorldCoords.getTopRight().x - 1, 1), 1, 1);
        this.shader = new ShaderProgram(
                new TextureShaderVert(),
                new GraphShaderFrag()
        );
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

    public void update(List<ScrollEvent> scrollEvents) {
        WorldCoords mousePos = Mouse.getMousePos();
        WorldCoords delta = new WorldCoords(mousePos);
        delta.sub(this.rect.getTransform().getCenter());

        float xAdjusted = delta.x / this.rect.getWidth() * this.getShader().getXRange().y;
        float yAdjusted = delta.y / this.rect.getHeight() * this.getShader().getYRange().y;

        for (ScrollEvent event : scrollEvents) {
            if (event.yOffset < 0) {
                this.zoom(xAdjusted, yAdjusted, 1 + 0.05f * (float) Math.abs(event.yOffset));
            } else {
                this.zoom(xAdjusted, yAdjusted, 1 - 0.05f * (float) Math.abs(event.yOffset));
            }
        }
    }

    public void draw() {
        this.shader.bind();
        this.rect.draw();
        this.shader.unbind();
    }
}
