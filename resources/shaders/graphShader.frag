#version 410

#define NUM_INPUTS 1000

uniform vec2 xRange;
uniform vec2 yRange;
uniform float radiusUV;
uniform float yValues[1000];

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

float calc(float x) {
    float closestIndex = mapRange(x, xRange, vec2(0, NUM_INPUTS - 1));
    float onlyDecimals = closestIndex - floor(closestIndex);
    return mix(yValues[int(closestIndex)], yValues[int(closestIndex) + 1], onlyDecimals);
}

float getGridlineSpacing(float rangeX) {
    // TODO: define a formula for this instead of relying on if statements
    if (rangeX < 0.4) {
        return 0.04;
    } if (rangeX < 4) {
        return 0.4;
    } if (rangeX < 40) {
        return 4;
    } if (rangeX < 400) {
        return 40;
    } if (rangeX < 4000) {
        return 400;
    } if (rangeX < 40000) {
        return 4000;
    }
}

void main() {
    vec2 coords = vec2(
        mapRange(texCoords.x, vec2(0, 1), xRange),
        mapRange(1 - texCoords.y, vec2(0, 1), yRange)
    );

    float yValue = calc(coords.x);  // f(x)

    // Derivative of the function
    // Since we can't do limits, we'll just use a really small value for h as a good approximation
    // f'(x) = (f(x + h) - f(x)) / h
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
        fragColor = vec4(0.8, 0.8, 0.8, 1.0);
        return;
    }

    // Check for grid lines
    float rangeX = abs(xRange.y - xRange.x);

    float gridlineSep = getGridlineSpacing(abs(xRange.y - xRange.x));
    if (mod(coords.x, gridlineSep) < RADIUS / 1.5 || mod(coords.y, gridlineSep) < RADIUS / 1.5) {
        fragColor = vec4(0.3, 0.3, 0.3, 1.0);
        return;
    }

    // Draw sub-grid lines, which occur 5x as often as the main grid lines
    if (mod(coords.x, gridlineSep / 4) < RADIUS / 2 || mod(coords.y, gridlineSep / 4) < RADIUS / 2) {
        fragColor = vec4(0.15, 0.15, 0.15, 1.0);
        return;
    }

    fragColor = vec4(0.11, 0.11, 0.11, 1);
}
