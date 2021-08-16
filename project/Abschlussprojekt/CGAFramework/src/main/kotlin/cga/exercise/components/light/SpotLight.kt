package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Math
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f

class SpotLight(posi: Vector3f, lightColor: Vector3f, private var innerConeAngle: Float, private var outerConeAngle: Float,
                k: Vector3f) : ISpotLight, PointLight(posi, lightColor, k) {
    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        super.bind(shaderProgram, name, viewMatrix)
        shaderProgram.setUniformf("$name.innerConeAngle", Math.cos(innerConeAngle))
        shaderProgram.setUniformf("$name.outerConeAngle", Math.cos(outerConeAngle))
        shaderProgram.setUniform3f("$name.spotDirection", viewMatrix.transformDirection(getWorldZAxis().negate()))
        shaderProgram.setUniform3f("$name.kWerte", k)
    }
}