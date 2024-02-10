#version 410

uniform vec2 offset;
uniform vec2 widthHeight;
uniform float dotRadius;
uniform float dotSeparation;

in vec2 texCoords;
out vec4 fragColor;

void main() {
    // Adjust texCoords for the rectangle's aspect ratio
    vec2 adjustedTexCoords = texCoords * widthHeight - vec2(0.5, 0.5);

    // Calculate the position of the current fragment in the grid
    vec2 gridPosition = adjustedTexCoords / dotSeparation;

    // Find the nearest grid point
    vec2 nearestGridPoint = round(gridPosition);

    // Calculate the distance to the nearest grid point
    float distanceToGridPoint = distance(gridPosition, nearestGridPoint);

    // If the distance is less than the dot radius, the fragment is part of a dot
    if (distanceToGridPoint < dotRadius) {
        fragColor = vec4(0.16, 0.16, 0.16, 1.0);
    } else {
        fragColor = vec4(0.11, 0.11, 0.11, 1.0);
    }
}