package grapher;

import formulas.Formula;
import jangl.coords.PixelCoords;
import jangl.graphics.shaders.FragmentShader;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL41;

public class GraphShaderFrag extends FragmentShader {
    private Vector2f xRange;
    private Vector2f yRange;
    private int radiusPixels;
    private Formula formula;

    public GraphShaderFrag() {
        super("resources/shaders/graphShader.frag");

        this.xRange = new Vector2f(-10, 10);
        this.yRange = new Vector2f(-10, 10);
        this.radiusPixels = 2;
    }

    public Vector2f getXRange() {
        return new Vector2f(this.xRange);
    }

    public void setXRange(Vector2f xRange) {
        this.xRange = new Vector2f(xRange);
    }

    public Vector2f getYRange() {
        return new Vector2f(this.yRange);
    }

    public void setYRange(Vector2f yRange) {
        this.yRange = new Vector2f(yRange);
    }

    public int getRadiusPixels() {
        return this.radiusPixels;
    }

    public void setRadiusPixels(int radiusPixels) {
        this.radiusPixels = radiusPixels;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    @Override
    public void setUniforms(int programID) {
        int location = GL41.glGetUniformLocation(programID, "xRange");
        GL41.glUniform2f(location, this.xRange.x, this.xRange.y);

        location = GL41.glGetUniformLocation(programID, "yRange");
        GL41.glUniform2f(location, this.yRange.x, this.yRange.y);

        location = GL41.glGetUniformLocation(programID, "radiusUV");
        GL41.glUniform1f(location, PixelCoords.distToWorldCoords(this.radiusPixels));

        this.formula.uploadUniforms(xRange.x, xRange.y, 1000, "yValues", programID);
    }
}
