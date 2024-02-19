#version 410

#define NUM_INPUTS 1000
#define NUM_INVALID_RANGES 10

uniform vec2 xRange;
uniform vec2 yRange;
uniform float radiusUV;
uniform vec2 xyValues[NUM_INPUTS];
uniform vec2 invalidRanges[NUM_INVALID_RANGES];
uniform float invalidRangesLength;

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

float closestIndex(float x) {
    return mapRange(x, xRange, vec2(0, NUM_INPUTS - 1));
}

float slope(float x) {
    float closest = closestIndex(x);

    int lower = int(closest);
    int higher = int(closest) + 1;


    // m = (y2 - y1) / (x2 - x1)
    return (xyValues[higher].y - xyValues[lower].y) /
        (xyValues[higher].x - xyValues[lower].x);
}

float calculate(float x) {
    float closest = closestIndex(x);
    float onlyDecimals = closest - floor(closest);
    return mix(xyValues[int(closest)].y, xyValues[int(closest) + 1].y, onlyDecimals);
}

float getGridlineSpacing(float rangeX, float maxGridlines) {
    return pow(maxGridlines, floor(log(rangeX) / log(maxGridlines))) / 2;
}

bool isInDomain(float x) {
    for (int i = 0; i < invalidRangesLength; i++) {
        if (x - RADIUS >= invalidRanges[i].x && x + RADIUS <= invalidRanges[i].y) {
            return false;
        }
    }

    return true;
}

void main() {
    vec2 coords = vec2(
        mapRange(texCoords.x, vec2(0, 1), xRange),
        mapRange(1 - texCoords.y, vec2(0, 1), yRange)
    );

    float yValue = calculate(coords.x);  // f(x)

    // Derivative of the function
    // Since the slope at f(x) = the derivative, we can find the slope at the point
    float fPrime = slope(coords.x);

    // Line-point distance formula for any given point (x, y):
    // abs(f(x) - y) / sqrt(1 + f'(x)^2)
    float distance = abs(yValue - coords.y) / sqrt(1 + fPrime * fPrime);
    if (isInDomain(coords.x) && distance < RADIUS) {
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