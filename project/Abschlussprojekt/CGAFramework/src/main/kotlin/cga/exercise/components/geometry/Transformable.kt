package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f


open class Transformable(var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {

        modelMatrix.rotateXYZ(pitch, yaw, roll)

    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val tempMatrix = Matrix4f()
        tempMatrix.translate(altMidpoint)
        tempMatrix.rotateXYZ(pitch, yaw, roll)
        tempMatrix.translate(Vector3f(altMidpoint).negate())

        //Platzierung
        modelMatrix = tempMatrix.mul(modelMatrix)

    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translateLocal(deltaPos: Vector3f) {

        modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: global operations will be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun translateGlobal(deltaPos: Vector3f) {

        modelMatrix = Matrix4f().translate(deltaPos).mul(modelMatrix)

    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scaleLocal(scale: Vector3f) {

        modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {
        val vec : Vector3f = Vector3f()
        modelMatrix.getColumn(3, vec)
        return vec
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {
        val position = Vector3f()
        val world = getWorldModelMatrix()
        world.getColumn(3, position)
        return position
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        val xAxis = Vector3f()
        modelMatrix.getColumn(0, xAxis)
        xAxis.normalize()
        return xAxis
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        val yAxis = Vector3f()
        modelMatrix.getColumn(1, yAxis)
        yAxis.normalize()
        return yAxis
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        val zAxis = Vector3f()
        modelMatrix.getColumn(2, zAxis)
        zAxis.normalize()
        return zAxis
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        val globalXAxis = Vector3f()
        val worldMatrix = getWorldModelMatrix()
        worldMatrix.getColumn(0,globalXAxis)
        globalXAxis.normalize()
        return  globalXAxis
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        val globalYAxis = Vector3f()
        val worldMatrix = getWorldModelMatrix()
        worldMatrix.getColumn(1,globalYAxis)
        globalYAxis.normalize()
        return  globalYAxis
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        val globalZAxis = Vector3f()
        val worldMatrix = getWorldModelMatrix()
        worldMatrix.getColumn(2,globalZAxis)
        globalZAxis.normalize()
        return  globalZAxis
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {


        return  parent?.getWorldModelMatrix()?.mul(modelMatrix) ?: Matrix4f(modelMatrix)
    }

    /**
     * Returns object model matrix
     * @return modelMatrix
     */
    fun getLocalModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }
}