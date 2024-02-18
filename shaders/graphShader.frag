#version 410

#define NUM_INPUTS 500

uniform vec2 xRange;
uniform vec2 yRange;
uniform float radiusUV;
uniform vec2 xyValues[NUM_INPUTS];

in vec2 texCoords;
out vec4 fragColor;

float RADIUS = (xRange.y - xRange.x) * radiusUV;

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

float getClosestIndex(float x) {
    return mapRange(x, xRange, vec2(0, NUM_INPUTS - 1));
}

float findSlope(float x) {
    float closestIndex = getClosestIndex(x);
    int lowIndex = int(closestIndex);
    int highIndex = int(ceil(closestIndex));

    // y2 - y1
    float deltaY = xyValues[highIndex].y - xyValues[lowIndex].y;

    // x2 - x1
    float deltaX = xyValues[highIndex].x - xyValues[lowIndex].x;

    return deltaY / deltaX;
}

float calculate(float x) {
    float closestIndex = getClosestIndex(x);
    float onlyDecimals = closestIndex - floor(closestIndex);

    return mix(
        xyValues[int(closestIndex)].y,
        xyValues[int(closestIndex) + 1].y,
        onlyDecimals
    );
}

float getGridlineSpacing(float rangeX, float maxGridlines) {
    return pow(maxGridlines, floor(log(rangeX) / log(maxGridlines))) / 2;
}

void main() {
    vec2 coords = vec2(
        mapRange(texCoords.x, vec2(0, 1), xRange),
        mapRange(1 - texCoords.y, vec2(0, 1), yRange)
    );

    float yValue = calculate(coords.x);  // f(x)

    // Derivative of the function
    // Since the derivative is the slope of the tangent line, we can get that by finding the slope of the line
    float fPrime = findSlope(coords.x);

    // Line-point distance formula for any given point (x, y):
    // abs(f(x) - y) / sqrt(1 + f'(x)^2)
    float distance = abs(yValue - coords.y) / sqrt(1 + fPrime * fPrime);
    if (distance < RADIUS) {
        fragColor = vec4(0.75, 0.3, 0, 1.0);
        return;
    }

    // Check for axis lines
    if (abs(coords.x) < RADIUS || abs(coords.y) < RADIUS) {
        fragColor = vec4(0.8, 0.8, 0.8, 1.0);
        return;
    }

    // Check for grid lines
    float rangeX = abs(xRange.y - xRange.x);

    float gridlineSep = getGridlineSpacing(abs(xRange.y - xRange.x), 5);
    if (mod(coords.x, gridlineSep) < RADIUS / 1.5 || mod(coords.y, gridlineSep) < RADIUS / 1.5) {
        fragColor = vec4(0.3, 0.3, 0.3, 1.0);
        return;
    }

    // Draw sub-grid lines, which occur 5x as often as the main grid lines
    if (mod(coords.x, gridlineSep / 5) < RADIUS / 2 || mod(coords.y, gridlineSep / 5) < RADIUS / 2) {
        fragColor = vec4(0.15, 0.15, 0.15, 1.0);
        return;
    }

    fragColor = vec4(0.11, 0.11, 0.11, 1);
}
