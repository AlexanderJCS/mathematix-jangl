package grapher;

import jangl.graphics.shaders.FragmentShader;
import org.lwjgl.opengl.GL41;

public class GraphShaderFrag extends FragmentShader {
    public GraphShaderFrag() {
        super("resources/graphShader.frag");
    }

    @Override
    public void setUniforms(int programID) {
        int location = GL41.glGetUniformLocation(programID, "xRange");
        GL41.glUniform2f(location, -10, 10);

        location = GL41.glGetUniformLocation(programID, "yRange");
        GL41.glUniform2f(location, -10, 10);
    }
}
