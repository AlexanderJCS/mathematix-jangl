#version 410

uniform vec2 offset;
uniform vec2 widthHeight;
uniform float dotRadius;

in vec2 texCoords;
out vec4 fragColor;

void main() {
    // Adjust texCoords for the rectangle's aspect ratio
    vec2 adjustedTexCoords = texCoords * widthHeight;

    // Calculate the distance from the current coordinate to the nearest grid point
    float dx = mod(adjustedTexCoords.x, 0.05) - 0.05;
    float dy = mod(adjustedTexCoords.y, 0.05) - 0.05;
    float distSquared = dx * dx + dy * dy;

    // Create a bunch of dots around every (x, y) such that x and y are multiples of 0.1
    if (distSquared < pow(dotRadius, 2)) {
        fragColor = vec4(0.16, 0.16, 0.16, 1.0);
    } else {
        fragColor = vec4(0.11, 0.11, 0.11, 1.0);
    }
}