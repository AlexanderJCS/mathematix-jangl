package formulas.node.nodes.gpugraph;

import java.nio.IntBuffer;

public class GpuGraph {
    private GpuGraphVertex[] vertices;
    private int startAt;

    public GpuGraph(int startAt) {
        this.vertices = new GpuGraphVertex[100];
        this.startAt = startAt;
    }

    public void addVertex(GpuGraphVertex vertex, int index) {
        this.vertices[index] = vertex;
    }

    public int[] toIntArray() {
        // TODO: clean this up
        IntBuffer buffer = IntBuffer.allocate(this.vertices.length * 5 + 1);

        buffer.put(this.startAt);

        for (int i = 0; i < 100; i++) {
            if (this.vertices[i] != null) {
                System.out.println(this.vertices[i].toIntBuffer());
                buffer.put(this.vertices[i].toIntBuffer());
            } else {
                buffer.put(new int[]{0, 0, 0, 0, 0});
            }
        }

        buffer.flip();

        int[] arr = new int[buffer.capacity()];
        buffer.get(arr);

        return arr;
    }
}
