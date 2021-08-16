#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texture;
layout(location = 2) in vec3 normal;
//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view;
uniform mat4 projection;
uniform vec2 tcMultiplier;

out struct VertexData
{
    vec2 texture;
    vec3 normal;
    vec3 pointLightToLight;
    vec3 pointLightTokenToLight;
    vec3 pointLightGlobalToLight;
    vec3 spotLightToLight;
    vec3 toCamera;
} vertexData;

struct PointLight
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLight pointlight;

struct PointLightToken
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLightToken pointLightToken;

struct PointLightGlobal
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLightGlobal pointLightGlobal;

struct SpotLight
{
    vec3 LightColor;
    vec3 LightPosi;
    float innerConeAngle;
    float outerConeAngle;
    vec3 spotDirection;
    vec3 kWerte;
};
uniform SpotLight spotlight;

//
void main(){
    //vec4 pos = model_matrix * vec4(position, 1.0f);
    //gl_Position = vec4(pos.xy, -pos.z, 1.0f);
    mat4 viewModel = view * model_matrix;
    vec4 vertexDataPosi = viewModel * vec4(position, 1.0f);
    gl_Position = projection * vertexDataPosi;
    //vertexData.position = pos.xyz;


    vertexData.texture = tcMultiplier * texture;
    mat4 inverse_matrix = transpose(inverse(viewModel));
    vertexData.normal = (inverse_matrix * vec4(normal, 1.0f)).xyz;

    vertexData.toCamera = -vertexDataPosi.xyz;
    vertexData.pointLightToLight = pointlight.LightPosi - vertexDataPosi.xyz;
    vertexData.pointLightTokenToLight = pointLightToken.LightPosi - vertexDataPosi.xyz;
    vertexData.spotLightToLight = spotlight.LightPosi - vertexDataPosi.xyz;
    vertexData.pointLightGlobalToLight = pointLightGlobal.LightPosi - vertexDataPosi.xyz;

}