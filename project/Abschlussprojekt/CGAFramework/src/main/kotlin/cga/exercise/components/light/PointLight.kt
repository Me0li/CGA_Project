package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f


open class PointLight(var posi: Vector3f, var lightColor: Vector3f, var k: Vector3f) :IPointLight, Transformable() {

    init {
        translateGlobal(posi)
    }

    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        shaderProgram.setUniform3f("$name.LightPosi", viewMatrix.transformPosition(getWorldPosition())) //getWorldPosition
        shaderProgram.setUniform3f("$name.LightColor", lightColor)
        shaderProgram.setUniform3f("$name.kWerte", k)
    }

}