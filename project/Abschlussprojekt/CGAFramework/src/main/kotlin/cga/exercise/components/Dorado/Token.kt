package cga.exercise.components.Dorado

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import org.joml.Vector3f

class Token(var isBlocked:Boolean = false, var position: Int = 0, var colour: Vector3f, var number: Int,var isIsland: Boolean = false, meshList: MutableList<Mesh>) : Renderable(meshList) {

    fun move(hexagonList: MutableList<Hexagon>, amount: Int){

        for(i in 0 until amount){
            val neuePosi = hexagonList[position+i].getWorldPosition()
            val steinPosi = this.getWorldPosition()
            val mov =Vector3f((neuePosi.x - steinPosi.x), ((neuePosi.y + 0.07f) - steinPosi.y), (neuePosi.z - steinPosi.z))
            this.translateGlobal(mov)
        }
        position += amount

    }
}