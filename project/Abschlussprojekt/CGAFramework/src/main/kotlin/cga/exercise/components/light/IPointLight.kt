package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

interface IPointLight {
    fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f)
}