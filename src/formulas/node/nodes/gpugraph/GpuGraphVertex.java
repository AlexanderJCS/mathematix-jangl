package formulas.node.nodes.gpugraph;

import org.lwjgl.opengl.GL41;

/**
 * Represents a single vertex in the GpuGraph. This vertex contains the input IDs, the node type, and the node value.
 */
public class GpuGraphVertex {
    private final int[] inputIDs;
    private final int inputIDLength;
    private final int nodeType;
    public final Float nodeValue;

    public GpuGraphVertex(int[] inputIDs, int nodeType, Float nodeValue) {
        this.inputIDLength = inputIDs.length;
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
        GL41.glUniform1i(location, this.inputIDLength);

        location = GL41.glGetUniformLocation(programID, uniformName + ".nodeType");
        GL41.glUniform1i(location, this.nodeType);

        location = GL41.glGetUniformLocation(programID, uniformName + ".nodeValue");
        GL41.glUniform1f(location, this.nodeValue != null ? this.nodeValue : 0.0f);

        location = GL41.glGetUniformLocation(programID, uniformName + ".computed");
        GL41.glUniform1i(location, this.nodeValue == null ? 0 : 1);
    }
}
