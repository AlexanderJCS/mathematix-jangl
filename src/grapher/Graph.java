package grapher;

import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.shapes.Rect;
import formulas.Formula;

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

    public void setFormula(Formula gpuGraph) {
        ((GraphShaderFrag) this.shader.getFragmentShader()).setFormula(gpuGraph);
    }

    public void draw() {
        this.shader.bind();
        this.rect.draw();
        this.shader.unbind();
    }
}
