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

uniform vec3 sceneColor;


//fragment shader output
out vec4 color;

//Bidirectional Reflectance Distribution Function
vec3 BRDF(vec3 N, vec3 L, vec3 V, float shininess, vec3 diffuse, vec3 specular){
   return diffuse * max(dot(L, N),0) + specular * pow(max(dot(reflect(-L, N), V), 0), shininess);

}
//
vec3 BRDFBlinn(vec3 N, vec3 L, vec3 V, float shininess, vec3 diffuse, vec3 specular){
      return diffuse * max(dot(L, N),0) + specular * pow(max(dot(N, normalize(V+L)), 0), shininess);

}
//Attenuation muss hier noch verrechnet werden in 4.3.4
vec3 getPointLightIntensity(float lPL, vec3 PLColor)
{
    float attenuation = 1.0f / (pointlight.kWerte.x + pointlight.kWerte.y * lPL + pointlight.kWerte.z * (lPL*lPL));
    return PLColor * attenuation; /// pow(lPL, 2);
}

vec3 getSpotLightIntensity(float lSL, vec3 lS, vec3 spotDirection, vec3 lightColor, float innerConeAngle, float outerConeAngle)
{
    float attenuation = 1.0f / (spotlight.kWerte.x + spotlight.kWerte.y * lSL + spotlight.kWerte.z * (lSL*lSL));

    float rou = outerConeAngle;
    float lu = innerConeAngle;
    float lo = dot(-lS, spotDirection);
    float I = clamp((lo - rou)/(lu-rou),0.0f,1.0f);
    return I* lightColor * attenuation; /// pow(lSL, 2);
}

void main(){

    //color = vec4(0, (0.5f + abs(vertexData.position.z)), 0, 1.0f);
    vec3 c = vec3(abs(vertexData.normal.rgb));
    vec3 norm = normalize(c);
    //color = vec4(norm, 1.0f);
    vec3 N = normalize(vertexData.normal);
    vec3 V = normalize(vertexData.toCamera);

    float lPL = length(vertexData.pointLightToLight);
    float lSL = length(vertexData.spotLightToLight);
    float lPLT = length(vertexData.pointLightTokenToLight);
    float lPLG = length(vertexData.pointLightGlobalToLight);

    vec3 LP = vertexData.pointLightToLight / lPL;
    vec3 LS = vertexData.spotLightToLight / lSL;
    vec3 LPT = vertexData.pointLightTokenToLight / lPLT;
    vec3 LPG = vertexData.pointLightGlobalToLight / lPLG;

    vec3 emitColor = texture(material.emissive,vertexData.texture).rgb;
    vec3 diffColor = texture(material.diffuse,vertexData.texture).rgb;
    vec3 specColor = texture(material.specular,vertexData.texture).rgb;


    //color.rgb = (emitColor + vec3(1.0f)* 0.01 + BRDF(N, LP, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPL, pointlight.LightColor)
    color.rgb = (emitColor * sceneColor
    +BRDFBlinn(N, LP, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPL, pointlight.LightColor)
    //+ BRDFBlinn(N, LS, V, material.shininess, diffColor, specColor)
    //* getSpotLightIntensity(lSL, LS, spotlight.spotDirection, spotlight.LightColor, spotlight.innerConeAngle, spotlight.outerConeAngle)
    + BRDFBlinn(N, LPT, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLT, pointLightToken.LightColor)
    + BRDFBlinn(N, LPG, V, material.shininess, diffColor, specColor) * getPointLightIntensity(lPLG, pointLightGlobal.LightColor)
    );



    //color = texColor;
}

