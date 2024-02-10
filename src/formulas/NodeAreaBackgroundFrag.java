package formulas;

import jangl.graphics.shaders.FragmentShader;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL41;

public class NodeAreaBackgroundFrag extends FragmentShader {
    private Vector2f offset;
    private float dotRadius;

    public NodeAreaBackgroundFrag() {
        super("resources/shaders/nodeAreaBackground.frag");
        this.offset = new Vector2f().zero();
        this.dotRadius = 0.01f;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    public float getDotRadius() {
        return dotRadius;
    }

    public void setDotRadius(float dotRadius) {
        this.dotRadius = dotRadius;
    }

    @Override
    public void setUniforms(int programID) {
        int location = GL41.glGetUniformLocation(programID, "offset");
        GL41.glUniform2f(location, this.offset.x, this.offset.y);

        location = GL41.glGetUniformLocation(programID, "dotRadius");
        GL41.glUniform1f(location, this.dotRadius);
    }
}
