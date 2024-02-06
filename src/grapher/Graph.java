package grapher;

import formulas.node.nodes.gpugraph.GpuGraph;
import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.shapes.Rect;

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

    public void setGpuGraph(GpuGraph graph) {
        ((GraphShaderFrag) this.shader.getFragmentShader()).setGpuGraph(graph);
    }

    public void draw() {
        this.shader.bind();
        this.rect.draw();
        this.shader.unbind();
    }
}
