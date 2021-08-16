package cga.exercise.components.geometry

import jdk.jfr.Unsigned
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.*
import java.nio.IntBuffer


class Skybox {


    private var skyboxVertices: FloatArray = floatArrayOf(
        -1f, -1f, 1f,
        1f, -1f, 1f,
        1f, -1f, -1f,
        -1f, -1f, -1f,
        -1f, 1f, 1f,
        1f, 1f, 1f,
        1f, 1f, -1f,
        -1f, 1f, -1f
    )

    private var skyboxIndices: IntArray = intArrayOf(
        //Rechts
        1, 2, 6,
        6, 5, 1,
        //Links
        0, 4, 7,
        7, 3, 0,
        //Oben
        4, 5, 6,
        6, 7, 4,
        //Unten
        0, 3, 2,
        2, 1, 0,
        //Hinten
        0, 1, 5,
        5, 4, 0,
        //Vorne
        3, 7, 6,
        6, 2, 3
    )

    private var skyboxVAO = 0
    private var skyboxVBO = 0
    private var skyboxIBO = 0
    private var cubemapTexture = -1

    init {
        skyboxVAO = glGenVertexArrays()
        skyboxVBO = glGenBuffers()
        skyboxIBO = glGenBuffers()

        glBindVertexArray(skyboxVAO)
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, skyboxIBO)

        glBufferData(GL_ARRAY_BUFFER, skyboxVertices, GL_STATIC_DRAW)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, skyboxIndices, GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3*4, 0)



    }

    fun loadCubemap(textures: Array<String>){
        cubemapTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture)


        for (i in 0 until 6){
            val x = BufferUtils.createIntBuffer(1)
            val y = BufferUtils.createIntBuffer(1)
            val nrChannels = BufferUtils.createIntBuffer(1)
            STBImage.stbi_set_flip_vertically_on_load(false)
            val data = STBImage.stbi_load(textures[i], x, y, nrChannels, 0)
                ?: throw Exception("Image file \"" + textures[i] + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())

            val width = x.get()
            val height = y.get()
            GL11.glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                0,
                GL_RGB,
                width,
                height,
                0,
                GL_RGB,
                GL_UNSIGNED_BYTE,
                data
            )
            STBImage.stbi_image_free(data)

        }


        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
        glBindTexture(GL_TEXTURE_CUBE_MAP,0)
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun render(){
        glActiveTexture(GL_TEXTURE0)
        glBindVertexArray(skyboxVAO)

        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture)
        glDrawElements(GL_TRIANGLES, skyboxIndices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }


}