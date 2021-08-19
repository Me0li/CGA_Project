#version 330 core

//input from vertex shader
struct Material
{
    sampler2D diffuse;
    sampler2D emissive;
    sampler2D specular;
    float shininess;
};
uniform Material material;

in struct VertexData
{
    vec2 texture;
    vec3 normal;
    vec3 pointLightToLight;
    vec3 pointLightROToLight;
    vec3 pointLightRUToLight;
    vec3 pointLightLOToLight;
    vec3 pointLightLUToLight;
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

struct PointLightRO
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLightRO pointlightRO;

struct PointLightRU
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLightRU pointlightRU;

struct PointLightLO
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLightLO pointlightLO;

struct PointLightLU
{
    vec3 LightColor;
    vec3 LightPosi;
    vec3 kWerte;
};
uniform PointLightLU pointlightLU;

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

uniform vec3 sceneColor;

uniform float levels;

//fragment shader output
out vec4 color;

//Bidirectional Reflectance Distribution Function
vec3 BRDF(vec3 N, vec3 L, vec3 V, float shininess, vec3 diffuse, vec3 specular){
    return diffuse * max(dot(L, N),0) + specular * pow(max(dot(reflect(-L, N), V), 0), shininess);
}

vec3 BRDFBlinn(vec3 N, vec3 L, vec3 V, float shininess, vec3 diffuse, vec3 specular){

    float brightness = max(dot(L, N),0);
    float level = floor(brightness * levels);
    brightness = level / levels;

    return diffuse * brightness + specular * pow(max(dot(N, normalize(V+L)), 0), shininess);
}

vec3 getPointLightIntensity(float lPL, vec3 PLColor)
{
    float attenuation = 1.0f / (pointlight.kWerte.x + pointlight.kWerte.y * lPL + pointlight.kWerte.z * (lPL*lPL));
    return PLColor * attenuation;
}

vec3 getSpotLightIntensity(float lSL, vec3 lS, vec3 spotDirection, vec3 lightColor, float innerConeAngle, float outerConeAngle)
{
    float attenuation = 1.0f / (spotlight.kWerte.x + spotlight.kWerte.y * lSL + spotlight.kWerte.z * (lSL*lSL));

    float rou = outerConeAngle;
    float lu = innerConeAngle;
    float lo = dot(-lS, spotDirection);
    float I = clamp((lo - rou)/(lu-rou),0.0f,1.0f);
    return I* lightColor * attenuation;
}

void main(){
    vec3 N = normalize(vertexData.normal);
    vec3 V = normalize(vertexData.toCamera);

    float lPL = length(vertexData.pointLightToLight);
    float lSL = length(vertexData.spotLightToLight);
    float lPLT = length(vertexData.pointLightTokenToLight);
    float lPLG = length(vertexData.pointLightGlobalToLight);
    float lPLRO = length(vertexData.pointLightROToLight);
    float lPLRU = length(vertexData.pointLightRUToLight);
    float lPLLO = length(vertexData.pointLightLOToLight);
    float lPLLU = length(vertexData.pointLightLUToLight);

    vec3 LP = vertexData.pointLightToLight / lPL;
    vec3 LS = vertexData.spotLightToLight / lSL;
    vec3 LPT = vertexData.pointLightTokenToLight / lPLT;
    vec3 LPG = vertexData.pointLightGlobalToLight / lPLG;
    vec3 LPRO = vertexData.pointLightROToLight / lPLRO;
    vec3 LPRU = vertexData.pointLightRUToLight / lPLRU;
    vec3 LPLO = vertexData.pointLightLOToLight / lPLLO;
    vec3 LPLU = vertexData.pointLightLUToLight / lPLLU;

    vec3 emitColor = texture(material.emissive,vertexData.texture).rgb;
    vec3 diffColor = texture(material.diffuse,vertexData.texture).rgb;
    vec3 specColor = texture(material.specular,vertexData.texture).rgb;

    color.rgb = (emitColor * sceneColor
    +BRDFBlinn(N, LP, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPL, pointlight.LightColor)
    + BRDFBlinn(N, LPT, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLT, pointLightToken.LightColor)
    + BRDFBlinn(N, LPG, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLG, pointLightGlobal.LightColor)
    + BRDFBlinn(N, LPRO, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLRO, pointlightRO.LightColor)
    + BRDFBlinn(N, LPRU, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLRU, pointlightRU.LightColor)
    + BRDFBlinn(N, LPLO, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLLO, pointlightLO.LightColor)
    + BRDFBlinn(N, LPLU, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLLU, pointlightLU.LightColor)
    );
}

