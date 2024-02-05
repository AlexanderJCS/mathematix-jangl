#version 410

uniform vec2 xRange;
uniform vec2 yRange;

in vec2 texCoords;
out vec4 fragColor;

float RADIUS = 0.005 * (abs(xRange.x) + abs(xRange.y));
float STEP = 0.0005 * (abs(xRange.x) + abs(xRange.y));

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