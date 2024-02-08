#version 410

uniform vec2 xRange;
uniform vec2 yRange;
uniform float radiusUV;

in vec2 texCoords;
out vec4 fragColor;

float RADIUS = radiusUV * (abs(xRange.x) + abs(xRange.y));

struct Node {
    int inputIDs[2];
    int inputSize;

    int nodeType;
    float nodeValue;

    bool computed;
};

struct Graph {
    int startAt;
    float originalNodeValues[100];
    bool originalComputed[100];

    Node nodes[100];
};

uniform Graph graph;

float nodeValues[100];
bool nodeComputed[100];

void computeAddNode(Node node, int nodeIndex, float x) {
    float sum = 0;

    for (int i = 0; i < node.inputSize; i++) {
        sum += nodeValues[node.inputIDs[i]];
    }

    nodeValues[nodeIndex] = sum;
}

void computeGraphNode(Node node, int nodeIndex, float x) {
    // If there is no connection, exit instantly
    if (node.inputSize == 0) {
        nodeValues[nodeIndex] = 0;
        return;
    }

    nodeValues[nodeIndex] = nodeValues[node.inputIDs[0]];
}

void computeSubNode(Node node, int nodeIndex, float x) {
    nodeValues[nodeIndex] = nodeValues[node.inputIDs[0]] - nodeValues[node.inputIDs[1]];;
}

void computeMulNode(Node node, int nodeIndex, float x) {
    nodeValues[nodeIndex] = nodeValues[node.inputIDs[0]] * nodeValues[node.inputIDs[1]];;
}

/**
 * Computes a node. Modifies the node and stores the output in nodeValue.
 * It assumes that all connections to the node are already computed.
 * @param node The node to compute
 * @return the computed value of the node
 */
float computeNode(Node node, int nodeIndex, float x) {
    nodeComputed[nodeIndex] = true;

    if (node.nodeType == 0) {
        computeGraphNode(node, nodeIndex, x);
    } else if (node.nodeType == 1) {
        nodeValues[nodeIndex] = x;
    } else if (node.nodeType == 3) {
        computeAddNode(node, nodeIndex, x);
    } else if (node.nodeType == 4) {
        computeSubNode(node, nodeIndex, x);
    } else if (node.nodeType == 5) {
        computeMulNode(node, nodeIndex, x);
    }

    return nodeValues[nodeIndex];
}

void copyNodeValues() {
    nodeValues = graph.originalNodeValues;
}

void copyNodeComputed() {
    nodeComputed = graph.originalComputed;
}

/**
 * Evaluates the graph the y-coordinate for the x-coordinate, x
 * @param x The x-coordinate to find the y coordinate of
 * @return The y-value for the given x value
 */
float eval(float x) {
    copyNodeComputed();

    // While condition: repeat until the final value is calculated
    while (!nodeComputed[graph.startAt]) {
        // Start at the starting node
        int index = graph.startAt;

        while (true) {
            Node node = graph.nodes[index];

            // Check if all of the nodes are computed.
            bool allConnectionsComputed = true;
            for (int i = 0; i < node.inputSize; i++) {

                // If a connection is not computed, go to that node and restart the process
                if (!nodeComputed[node.inputIDs[i]]) {
                    index = node.inputIDs[i];
                    node = graph.nodes[index];  // technically not needed but it's nice-to-have just in case
                    allConnectionsComputed = false;
                    break;
                }
            }

            // If all connections are computed, compute this node and restart at the beginning node
            if (allConnectionsComputed) {
                computeNode(node, index, x);
                break;
            }
        }
    }

    return nodeValues[graph.startAt];
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

    copyNodeValues();

    coords.y = -coords.y;  // flip the graph due to how texture coords are

    float yValue = eval(coords.x);  // f(x)

    // Derivative of the function
    // Since we can't do limits, we'll just use a really small value for h as a good approximation
    // f'(x) = (f(x + h) - f(x)) / h
    float h = 0.0001;
    float fPrime = (eval(coords.x + h) - yValue) / h;  // f`(x)

    // Line-point distance formula for any given point (x, y):
    // abs(f(x) - y) / sqrt(1 + f'(x)^2)
    float distance = abs(yValue - coords.y) / sqrt(1 + fPrime * fPrime);
    if (distance < RADIUS) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
        return;
    }

    // Check for axis lines
    if (abs(coords.x) < RADIUS || abs(coords.y) < RADIUS) {
        fragColor = vec4(1.0, 1.0, 1.0, 1.0);
        return;
    }

    fragColor = vec4(0.0, 0.0, 0.0, 1.0);
}