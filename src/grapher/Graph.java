package grapher;

import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.io.mouse.ScrollEvent;
import jangl.shapes.Rect;
import formulas.Formula;

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

    private void zoomIn(float amount) {
        // TODO: use the amount argument
        GraphShaderFrag shader = this.getShader();
        shader.setXRange(shader.getXRange().mul(1.1f));
        shader.setYRange(shader.getYRange().mul(1.1f));
    }

    private void zoomOut(float amount) {
        GraphShaderFrag shader = this.getShader();
        shader.setXRange(shader.getXRange().mul(0.9f));
        shader.setYRange(shader.getYRange().mul(0.9f));
    }

    public void update(List<ScrollEvent> scrollEvents) {
        for (ScrollEvent event : scrollEvents) {
            if (event.yOffset > 0) {
                this.zoomIn((float) event.yOffset);
            } else {
                this.zoomOut((float) event.yOffset);
            }
        }
    }

    public void draw() {
        this.shader.bind();
        this.rect.draw();
        this.shader.unbind();
    }
}
