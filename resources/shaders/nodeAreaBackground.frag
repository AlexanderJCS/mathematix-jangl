#version 410

uniform vec2 offset;
uniform float dotRadius;

in vec2 texCoords;
out vec4 fragColor;

void main() {
    // Calculate the distance from the current coordinate to the nearest grid point
    float dx = mod(texCoords.x, 0.1) - 0.05;
    float dy = mod(texCoords.y, 0.1) - 0.05;
    float dist = sqrt(dx * dx + dy * dy);

    // Create a bunch of dots around every (x, y) such that x and y are multiples of 0.1
    if (dist < dotRadius) {
        fragColor = vec4(0.05, 0.05, 0.05, 1.0);
    } else {
        fragColor = vec4(0.075, 0.075, 0.075, 1.0);
    }
}