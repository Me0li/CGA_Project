package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class TronCamera(var fieldOfView: Float = 1.57f, var aspectRatio : Float = 16.0f/9.0f, var nearPlane: Float = 0.1f, var farPlane: Float = 100.0f): ICamera, Transformable() {
    override fun getCalculateViewMatrix(): Matrix4f {
        val eye = getWorldPosition() // Kamera Position
        val up = getWorldYAxis() //lokale Y Vektor
        val center = getWorldPosition().sub(getWorldZAxis()) // 4 Spalte von getWorldPosition minus die Z Achse für einen Punkt vor der Kamera. Z Achse zeigt an sich hinter die Kamera

        return Matrix4f().lookAt(eye, center, up)
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fieldOfView, aspectRatio, nearPlane, farPlane)
    }

    override fun bind(shader: ShaderProgram) {

        shader.setUniform4f("view", getCalculateViewMatrix(), false)
        shader.setUniform4f("projection", getCalculateProjectionMatrix(), false)
    }
}