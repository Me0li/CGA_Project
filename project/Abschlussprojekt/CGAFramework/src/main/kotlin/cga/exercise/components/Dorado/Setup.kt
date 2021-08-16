package cga.exercise.components.Dorado

import org.joml.Vector3f

class Setup {
    var players: Int = 4
    var dingdong: Boolean = false
    val colors: MutableList<String> = mutableListOf()
    val farbArray : MutableList<Vector3f> = mutableListOf()



    fun players() {
    println("Wie viele Spieler nehmen teil? Maximal 4: ")
    val testing  = readLine()
    var status = 0

        if (testing != "2" && testing != "3" && testing != "4"){
            println("Bitte wählen Sie eine Spieleranzahl zwischen 2 und 4!")
            players()
            status = 1
        }
    if (status == 0){
        if (testing != null) {
            players =  testing.toInt()
        }
    }


}

    fun colors(spielerAnzahl: Int, n: Int): MutableList<Vector3f> {
        farbArray.add(Vector3f(3f,0f,0f))
        farbArray.add(Vector3f(0f,0f,3f))
        farbArray.add(Vector3f(0f,3f,0f))
        farbArray.add(Vector3f(3f,3f,0f))
        return farbArray



        var status = 0
        for (i in n until spielerAnzahl) {
            if (status == 0) {
                println("Welche Farbe hat Spieler " + (i + 1) + "? Alle Auswahlmöglichkeiten: rot, gruen, gelb, blau")
                val eingabe = readLine().toString()

                if (eingabe != "rot" && eingabe != "gruen" && eingabe != "gelb" && eingabe != "blau") {
                    println("Bitte wählen Sie zwischen: rot, gruen, gelb, blau!")
                    colors(players, i)
                    status = 1
                } else if (status == 0 && i > 0) {
                    for (k in 0 until colors.size) {
                        if (eingabe == colors[k]) {
                            println("Diese Farbe wurde bereits gewählt!")
                            print("Gewählte Farben: ")
                            for (l in 0 until colors.size) {
                                print(colors[l] + " ")
                            }
                            println("Bitte wählen Sie eine andere Farbe")
                            colors(players, i)
                            status = 1
                        }
                    }
                }
                if (status == 0){
                    colors.add(eingabe)
                    if (eingabe == "rot") farbArray.add(Vector3f(3f,0f,0f))
                    if (eingabe == "gelb") farbArray.add(Vector3f(3f,3f,0f))
                    if (eingabe == "gruen") farbArray.add(Vector3f(0f,3f,0f))
                    if (eingabe == "blau") farbArray.add(Vector3f(0f,0f,3f))
                }
            }
        }
        if (status == 0) {
            dingdong = true
            return farbArray
        } else return farbArray

    }
}