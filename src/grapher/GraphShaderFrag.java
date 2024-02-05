package grapher;

import jangl.graphics.shaders.FragmentShader;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL41;

public class GraphShaderFrag extends FragmentShader {
    private Vector2f xRange;
    private Vector2f yRange;
    private float radiusUV;

    public GraphShaderFrag() {
        super("resources/graphShader.frag");

        this.xRange = new Vector2f(-10, 10);
        this.yRange = new Vector2f(-10, 10);
        this.radiusUV = 0.0025f;
    }

    public Vector2f getXRange() {
        return this.xRange;
    }

    public void setXRange(Vector2f xRange) {
        this.xRange = xRange;
    }

    public Vector2f getYRange() {
        return this.yRange;
    }

    public void setYRange(Vector2f yRange) {
        this.yRange = yRange;
    }

    public float getRadiusUV() {
        return radiusUV;
    }

    public void setRadiusUV(float radiusUV) {
        this.radiusUV = radiusUV;
    }

    @Override
    public void setUniforms(int programID) {
        int location = GL41.glGetUniformLocation(programID, "xRange");
        GL41.glUniform2f(location, this.xRange.x, this.xRange.y);

        location = GL41.glGetUniformLocation(programID, "yRange");
        GL41.glUniform2f(location, this.yRange.x, this.yRange.y);

        location = GL41.glGetUniformLocation(programID, "radiusUV");
        GL41.glUniform1f(location, this.radiusUV);
    }
}
