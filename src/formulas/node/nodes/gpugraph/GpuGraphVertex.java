package formulas.node.nodes.gpugraph;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL41;

import java.nio.IntBuffer;

public class GpuGraphVertex {
    private final int[] inputIDs;
    private final int nodeType;
    private final int nodeValue;

    public GpuGraphVertex(int[] inputIDs, int nodeType, int nodeValue) {
        this.inputIDs = new int[2];
        System.arraycopy(inputIDs, 0, this.inputIDs, 0, inputIDs.length);

        this.nodeType = nodeType;
        this.nodeValue = nodeValue;
    }

    public void uploadUniforms(int programID, String uniformName) {
        int location = GL41.glGetUniformLocation(programID, uniformName + ".inputIDs[0]");
        GL41.glUniform1i(location, this.inputIDs[0]);

        location = GL41.glGetUniformLocation(programID, uniformName + ".inputIDs[1]");
        GL41.glUniform1i(location, this.inputIDs[1]);

        location = GL41.glGetUniformLocation(programID, uniformName + ".inputSize");
        GL41.glUniform1i(location, this.inputIDs.length);

        location = GL41.glGetUniformLocation(programID, uniformName + ".nodeType");
        GL41.glUniform1i(location, this.nodeType);

        location = GL41.glGetUniformLocation(programID, uniformName + ".nodeValue");
        GL41.glUniform1i(location, this.nodeValue);
    }

    public IntBuffer toIntBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(this.inputIDs.length + 3);
        buffer.put(this.inputIDs);
        buffer.put(this.inputIDs.length);

        buffer.put(this.nodeType);
        buffer.put(this.nodeValue);

        buffer.flip();
        return buffer;
    }
}
