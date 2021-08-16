package cga.exercise.components.Dorado

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable


class Hexagon(var hexagonListOfTokens: MutableList<Token>, var isWater: Boolean = false, meshList: MutableList<Mesh>) : Renderable(meshList) {

}