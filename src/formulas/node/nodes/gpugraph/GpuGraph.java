package formulas.node.nodes.gpugraph;

import org.lwjgl.opengl.GL41;

/**
 * Represents the graph of nodes that will be computed by the GPU. This GpuGraph contains several GpuGraphVertex objects
 * which represent the nodes. The GpuGraph is responsible for uploading the uniforms of the graph to the GPU.
 */
public class GpuGraph {
    private final GpuGraphVertex[] vertices;
    private final int startAt;

    public GpuGraph(int startAt) {
        this.vertices = new GpuGraphVertex[100];
        this.startAt = startAt;
    }

    public void addVertex(GpuGraphVertex vertex, int index) {
        this.vertices[index] = vertex;
    }

    public void uploadUniforms(int programID, String uniformName) {
        int location = GL41.glGetUniformLocation(programID, uniformName + ".startAt");
        GL41.glUniform1i(location, this.startAt);

        for (int i = 0; i < 100; i++) {
            if (this.vertices[i] != null) {
                this.vertices[i].uploadUniforms(programID, uniformName + ".nodes[" + i + "]");
            }
        }
    }
}
