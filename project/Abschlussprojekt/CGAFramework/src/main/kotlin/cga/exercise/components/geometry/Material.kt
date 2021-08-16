package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.lwjgl.opengl.GL13.GL_TEXTURE0

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        emit.bind(0)
        shaderProgram.setUniform("material.emissive", 0)
        diff.bind(1)
        shaderProgram.setUniform("material.diffuse", 1)
        //specular.bind(2)
        shaderProgram.setUniform("material.specular", 2)

        shaderProgram.setUniform2f("tcMultiplier", tcMultiplier)
        shaderProgram.setUniformf("material.shininess", shininess)

    }
}