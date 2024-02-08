#version 410

#define NUM_INPUTS 1000

uniform vec2 xRange;
uniform vec2 yRange;
uniform float radiusUV;
uniform float yValues[1000];

in vec2 texCoords;
out vec4 fragColor;

float RADIUS = radiusUV * (abs(xRange.x) + abs(xRange.y));

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

float closestIndex(float x) {
    // TODO: linearly interpolate between indices
    return int(mapRange(x, xRange, vec2(0, NUM_INPUTS - 1)));
}

float calc(float x) {
    float closestIndex = mapRange(x, xRange, vec2(0, NUM_INPUTS - 1));
    float onlyDecimals = closestIndex - floor(closestIndex);
    return mix(yValues[int(closestIndex)], yValues[int(closestIndex) + 1], onlyDecimals);
}

void main() {
    vec2 coords = vec2(
        mapRange(texCoords.x, vec2(0, 1), xRange),
        mapRange(texCoords.y, vec2(0, 1), yRange)
    );

    coords.y = -coords.y;  // flip the graph due to how texture coords are

    float yValue = calc(coords.x);  // f(x)

    // Derivative of the function
    // Since we can't do limits, we'll just use a really small value for h as a good approximation
    // f'(x) = (f(x + h) - f(x)) / h

    // TODO: once linear interpolation is done, lower the h value
    float h = 0.0001;
    float fPrime = (calc(coords.x + h) - yValue) / h;  // f`(x)

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

// TODO: verify if this needs to be here
#undef NUM_INPUTS