#version 330 core
//#extension GL_NV_shadow_samplers_cube : enable
in vec3 texCoords;


out vec4 color;

uniform samplerCube skybox;
void main() {
    color = texture(skybox, texCoords);
}
