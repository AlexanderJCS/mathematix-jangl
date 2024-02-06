#version 410

uniform vec2 xRange;
uniform vec2 yRange;
uniform float radiusUV;

in vec2 texCoords;
out vec4 fragColor;

float RADIUS = radiusUV * (abs(xRange.x) + abs(xRange.y));
float STEP = RADIUS / 10;

struct Node {
    int inputIDs[2];
    int inputSize;

    int nodeType;
    int nodeValue;
};

struct Graph {
    int startAt;
    Node nodes[100];
};

uniform Graph graph;

void computeAddNode(Node node) {
    int sum = 0;

    for (int i = 0; i < node.inputSize; i++) {
        sum += graph.nodes[i].nodeValue;
    }

    node.nodeValue = int(sum);  // TODO: make nodeValue a float
}

/**
 * Computes a node. Modifies the node and stores the output in nodeValue.
 * It assumes that all connections to the node are already computed.
 * @param node The node to compute
 */
void computeNode(Node node, float x) {
    if (node.nodeType == 1) {
        node.nodeValue = int(x);  // TODO: make nodeValue a float, not int
    } else if (node.nodeType == 4) {
        computeAddNode(node);
    }
}

/**
 * Evaluates the graph the y-coordinate for the x-coordinate, x
 * @param x The x-coordinate to find the y coordinate of
 * @return The y-value for the given x value
 */
float eval(float x) {
    // While condition: repeat until the final value is calculated
    while (graph.nodes[graph.startAt].nodeValue == 0) {
        // Start at the starting node
        int index = graph.startAt;

        while (true) {
            Node node = graph.nodes[index];

            // Check if all of the nodes are computed.
            bool allConnectionsComputed = true;
            for (int i = 0; i < node.inputSize; i++) {

                // If a connection is not computed, go to that node and restart the process
                if (graph.nodes[node.inputIDs[i]].nodeValue == 0) {
                    index = node.inputIDs[i];
                    allConnectionsComputed = false;
                    break;
                }
            }

            // If all connections are computed, compute this node and restart at the beginning node
            if (allConnectionsComputed) {
                computeNode(node, x);
                break;
            }
        }
    }

    return graph.nodes[graph.startAt].nodeValue;
}

/**
 * Map a value from one range to another
 *
 * @param value The value to map
 * @param inRange The input range
 * @param outRange The output range
 * @return The mapped value
 */
float mapRange(float value, vec2 inRange, vec2 outRange) {
    // Normalize the input value to the [0, 1] range
    float normalizedValue = (value - inRange.x) / (inRange.y - inRange.x);

    // Use mix to map the normalized value to the output range
    float mappedValue = mix(outRange.x, outRange.y, normalizedValue);

    return mappedValue;
}

void main() {
    vec2 coords = vec2(
        mapRange(texCoords.x, vec2(0, 1), xRange),
        mapRange(texCoords.y, vec2(0, 1), yRange)
    );

    coords.y = -coords.y;  // flip the graph due to how texture coords are

    // Iterate through a series of x-values and see if the y-value is within range.
    for (float i = coords.x - RADIUS; i < coords.x + RADIUS; i += STEP) {
        float funcVal = i * i * i;

        if (abs(coords.y - funcVal) < RADIUS) {
            fragColor = vec4(1.0, 0.0, 0.0, 1.0);
            return;
        }
    }

    // Check for axis lines
    if (abs(coords.x) < RADIUS || abs(coords.y) < RADIUS) {
        fragColor = vec4(1.0, 1.0, 1.0, 1.0);
        return;
    }

    fragColor = vec4(0.0, 0.0, 0.0, 1.0);
}