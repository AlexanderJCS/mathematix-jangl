package formulas;

import jangl.coords.WorldCoords;
import jangl.graphics.shaders.FragmentShader;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL41;

public class BackgroundShader extends FragmentShader {
    private Vector2f offset;
    private WorldCoords widthHeight;
    private float dotRadius;
    private float dotSeparation;

    public BackgroundShader() {
        super("shaders/backgroundShader.frag");
        this.offset = new Vector2f().zero();
        this.dotRadius = 0.1f;
        this.dotSeparation = 0.03f;
        this.widthHeight = WorldCoords.getTopRight();
    }

    public void setWidthHeight(WorldCoords widthHeight) {
        this.widthHeight = new WorldCoords(widthHeight);
    }

    public WorldCoords getWidthHeight() {
        return new WorldCoords(this.widthHeight);
    }

    public Vector2f getOffset() {
        return new Vector2f(this.offset);
    }

    public void setOffset(Vector2f offset) {
        this.offset = new Vector2f(offset);
    }

    public float getDotRadius() {
        return this.dotRadius;
    }

    public void setDotRadius(float dotRadius) {
        this.dotRadius = dotRadius;
    }

    public float getDotSeparation() {
        return this.dotSeparation;
    }

    public void setDotSeparation(float dotSeparation) {
        this.dotSeparation = dotSeparation;
    }

    @Override
    public void setUniforms(int programID) {
        int location = GL41.glGetUniformLocation(programID, "offset");
        GL41.glUniform2f(location, this.offset.x, this.offset.y);

        location = GL41.glGetUniformLocation(programID, "dotRadius");
        GL41.glUniform1f(location, this.dotRadius);

        location = GL41.glGetUniformLocation(programID, "widthHeight");
        GL41.glUniform2f(location, this.widthHeight.x, this.widthHeight.y);

        location = GL41.glGetUniformLocation(programID, "dotSeparation");
        GL41.glUniform1f(location, this.dotSeparation);
    }
}
