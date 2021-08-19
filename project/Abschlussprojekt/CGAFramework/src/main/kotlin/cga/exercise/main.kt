package cga.exercise

import cga.exercise.game.Game
import cga.exercise.components.Dorado.Setup

fun main() {

    //bevor das Spielfenster sich öffnet werden die Spieleinstellungen vorgenommen
    val setup = Setup()
    setup.players()
    val colors = setup.colors(setup.players, 0)


    val game = Game(1280, 720, setup.players, colors)
        game.run()

}