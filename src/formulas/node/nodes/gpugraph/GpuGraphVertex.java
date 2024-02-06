package formulas.node.nodes.gpugraph;

import org.lwjgl.BufferUtils;

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

    public IntBuffer toIntBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(this.inputIDs.length + 2);
        buffer.put(this.inputIDs);
        buffer.put(this.nodeType);
        buffer.put(this.nodeValue);

        buffer.flip();
        return buffer;
    }
}
