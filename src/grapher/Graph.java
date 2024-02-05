package grapher;

import jangl.coords.WorldCoords;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderVert;
import jangl.shapes.Rect;

public class Graph {
    private final Rect rect;
    private final ShaderProgram shader;

    public Graph() {
        TEXTURE.useDefaultShader(false);

        this.rect = new Rect(new WorldCoords(1, 0), 1, 1);
        this.rect.getTransform().setPos(WorldCoords.getMiddle());
        this.shader = new ShaderProgram(
                new TextureShaderVert(),
                new GraphShaderFrag()
        );
    }

    public void draw() {
        this.shader.bind();
        this.rect.draw();
        this.shader.unbind();
    }
}
