package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram

open class Renderable(var meshList: MutableList<Mesh>): IRenderable, Transformable() {

    override fun render(shaderProgram: ShaderProgram) {

        shaderProgram.setUniform4f("model_matrix", getWorldModelMatrix(), false)

        for (m in meshList){
            //m.render()
            m.render(shaderProgram)
        }
    }
}