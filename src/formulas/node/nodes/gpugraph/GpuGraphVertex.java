package formulas.node.nodes.gpugraph;

import org.lwjgl.opengl.GL41;

public class GpuGraphVertex {
    private final int[] inputIDs;
    private final int nodeType;
    private final Float nodeValue;

    public GpuGraphVertex(int[] inputIDs, int nodeType, Float nodeValue) {
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
        GL41.glUniform1f(location, this.nodeValue != null ? this.nodeValue : 0.0f);

        location = GL41.glGetUniformLocation(programID, uniformName + ".computed");
        GL41.glUniform1i(location, this.nodeValue != null ? 1 : 0);
    }
}
