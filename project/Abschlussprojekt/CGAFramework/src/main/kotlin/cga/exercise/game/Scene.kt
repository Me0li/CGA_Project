package cga.exercise.game

import cga.exercise.components.Dorado.Hexagon
import cga.exercise.components.Dorado.Token
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import kotlin.math.sin



/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow, private val playerCount: Int, playerColour: MutableList<Vector3f>) {
    private var staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram
   // private var mesh1: Mesh
    //private var initialMeshD: Mesh
    //private var initialMeshW: Mesh
    //private var sphereMesh: Mesh
    private var groundMesh: Mesh
    private var cameraCeiling: TronCamera
    private var cameraFree: TronCamera
    private var cameraToken: TronCamera
    private var cameraDice: TronCamera
    private var renderableGround: Renderable
    private var material: Material
    //private var lightCycle: Renderable?
    private var pointLightFeld: PointLight
    private var pointLightToken: PointLight
    //private var spotLight: SpotLight
    private var mousePosi: Vector2f = Vector2f(0.0f,0.0f)
    private var firstMouseMove: Boolean = true
    private var hexagonMeshLand: Mesh
    private var tokenMesh: Mesh
    private var listOfTokens: MutableList<Token> = mutableListOf()
    private var listOfHexagons: MutableList<Hexagon> = mutableListOf()
    private var diceMesh: Mesh
    private var renderableDice: Renderable
    //private var startMesh: Mesh
    //private var renderableStart: Renderable
    private var playersTurn: Int = 1
    private var status: Int = 0
    private var status2: Boolean = true
    private var lightytighty: Int = 4
    private var diceRoll: Int = 0
    private var pointLightGlobal: PointLight
    //private var start: Hexagon
    private var rot :Boolean = false
    private var gelb :Boolean = false
    private var gruen :Boolean = false
    private var blau :Boolean = false
    private var cameraType : Int = 2
    private var skybox: Skybox = Skybox()
    private var skyboxScale: Transformable = Transformable()
    private var hexagonMaterialLand: Material
    private var shaderStatus: Int = 0
    private var levels: Float = 3f
    private var lastPressed: Int = 0
    private val emit: Texture2D
    private var emitSwitch: Int = 0
    private val diff: Texture2D
    private val spec: Texture2D
    private val vertexAttribute: Array<VertexAttribute>
    private var diceMaterial: Material
    private var diceRotated: Int = 0
    private var turnX: Int = 0
    private var turnY: Int = 0
    private var turnZ: Int = 0
    private var randomNumberArray: Array<Int> = arrayOf(-17,-16,-15,-14,-13,-12,-11,-10,17,16,15,14,13,12,11,10)
    private val tokenMaterial: Material
    private val hexagonMaterial: Material
    private val hexagonMeshWater: Mesh
    private var fov: Float = 1.57f
    private var pointLightGlobalLO: PointLight
    private var pointLightGlobalLU: PointLight
    private var pointLightGlobalRO: PointLight
    private var pointLightGlobalRU: PointLight


    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl","assets/shaders/skybox_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        //glDisable(GL_CULL_FACE); GLError.checkThrow()
        //glEnable(GL_CULL_FACE)
        //glFrontFace(GL_CCW); GLError.checkThrow()
        //glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        // Verschiedene Kameras einrichten
        cameraCeiling = TronCamera()

        cameraCeiling.rotateLocal(-1.57f, 0.0f, 0f)
        cameraCeiling.translateLocal(Vector3f(5f, -10f, 9f))
        cameraCeiling.bind(staticShader)

        cameraFree = TronCamera()
        cameraFree.translateLocal(Vector3f(0f,1f,0f))

        cameraToken = TronCamera()
        cameraToken.rotateLocal(-0.2f,0f,0f)
        cameraToken.translateLocal(Vector3f(0f,2f,4f))

        cameraDice = TronCamera()
        cameraDice.translateLocal(Vector3f(5f, 0.5f,-1.5f))
        cameraDice.rotateLocal(0f,3.14f,0f)


        //Create the Mesh
        val stride = 8*4
        val attrPos = VertexAttribute(3, GL_FLOAT, stride,0)
        val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3*4)
        val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5*4)
        vertexAttribute = arrayOf(attrPos, attrTC, attrNorm)



        //Textur Material erstellen
        emit = Texture2D("assets/textures/tilemap.png", true)
        emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        diff = Texture2D("assets/textures/ground_diff.png", true)
        diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        spec = Texture2D("assets/textures/ground_spec.png", true)
        spec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        material = Material(diff, emit, spec, 60.0f, Vector2f(1f))


        val hexagonLandTexture = Texture2D("assets/textures/Hexagon_Land.png", true)
        hexagonLandTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        hexagonMaterialLand = Material(Texture2D("assets/textures/ground_diff.png", false),hexagonLandTexture, Texture2D("assets/textures/ground_diff.png",false), 60f, Vector2f(1f))

        val hexa : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/hex.obj", false, false)
        val hexaMeshList : MutableList<OBJLoader.OBJMesh> = hexa.objects[0].meshes
        hexagonMeshLand = Mesh(hexaMeshList[0].vertexData, hexaMeshList[0].indexData, vertexAttribute, hexagonMaterialLand)

        val hexagonWaterTexture = Texture2D("assets/textures/Hexagon.png", true)
        hexagonWaterTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        hexagonMaterial = Material(Texture2D("assets/textures/ground_diff.png", false),hexagonWaterTexture, Texture2D("assets/textures/ground_diff.png",false), 60f, Vector2f(1f))
        hexagonMeshWater = Mesh(hexaMeshList[0].vertexData, hexaMeshList[0].indexData, vertexAttribute, hexagonMaterial)
        val renderableListHexa: MutableList<Mesh> = mutableListOf()
        renderableListHexa.add(hexagonMeshLand)
        val renderableListHexaWater: MutableList<Mesh> = mutableListOf()
        renderableListHexaWater.add(hexagonMeshWater)


        //Hexagone werden zu der listOfHexagons hinzugefügt und es wird zwischen Wasserfeld und nicht Wasserfeld unterschieden
        for (i in 0 until 36){
            if (i == 6|| i == 12 || i == 13 || i == 18 || i == 19 || i == 20 || i == 24 || i == 25 || i == 26 || i == 27 || i == 30 || i == 31 || i == 32
                || i == 33 || i == 34){
                listOfHexagons.add(Hexagon(mutableListOf(), true, renderableListHexaWater))
            } else{
                listOfHexagons.add(Hexagon(mutableListOf(),false, renderableListHexa))
            }
        }

        //Hexagone werden auf dem Spielfeld platziert
        for (i in 0 until 36){

            if(i in 0 until  4){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(17f,0f, 14f - 2f* i))
            }
            if (i == 4){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(16f,0f, 6f))
            }
            if (i == 5){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(14f,0f, 4f))
            }
            if (i == 6){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(12f,0f, 3f))
            }
            if (i == 7){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(10f,0f, 2f))
            }
            if (i in 8 until 14){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,1.57f)
                listOfHexagons[i].translateGlobal(Vector3f(10f - (2f*i- 14),0f, 2f))
            }

            if (i == 14){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-4f,0f, 3f))
            }
            if (i == 15){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-5.5f,0f, 4.5f))
            }
            if (i == 16){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-6.5f,0f, 6.5f))
            }
            if (i in 17 until 20){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-6.5f,0f, 8.5f + (2f*i - 34)))
            }
            if (i == 20){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-5.5f,0f, 14f))
            }
            if (i == 21){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-4f,0f, 15.5f))
            }
            if (i == 22){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-2f,0f, 16.5f))
            }
            if (i in 23 until 27){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(-2f + (2f*i -44),0f, 16.5f))
            }
            if (i == 27){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(7.5f,0f, 15.5f))
            }
            if (i == 28){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(8.6f,0f, 14f))
            }
            if (i in 29 until 31){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(9f,0f, 14f - (2f*i -56)))
            }
            if (i == 31){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(7.7f,0f, 8.5f))
            }
            if (i == 32){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(6f,0f, 7.5f))
            }
            if (i in 33 until 36){
                listOfHexagons[i].scaleLocal(Vector3f(0.001f))
                listOfHexagons[i].rotateLocal(-1.57f,0f,0f)
                listOfHexagons[i].translateGlobal(Vector3f(6f - (2f*i -64),0f, 7.5f))
            }
        }

        //start = Hexagon(mutableListOf(), false, mutableListOf())
        //Token Objekt reinladen und Textur mitgeben
        val token : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/dameSteinNeu.obj", false, false)
        val tokenMeshList : MutableList<OBJLoader.OBJMesh> = token.objects[0].meshes
        val tokenTexture = Texture2D("assets/textures/Tokenmap.png", true)
        tokenTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tokenMaterial = Material(Texture2D("assets/textures/ground_diff.png", false),tokenTexture, Texture2D("assets/textures/ground_diff.png",false), 60f, Vector2f(1f))

        tokenMesh = Mesh(tokenMeshList[0].vertexData, tokenMeshList[0].indexData, vertexAttribute, tokenMaterial)

        val renderableListToken : MutableList<Mesh> = mutableListOf()
        renderableListToken.add(tokenMesh)

        //Token wird im Startgebiet platziert und in die listOfTokens hinzugefügt
        var counter = 0
        for (k in 0 until playerCount){

            for (i in 0 until 4)
            {
                if (k<2){
                    listOfTokens.add(Token(true, 0, playerColour[k], i+1, false, renderableListToken))
                    if (listOfTokens[counter].number == 4) listOfTokens[counter].isBlocked = false
                    listOfTokens[counter].scaleLocal(Vector3f(0.5f))
                    listOfTokens[counter].translateGlobal(Vector3f(17.7f- k * 1.3f,0.1f + i * 0.16f,16f))
                    //start.hexagonListOfTokens.add(listOfTokens[counter])
                    counter++
                }
                else if (k>1) {
                    listOfTokens.add(Token(true, 0, playerColour[k], i+1, false, renderableListToken))
                    if (listOfTokens[counter].number == 4) listOfTokens[counter].isBlocked = false
                    listOfTokens[counter].scaleLocal(Vector3f(0.5f))
                    listOfTokens[counter].translateGlobal(Vector3f(17.7f- (k-2) * 1.3f,0.1f + i * 0.16f,18f))
                    //start.hexagonListOfTokens.add(listOfTokens[counter])
                    counter++

                }
            }
        }

        //es wird geguckt welche Farben für die Spieler gewählt wurden
        for (i in 0 until listOfTokens.size){
            if (listOfTokens[i].colour == Vector3f(3f,0f,0f)) rot =true
            if (listOfTokens[i].colour == Vector3f(3f,3f,0f)) gelb =true
            if (listOfTokens[i].colour == Vector3f(0f,3f,0f)) gruen =true
            if (listOfTokens[i].colour == Vector3f(0f,0f,3f)) blau =true
        }

        //Würfelobjekt wird reingeladen, Textur wird hinzugefügt und in der Welt platziert
        val dice : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/Dice.obj", false, false)
        val diceMeshList : MutableList<OBJLoader.OBJMesh> = dice.objects[0].meshes
        val diceTexture = Texture2D("assets/textures/Dicemap.png", true)
        diceTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        diceMaterial = Material(Texture2D("assets/textures/ground_diff.png", false),diceTexture, Texture2D("assets/textures/ground_diff.png",false), 60f, Vector2f(1f))



        diceMesh = Mesh(diceMeshList[0].vertexData, diceMeshList[0].indexData, vertexAttribute, diceMaterial)
        val renderableListDice : MutableList<Mesh> = mutableListOf()
        renderableListDice.add(diceMesh)

        renderableDice = Renderable(renderableListDice)
        renderableDice.scaleLocal(Vector3f(2f))
        renderableDice.translateGlobal(Vector3f(5f, 0.5f,-0.5f))


        //Bodenobjekt wird reingeladen, Textur wird hinzugefügt und in der Welt platziert
        val ground : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj", false, false)
        val groundMeshList: MutableList<OBJLoader.OBJMesh> = ground.objects[0].meshes
        groundMesh = Mesh(groundMeshList[0].vertexData, groundMeshList[0].indexData, vertexAttribute, material)
        val renderableListGround : MutableList<Mesh> = mutableListOf()
        renderableListGround.add(groundMesh)
        renderableGround = Renderable(renderableListGround)
        renderableGround.scaleLocal(Vector3f(0.74f,0.5f,0.5f))
        renderableGround.translateGlobal(Vector3f(5f,0f,9f))

        //Lichter werden erzeugt und platziert
        pointLightFeld = PointLight(Vector3f(50.0f,1f,0.0f), listOfTokens[0].colour, Vector3f(1.0f, 0.5f, 0.1f))
        pointLightToken = PointLight(Vector3f(0f, 10f,0f), Vector3f(1f, 1f, 1f), Vector3f(1.0f, 0.5f, 0.1f))
        //spotLight = SpotLight(Vector3f(0.0f,1.0f,0.0f), Vector3f(1.0f, 1.0f, 1.0f), 0.17f, 0.44f, Vector3f(0.5f, 0.05f, 0.01f))
        pointLightGlobal = PointLight(Vector3f(6f, 10f, 10f), Vector3f(6f), Vector3f(1.0f,0.5f,0.1f))
        pointLightGlobalRO = PointLight(Vector3f(15f, 10f, 2f), Vector3f(4f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLightGlobalRU = PointLight(Vector3f(15f, 10f, 17f), Vector3f(4f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLightGlobalLO = PointLight(Vector3f(-5f, 10f, 2f), Vector3f(4f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLightGlobalLU = PointLight(Vector3f(-5f, 10f, 17f), Vector3f(4f), Vector3f(1.0f, 0.5f, 0.1f))
        pointLightToken.rotateLocal(1.57f, 0f, 0f)

        //Token Kamera wird an den obersten Stein des ersten Spielers angehängt
        cameraToken.parent = listOfTokens[3]

        //Cubemap pngs werden in einem Array gespeichert
        val facesCubemapStil1 = arrayOf<String>(
            "assets/textures/right.png",
            "assets/textures/left.png",
            "assets/textures/up.png",
            "assets/textures/down.png",
            "assets/textures/front.png",
            "assets/textures/back.png"

        )

        val facesCubemapStil2 = arrayOf<String>(
            "assets/textures/lakeboxright.png",
            "assets/textures/lakeboxleft.png",
            "assets/textures/lakeboxup.png",
            "assets/textures/lakeboxdown.png",
            "assets/textures/lakeboxfront.png",
            "assets/textures/lakeboxback.png"

        )

        //Skybox Textur wird ausgewählt und Skybox wird in der Welt platziert
        skybox.loadCubemap(facesCubemapStil1)
        skyboxScale.scaleLocal(Vector3f(15f,15f,11f))
        skyboxScale.translateGlobal(Vector3f(5f,10f,9f))

        skyboxShader.setUniform("skybox", 0)


    }

    //für renderdoc: Executable Path: D:\Downloads\openjdk-16_windows-x64_bin\jdk-16\bin\javaw.exe
    //               Working Directory: C:/Users/danie/Uni/CGA/Assignment1/CGAFramework
    //               Commandline Arguments: -jar C:\Users\danie\Uni\CGA\Assignment1\CGAFramework\build\libs\CGAFramework-1.0.jar
    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.use()
        if (cameraType == 1){
            pointLightFeld.bind(staticShader, "pointlight", cameraToken.getCalculateViewMatrix())
            // spotLight.bind(staticShader, "spotlight", Camera.getCalculateViewMatrix())
            pointLightToken.bind(staticShader, "pointLightToken", cameraToken.getCalculateViewMatrix())
            pointLightGlobal.bind(staticShader, "pointLightGlobal", cameraToken.getCalculateViewMatrix())
            pointLightGlobalRO.bind(staticShader, "pointlightRO", cameraToken.getCalculateViewMatrix())
            pointLightGlobalRU.bind(staticShader, "pointlightRU", cameraToken.getCalculateViewMatrix())
            pointLightGlobalLO.bind(staticShader, "pointlightLO", cameraToken.getCalculateViewMatrix())
            pointLightGlobalLU.bind(staticShader, "pointlightLU", cameraToken.getCalculateViewMatrix())

            cameraToken.bind(staticShader)
            staticShader.setUniform4f("projection", Matrix4f().perspective(fov, cameraToken.aspectRatio, cameraToken.nearPlane, cameraToken.farPlane), false)
        }

        if (cameraType == 2){
            pointLightFeld.bind(staticShader, "pointlight", cameraCeiling.getCalculateViewMatrix())
            // spotLight.bind(staticShader, "spotlight", Camera.getCalculateViewMatrix())
            pointLightToken.bind(staticShader, "pointLightToken", cameraCeiling.getCalculateViewMatrix())
            pointLightGlobal.bind(staticShader, "pointLightGlobal", cameraCeiling.getCalculateViewMatrix())
            pointLightGlobalRO.bind(staticShader, "pointlightRO", cameraCeiling.getCalculateViewMatrix())
            pointLightGlobalRU.bind(staticShader, "pointlightRU", cameraCeiling.getCalculateViewMatrix())
            pointLightGlobalLO.bind(staticShader, "pointlightLO", cameraCeiling.getCalculateViewMatrix())
            pointLightGlobalLU.bind(staticShader, "pointlightLU", cameraCeiling.getCalculateViewMatrix())

            cameraCeiling.bind(staticShader)
        }
        if (cameraType == 3){
            pointLightFeld.bind(staticShader, "pointlight", cameraFree.getCalculateViewMatrix())
            // spotLight.bind(staticShader, "spotlight", Camera.getCalculateViewMatrix())
            pointLightToken.bind(staticShader, "pointLightToken", cameraFree.getCalculateViewMatrix())
            pointLightGlobal.bind(staticShader, "pointLightGlobal", cameraFree.getCalculateViewMatrix())
            pointLightGlobalRO.bind(staticShader, "pointlightRO", cameraFree.getCalculateViewMatrix())
            pointLightGlobalRU.bind(staticShader, "pointlightRU", cameraFree.getCalculateViewMatrix())
            pointLightGlobalLO.bind(staticShader, "pointlightLO", cameraFree.getCalculateViewMatrix())
            pointLightGlobalLU.bind(staticShader, "pointlightLU", cameraFree.getCalculateViewMatrix())

            cameraFree.bind(staticShader)
        }

        if (cameraType == 4){
            pointLightFeld.bind(staticShader, "pointlight", cameraDice.getCalculateViewMatrix())
            // spotLight.bind(staticShader, "spotlight", Camera.getCalculateViewMatrix())
            pointLightToken.bind(staticShader, "pointLightToken", cameraDice.getCalculateViewMatrix())
            pointLightGlobal.bind(staticShader, "pointLightGlobal", cameraDice.getCalculateViewMatrix())
            pointLightGlobalRO.bind(staticShader, "pointlightRO", cameraDice.getCalculateViewMatrix())
            pointLightGlobalRU.bind(staticShader, "pointlightRU", cameraDice.getCalculateViewMatrix())
            pointLightGlobalLO.bind(staticShader, "pointlightLO", cameraDice.getCalculateViewMatrix())
            pointLightGlobalLU.bind(staticShader, "pointlightLU", cameraDice.getCalculateViewMatrix())

            cameraDice.bind(staticShader)
        }

        //renderableSphere.render(staticShader)
        staticShader.setUniform3f("sceneColor", Vector3f(0.5f,0.7f,1.0f))
        renderableGround.render(staticShader)
        staticShader.setUniform3f("sceneColor", Vector3f(1f,1f,1f))
        renderableDice.render(staticShader)
        //staticShader.setUniform3f("sceneColor", Vector3f(3f,0f,3f))
        //renderableStart.render(staticShader)
        staticShader.setUniform3f("sceneColor", Vector3f(3f,3f,0f))
        for (i in 0 until listOfHexagons.size){
            if (listOfHexagons[i].isWater){
                staticShader.setUniform3f("sceneColor", Vector3f(0.5f, 0.7f, 1.1f))
                listOfHexagons[i].render(staticShader)
            }
            else {
                staticShader.setUniform3f("sceneColor", Vector3f(1f, 1f, 1f))
                listOfHexagons[i].render(staticShader)
            }
        }

        for (i in 0 until listOfTokens.size) {
            if (listOfTokens[i].isIsland){
                staticShader.setUniform3f("sceneColor", Vector3f(3f,3f,3f))
                listOfTokens[i].render(staticShader)
            }else{
                staticShader.setUniform3f("sceneColor", listOfTokens[i].colour)
                listOfTokens[i].render(staticShader)
            }

        }

        glDepthFunc(GL_LEQUAL)
        glDepthMask(false)
        skyboxShader.use()
        skyboxShader.setUniform4f("model", skyboxScale.modelMatrix, false)
        if (cameraType == 1){

            skyboxShader.setUniform4f("projection", cameraToken.getCalculateProjectionMatrix(), false)
            skyboxShader.setUniform4f("view", Matrix4f(cameraToken.getCalculateViewMatrix()), false)

        }

        if (cameraType == 2){
            skyboxShader.setUniform4f("projection", cameraCeiling.getCalculateProjectionMatrix(), false)
            skyboxShader.setUniform4f("view", Matrix4f(cameraCeiling.getCalculateViewMatrix()), false)
        }
        if (cameraType == 3){

            skyboxShader.setUniform4f("projection", cameraFree.getCalculateProjectionMatrix(), false)
            skyboxShader.setUniform4f("view", Matrix4f(cameraFree.getCalculateViewMatrix()), false)
        }

        if (cameraType == 4){

            skyboxShader.setUniform4f("projection", cameraDice.getCalculateProjectionMatrix(), false)
            skyboxShader.setUniform4f("view", Matrix4f(cameraDice.getCalculateViewMatrix()) , false)
        }
        skybox.render()
        glDepthMask(true)
        glDepthFunc(GL_LESS)
        staticShader.setUniformf("levels", levels)
    }

    fun update(dt: Float, t: Float) {

        if (cameraType == 3){
            if (window.getKeyState(GLFW.GLFW_KEY_W)) {
                val deltaPos = Vector3f(0.0f, 0.0f, -9f*dt)

                cameraFree.translateLocal(deltaPos)

                if (window.getKeyState(GLFW.GLFW_KEY_A)) {


                    cameraFree.rotateLocal(0.0f, 2f * dt, 0.0f)
                }
                if (window.getKeyState(GLFW.GLFW_KEY_D)) {

                    cameraFree.rotateLocal(0.0f,-2f*dt, 0.0f)
                }

            }
            if (window.getKeyState(GLFW.GLFW_KEY_S)) {
                val deltaPos = Vector3f(0.0f, 0.0f, 8f*dt)

                cameraFree.translateLocal(deltaPos)

                if (window.getKeyState(GLFW.GLFW_KEY_A)) {

                    cameraFree.rotateLocal(0.0f, -2f * dt, 0.0f)
                }

                if (window.getKeyState(GLFW.GLFW_KEY_D)) {

                    cameraFree.rotateLocal(0.0f,2f*dt, 0.0f)
                }
            }
            if (window.getKeyState(GLFW.GLFW_KEY_SPACE)) {
                cameraFree.translateLocal(Vector3f(0f, 4f*dt, 0f))
            }
            if (window.getKeyState(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                cameraFree.translateLocal(Vector3f(0f, -4f*dt, 0f))
            }
        }

        /*if (cameraType == 2){
            if (window.getKeyState(GLFW.GLFW_KEY_SPACE)) {
                cameraCeiling.translateLocal(Vector3f(0f, 0f, 4f*dt))
            }
            if (window.getKeyState(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                cameraCeiling.translateLocal(Vector3f(0f, 0f, -4f*dt))
            }
        }
        
         */

        if (cameraType == 4){


            if ((diceRotated == 0 || diceRotated == 1) && window.getKeyState(GLFW.GLFW_KEY_SPACE)){
                renderableDice.rotateLocal(turnX*dt, turnY*dt,turnZ*dt)
                diceRotated = 1
            }
            else if (diceRotated == 1 && !window.getKeyState(GLFW.GLFW_KEY_SPACE)) {
                renderableDice.modelMatrix = Matrix4f()
                renderableDice.scaleLocal(Vector3f(2f))
                renderableDice.translateGlobal(Vector3f(5f, 0.5f,-0.5f))
                if (status2) {
                    if (diceRoll == 1) renderableDice.rotateLocal(3.14f, 0f, 0f)
                    else if (diceRoll == 2) renderableDice.rotateLocal(0f, 1.57f, 0f)
                    else if (diceRoll == 3) renderableDice.rotateLocal(-1.57f, 0f, 0f)
                    else if (diceRoll == 4) renderableDice.rotateLocal(1.57f, 0f, 0f)
                    else if (diceRoll == 5) renderableDice.rotateLocal(0f, -1.57f, 0f)
                    diceRotated = 2
                    status2 = false
                }
            }
        }


        if (status == 1){
            diceRoll = diceRoll()
            status2 = true
            diceRotated = 0
            turnX = randomNumberArray[kotlin.random.Random.nextInt(0,16)]
            turnY = randomNumberArray[kotlin.random.Random.nextInt(0,16)]
            turnZ = randomNumberArray[kotlin.random.Random.nextInt(0,16)]

            for (i in 0 until 4){
                if (listOfTokens[i +4*(playersTurn-1)].position + diceRoll > 36){
                    listOfTokens[i +4*(playersTurn-1)].isBlocked = true
                }
            }
            if (listOfTokens[lightytighty-1 +4*(playersTurn-1)].isBlocked){
                for (i in 0 until 4){
                    if (!listOfTokens[i + 4*(playersTurn-1)].isBlocked) lightytighty = i+1
                }
            }

            println("Sie haben eine " + diceRoll + " gewürfelt!")
            println("Spieler $playersTurn,bitte wählen Sie den zu bewegenden Spielstein.")
            val neuePosi :Vector3f
            //falls alle steine eines teams blockiert sind wird das Licht, welches die Position anzeigt an die man den Stein hinbewegen würde, nicht angezeigt
            if (listOfTokens[0 + 4*(playersTurn-1)].isBlocked && listOfTokens[1 + 4*(playersTurn-1)].isBlocked && listOfTokens[2 + 4*(playersTurn-1)].isBlocked && listOfTokens[3 + 4*(playersTurn-1)].isBlocked){
                neuePosi = Vector3f(50f,1f,50f)
                val lightPosi = pointLightFeld.getWorldPosition()
                val mov = Vector3f((neuePosi.x - lightPosi.x), (neuePosi.y - lightPosi.y), (neuePosi.z - lightPosi.z))
                pointLightFeld.translateGlobal(mov)
            }
          /*Das Licht, zum anzeigen wohin Stein bewegt werden würde, wird platziert
            1.If: Wenn die Summe von dem Würfelergebnis und der aktuellen Position des Steines über das letzte Hexagonfeld hinausgeht, wird kein Licht gesetzt,
            da dieser Stein sowieso nicht bewegt werden kann, weil er sonst über das Ziel hinaus schießen würde
            2.If: Hier wird geguckt ob in der hexagonListOfTokens was drin ist um die Höhe des Lichtes an die Anzahl der enthaltenden Steine anzupassen
            3.If: Wenn der oberste Stein ein ins Wasser gefallener Stein ist müssen wir die Höhe etwas anpassen, da der Stein der hexagonListOfTokens einen 0-Vektor hat*/
            else if (listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1 < 36){
                if (listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size > 0 ){
                    if (listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens[listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size-1].getWorldPosition() == Vector3f(0.0f,0.0f,0.0f)){
                        neuePosi= listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].getWorldPosition()
                        val lightPosi = pointLightFeld.getWorldPosition()
                        val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.23f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                        pointLightFeld.translateGlobal(mov)
                    }else{
                        neuePosi= listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens[listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size-1].getWorldPosition()
                        val lightPosi = pointLightFeld.getWorldPosition()
                        val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.16f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                        pointLightFeld.translateGlobal(mov)
                    }
                }else {
                    neuePosi = listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].getWorldPosition()
                    val lightPosi = pointLightFeld.getWorldPosition()
                    val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.16f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                    pointLightFeld.translateGlobal(mov)
                }

            }

            if (listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].colour == Vector3f(0f,0f,3f)){
                pointLightFeld.lightColor = Vector3f(1f,1f,4f)
            }else pointLightFeld.lightColor = listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].colour
            status = 2
        }

        if (status == 2){
            cameraType = 4
        }

        if(status == 3){
            if (!status2) {
                if (diceRoll == 1) renderableDice.rotateLocal(-3.14f, 0f, 0f)
                else if (diceRoll == 2) renderableDice.rotateLocal(0f, -1.57f, 0f)
                else if (diceRoll == 3) renderableDice.rotateLocal(1.57f, 0f, 0f)
                else if (diceRoll == 4) renderableDice.rotateLocal(-1.57f, 0f, 0f)
                else if (diceRoll == 5) renderableDice.rotateLocal(0f, 1.57f, 0f)

                status2 = true
            }

           if (lightytighty == 1){
               val hierisses = pointLightToken.getWorldPosition()
               val hierhin = listOfTokens[0+ 4 *(playersTurn-1)].getWorldPosition()
               val mov = Vector3f(hierhin.x - hierisses.x, hierhin.y - hierisses.y+0.2f, hierhin.z - hierisses.z)
               pointLightToken.translateGlobal(mov)
               pointLightToken.lightColor = Vector3f(sin(t/1)+1, sin(t/1)+1, sin(t/1)+1)

           }
            if (lightytighty == 2){
                val hierisses = pointLightToken.getWorldPosition()
                val hierhin = listOfTokens[1+ 4 *(playersTurn-1)].getWorldPosition()
                val mov = Vector3f(hierhin.x - hierisses.x, hierhin.y - hierisses.y+0.2f, hierhin.z - hierisses.z)
                pointLightToken.translateGlobal(mov)
                pointLightToken.lightColor = Vector3f(sin(t/1)+1, sin(t/1)+1, sin(t/1)+1)

            }
            if (lightytighty == 3){
                val hierisses = pointLightToken.getWorldPosition()
                val hierhin = listOfTokens[2+ 4 *(playersTurn-1)].getWorldPosition()
                val mov = Vector3f(hierhin.x - hierisses.x, hierhin.y - hierisses.y+0.2f, hierhin.z - hierisses.z)
                pointLightToken.translateGlobal(mov)
                pointLightToken.lightColor = Vector3f(sin(t/1)+1, sin(t/1)+1, sin(t/1)+1)

            }
            if (lightytighty == 4){
                val hierisses = pointLightToken.getWorldPosition()
                val hierhin = listOfTokens[3+ 4 *(playersTurn-1)].getWorldPosition()
                val mov = Vector3f(hierhin.x - hierisses.x, hierhin.y - hierisses.y +0.2f, hierhin.z - hierisses.z)
                pointLightToken.translateGlobal(mov)
                pointLightToken.lightColor = Vector3f(sin(t/1)+2, sin(t/1)+2, sin(t/1)+2)

            }
        }
        if (status == 4){

            if (diceRoll+listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position >=37) {
                for (i in 0 until 4){
                    if (listOfTokens[i +4*(playersTurn-1)].position + diceRoll > 36){
                        if (listOfHexagons[listOfTokens[i +4*(playersTurn-1)].position-1].hexagonListOfTokens.size >1){
                            if (listOfTokens[i +4*(playersTurn-1)].position != 36){
                                listOfHexagons[listOfTokens[i  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens[
                                        listOfHexagons[listOfTokens[i  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size-1].isBlocked = false

                            }
                        }
                    }
                }
                status+=1
            }
            else{
            //Hier wird das letzte Element an der alten Stelle der Position des Tokens das bewegt werden soll aus der hexagonListOfTokens entfernt. Dieses sollte auch das zu bewegende Token selber sein.
            //Zusätzlich wird das nun letzte Token der Liste auf nicht blockiert gesetzt
            if (listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position != 0){
                listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.removeAt(
                    listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size-1)
                if (listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size > 0){
                    listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens[
                            listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size-1].isBlocked = false
                }
            }
            if (listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position == 0 && listOfTokens[lightytighty-1  + 4 *(playersTurn-1)] != listOfTokens[(playersTurn-1)*4]){
                listOfTokens[(lightytighty-1  + 4 *(playersTurn-1))-1].isBlocked = false
            }



            //Hier wird das Token bewegt
            listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].move(listOfHexagons, diceRoll)


            //Hier wird das bewegte Token zu der listOfTokens an der neuen Position hinzugefügt und falls die Liste mindestens 2 Elemente enthält, wird das vorletzte Token blockiert.
            listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.add(listOfTokens[lightytighty-1  + 4 *(playersTurn-1)])
            if (listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size > 1){
                listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens[
                        listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size-2].isBlocked = true
            }
            if (listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].isWater){
                listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].isWater = false
                listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.set(0,Token(true, listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position, Vector3f(0f,0f,0f), 0, false, mutableListOf()))
                listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].isBlocked = true
                listOfTokens[lightytighty-1 + 4 *(playersTurn-1)].isIsland = true
                listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].colour = Vector3f(0f,0f,0f)
            }

            if (listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position == 36){
                listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].isBlocked = true
            }
            //Das bewegte Token wird auf die vorhanden Tokens draufgesetzt
            listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].translateGlobal(Vector3f(0f,  0.16f*(listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size-1), 0f))
            //println("size von listOfTokens " +listOfHexagons[listOfTokens[lightytighty-1  + 4 *(playersTurn-1)].position-1].hexagonListOfTokens.size)

                for (i in 0 until 4){
                    if (listOfTokens[i +4*(playersTurn-1)].position + diceRoll > 36){
                        if (listOfHexagons[listOfTokens[i +4*(playersTurn-1)].position-1].hexagonListOfTokens.size >1){
                            if (listOfTokens[i +4*(playersTurn-1)].position != 36) {
                                listOfHexagons[listOfTokens[i + 4 * (playersTurn - 1)].position - 1].hexagonListOfTokens[
                                        listOfHexagons[listOfTokens[i + 4 * (playersTurn - 1)].position - 1].hexagonListOfTokens.size - 1].isBlocked = false
                            }
                        }

                    }
                }
            status +=1
            }
        }
        if (status == 5){
            var areAllBlocked = true
            for (i in 0 until listOfTokens.size) {
                if (!listOfTokens[i].isBlocked){
                    areAllBlocked = false
                    break
                }
            }
            if (!areAllBlocked){
                if(playersTurn == playerCount){
                    playersTurn = 1
                }else playersTurn+=1
                var anzahlBlocked = 0
                for (i in 0 until 4) {
                    if (listOfTokens[i + 4 *(playersTurn-1)].isBlocked) anzahlBlocked+=1
                }
                if (anzahlBlocked == 4)
                {
                    if(playersTurn == playerCount) { playersTurn=1
                    }else playersTurn +=1
                    anzahlBlocked = 0
                    for (i in 0 until 4) {
                        if (listOfTokens[i + 4 *(playersTurn-1)].isBlocked) anzahlBlocked+=1
                    }
                    if (anzahlBlocked == 4){
                        if(playersTurn == playerCount) { playersTurn=1
                        }else playersTurn +=1
                        anzahlBlocked = 0
                        for (i in 0 until 4) {
                            if (listOfTokens[i + 4 *(playersTurn-1)].isBlocked) anzahlBlocked+=1
                        }
                        if (anzahlBlocked == 4){
                            if(playersTurn == playerCount) { playersTurn=1
                            }else playersTurn +=1
                        }

                    }

                }
                if (listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].isBlocked){
                    for (i in 0 until 4){
                        if (!listOfTokens[i + 4*(playersTurn-1)].isBlocked) lightytighty = i+1
                    }
                }
                cameraToken.parent = listOfTokens[lightytighty-1+ 4 *(playersTurn-1)]
                status=1
            }else {
                var punkteRot = 0
                var punkteGelb = 0
                var punkteGruen = 0
                var punkteBlau = 0
                pointLightToken.translateGlobal(Vector3f(0f,0f,0f))
                for (i in 0 until listOfHexagons[35].hexagonListOfTokens.size){
                    if (listOfHexagons[35].hexagonListOfTokens[i].colour == Vector3f(3f,0f,0f)) punkteRot += 100 + i* 10
                    else if(listOfHexagons[35].hexagonListOfTokens[i].colour == Vector3f(3f,3f,0f)) punkteGelb += 100 + i*10
                    else if(listOfHexagons[35].hexagonListOfTokens[i].colour == Vector3f(0f,3f,0f)) punkteGruen += 100 + i*10
                    else if(listOfHexagons[35].hexagonListOfTokens[i].colour == Vector3f(0f,0f,3f)) punkteBlau += 100 + i*10
                }
                println("Die Punkte sind wie folgt:")
                if (rot){
                    println("Rot: $punkteRot")
                }
                if (gelb){
                    println("Gelb: $punkteGelb")
                }
                if (gruen){
                   println("Gruen: $punkteGruen")
                }
                if (blau){
                    println("Blau: $punkteBlau")
                }

                if (punkteRot > punkteGelb && punkteRot > punkteBlau && punkteRot > punkteGruen && rot) println("Herzlichen Glückwunsch Rot, Sie haben gewonnen!!!")
                else if (punkteGelb > punkteRot && punkteGelb > punkteBlau && punkteGelb > punkteGruen && gelb) println("Herzlichen Glückwunsch Gelb, Sie haben gewonnen!!!")
                else if (punkteBlau > punkteGelb && punkteBlau > punkteRot && punkteBlau > punkteGruen && blau) println("Herzlichen Glückwunsch Blau, Sie haben gewonnen!!!")
                else if (punkteGruen > punkteGelb && punkteGruen > punkteBlau && punkteGruen > punkteRot && gruen) println("Herzlichen Glückwunsch Gruen, Sie haben gewonnen!!!")
                status+=1
            }

        }




    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_PRESS) {
            if (cameraType == 4 && diceRotated == 2){
                status += 1
                if (lastPressed == 0) cameraType = 2
                else cameraType = lastPressed
            }
            else if (cameraType != 4){
                status += 1
                if (lastPressed == 0) cameraType = 2
                else cameraType = lastPressed
            }
        }

        if (key == GLFW.GLFW_KEY_RIGHT && action == GLFW.GLFW_PRESS && lightytighty <4 && !listOfTokens[lightytighty  + 4 *(playersTurn-1)].isBlocked) lightytighty += 1
        else if (key == GLFW.GLFW_KEY_RIGHT && action == GLFW.GLFW_PRESS && lightytighty == 4 && !listOfTokens[0  + 4 *(playersTurn-1)].isBlocked) lightytighty = 1

        else if (key == GLFW.GLFW_KEY_RIGHT && action == GLFW.GLFW_PRESS && lightytighty <4 && listOfTokens[lightytighty  + 4 *(playersTurn-1)].isBlocked) {
            if (lightytighty <3 && !listOfTokens[lightytighty+1  + 4 *(playersTurn-1)].isBlocked) lightytighty +=2
            else if (lightytighty >=3 && !listOfTokens[lightytighty-3  + 4 *(playersTurn-1)].isBlocked) lightytighty -=2
            else if(lightytighty <3 && listOfTokens[lightytighty+1  + 4 *(playersTurn-1)].isBlocked){
                if (lightytighty == 1 && !listOfTokens[lightytighty+2  + 4 *(playersTurn-1)].isBlocked) lightytighty +=3
                if (lightytighty >1 && !listOfTokens[lightytighty-2  + 4 *(playersTurn-1)].isBlocked) lightytighty -=1
                else lightytighty
            }else if(lightytighty == 3 && !listOfTokens[lightytighty-2  + 4 *(playersTurn-1)].isBlocked) lightytighty -=1
            else lightytighty
        }
        else if (key == GLFW.GLFW_KEY_RIGHT && action == GLFW.GLFW_PRESS && lightytighty == 4 && listOfTokens[0  + 4 *(playersTurn-1)].isBlocked){
            if (!listOfTokens[1  + 4 *(playersTurn-1)].isBlocked) lightytighty = 2
            else if(listOfTokens[1  + 4 *(playersTurn-1)].isBlocked){
                if (!listOfTokens[2  + 4 *(playersTurn-1)].isBlocked) lightytighty = 3
            }else lightytighty
        }


        if (key == GLFW.GLFW_KEY_LEFT && action == GLFW.GLFW_PRESS && lightytighty > 1 && !listOfTokens[lightytighty-2  + 4 *(playersTurn-1)].isBlocked) lightytighty -= 1
        else if (key == GLFW.GLFW_KEY_LEFT && action == GLFW.GLFW_PRESS && lightytighty == 1 && !listOfTokens[3  + 4 *(playersTurn-1)].isBlocked) lightytighty = 4

        else if (key == GLFW.GLFW_KEY_LEFT && action == GLFW.GLFW_PRESS && lightytighty >1 && listOfTokens[lightytighty-2  + 4 *(playersTurn-1)].isBlocked) {
            if (lightytighty >2 && !listOfTokens[lightytighty-3  + 4 *(playersTurn-1)].isBlocked) lightytighty -=2
            else if (lightytighty <=2 && !listOfTokens[lightytighty+1  + 4 *(playersTurn-1)].isBlocked) lightytighty +=2
            else if(lightytighty >2 && listOfTokens[lightytighty-3  + 4 *(playersTurn-1)].isBlocked ){
                if (lightytighty == 4 &&!listOfTokens[lightytighty-4  + 4 *(playersTurn-1)].isBlocked) lightytighty -=3
                if (lightytighty == 3 &&!listOfTokens[lightytighty  + 4 *(playersTurn-1)].isBlocked) lightytighty +=1
                else lightytighty

            }else if(lightytighty == 2 && !listOfTokens[lightytighty  + 4 *(playersTurn-1)].isBlocked) lightytighty +=1
            else lightytighty
        }

        else if (key == GLFW.GLFW_KEY_LEFT && action == GLFW.GLFW_PRESS && lightytighty == 1 && listOfTokens[3  + 4 *(playersTurn-1)].isBlocked){
            if (!listOfTokens[2  + 4 *(playersTurn-1)].isBlocked) lightytighty = 3
            else if(listOfTokens[2  + 4 *(playersTurn-1)].isBlocked){
                if (!listOfTokens[1  + 4 *(playersTurn-1)].isBlocked) lightytighty = 2
                else lightytighty
            }
        }

        if (key == GLFW.GLFW_KEY_LEFT && action == GLFW.GLFW_PRESS){
            cameraToken.parent = listOfTokens[lightytighty-1+ 4 *(playersTurn-1)]

            val neuePosi :Vector3f
            if (listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1 < 36 && status != 0){
                if (listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size > 0 ){
                    if (listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens[listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size-1].getWorldPosition() == Vector3f(0.0f,0.0f,0.0f)){
                        neuePosi= listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].getWorldPosition()
                        val lightPosi = pointLightFeld.getWorldPosition()
                        val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.23f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                        pointLightFeld.translateGlobal(mov)
                    }else{
                        neuePosi= listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens[listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size-1].getWorldPosition()
                        val lightPosi = pointLightFeld.getWorldPosition()
                        val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.16f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                        pointLightFeld.translateGlobal(mov)
                    }
                }else {
                    neuePosi = listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].getWorldPosition()
                    val lightPosi = pointLightFeld.getWorldPosition()
                    val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.16f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                    pointLightFeld.translateGlobal(mov)
                }

            }
        }
        if (key == GLFW.GLFW_KEY_RIGHT && action == GLFW.GLFW_PRESS && status != 0){
            cameraToken.parent = listOfTokens[lightytighty-1+ 4 *(playersTurn-1)]

            val neuePosi :Vector3f
            if (listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1 < 36){
                if (listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size > 0 ){
                    if (listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens[listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size-1].getWorldPosition() == Vector3f(0.0f,0.0f,0.0f)){
                        neuePosi= listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].getWorldPosition()
                        val lightPosi = pointLightFeld.getWorldPosition()
                        val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.23f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                        pointLightFeld.translateGlobal(mov)
                    }else{
                        neuePosi= listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens[listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].hexagonListOfTokens.size-1].getWorldPosition()
                        val lightPosi = pointLightFeld.getWorldPosition()
                        val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.16f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                        pointLightFeld.translateGlobal(mov)
                    }

                }else {
                    neuePosi = listOfHexagons[listOfTokens[lightytighty-1+ 4 *(playersTurn-1)].position + diceRoll -1].getWorldPosition()
                    val lightPosi = pointLightFeld.getWorldPosition()
                    val mov =Vector3f((neuePosi.x - lightPosi.x), ((neuePosi.y + 0.16f) - lightPosi.y), (neuePosi.z - lightPosi.z))
                    pointLightFeld.translateGlobal(mov)
                }

            }
        }

        if (key == GLFW.GLFW_KEY_1 && action == GLFW.GLFW_PRESS){
            cameraType = 1
            cameraToken.parent = listOfTokens[lightytighty-1+ 4 *(playersTurn-1)]
            lastPressed = 1
        }

        if (key == GLFW.GLFW_KEY_2 && action == GLFW.GLFW_PRESS){
            cameraType = 2
            lastPressed = 2
        }

        if (key == GLFW.GLFW_KEY_3 && action == GLFW.GLFW_PRESS){
            cameraType = 3
            lastPressed = 3
        }
        if (key == GLFW.GLFW_KEY_R && action == GLFW.GLFW_PRESS){
            if (shaderStatus == 0) {
                staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/cel_frag.glsl")
                shaderStatus +=1
            }
            else {
                staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
                shaderStatus = 0
            }
        }
        if (key == GLFW.GLFW_KEY_T && action == GLFW.GLFW_PRESS){
            if (levels <5f){
                levels +=1f
                staticShader.setUniformf("levels", levels)
            }else{
                levels = 3f
                staticShader.setUniformf("levels", levels)
            }

        }
        if (key == GLFW.GLFW_KEY_F && action == GLFW.GLFW_PRESS && cameraType != 4){
            if (emitSwitch == 0){
                val ground : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj", false, false)
                val groundMeshList: MutableList<OBJLoader.OBJMesh> = ground.objects[0].meshes
                val emit2 = Texture2D("assets/textures/tilemap2.png", true)
                material = Material(diff, emit2, spec, 60.0f, Vector2f(1f))
                groundMesh = Mesh(groundMeshList[0].vertexData, groundMeshList[0].indexData, vertexAttribute, material)
                val renderableListGround : MutableList<Mesh> = mutableListOf()
                renderableListGround.add(groundMesh)
                renderableGround = Renderable(renderableListGround)
                renderableGround.scaleLocal(Vector3f(0.74f,0.5f,0.5f))
                renderableGround.translateGlobal(Vector3f(5f,0f,9f))

                val dice : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/Dice.obj", false, false)
                val diceMeshList : MutableList<OBJLoader.OBJMesh> = dice.objects[0].meshes
                val diceTexture = Texture2D("assets/textures/Dicemap2.png", true)
                diceTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
                diceMaterial = Material(Texture2D("assets/textures/ground_diff.png", false),diceTexture, Texture2D("assets/textures/ground_diff.png",false), 60f, Vector2f(1f))
                diceMesh = Mesh(diceMeshList[0].vertexData, diceMeshList[0].indexData, vertexAttribute, diceMaterial)
                val renderableListDice : MutableList<Mesh> = mutableListOf()
                renderableListDice.add(diceMesh)
                renderableDice = Renderable(renderableListDice)
                renderableDice.scaleLocal(Vector3f(2f))
                renderableDice.translateGlobal(Vector3f(5f, 0.5f,-0.5f))

                pointLightGlobalRO.lightColor = Vector3f(0f)
                pointLightGlobalRU.lightColor = Vector3f(0f)
                pointLightGlobalLO.lightColor = Vector3f(0f)
                pointLightGlobalLU.lightColor = Vector3f(0f)


                emitSwitch = 1
            }
            else if (emitSwitch == 1){
                val ground : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj", false, false)
                val groundMeshList: MutableList<OBJLoader.OBJMesh> = ground.objects[0].meshes
                material = Material(diff, emit, spec, 60.0f, Vector2f(1f))
                groundMesh = Mesh(groundMeshList[0].vertexData, groundMeshList[0].indexData, vertexAttribute, material)
                val renderableListGround : MutableList<Mesh> = mutableListOf()
                renderableListGround.add(groundMesh)
                renderableGround = Renderable(renderableListGround)
                renderableGround.scaleLocal(Vector3f(0.74f,0.5f,0.5f))
                renderableGround.translateGlobal(Vector3f(5f,0f,9f))

                val dice : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/Dice.obj", false, false)
                val diceMeshList : MutableList<OBJLoader.OBJMesh> = dice.objects[0].meshes
                val diceTexture = Texture2D("assets/textures/Dicemap.png", true)
                diceTexture.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
                diceMaterial = Material(Texture2D("assets/textures/ground_diff.png", false),diceTexture, Texture2D("assets/textures/ground_diff.png",false), 60f, Vector2f(1f))
                diceMesh = Mesh(diceMeshList[0].vertexData, diceMeshList[0].indexData, vertexAttribute, diceMaterial)
                val renderableListDice : MutableList<Mesh> = mutableListOf()
                renderableListDice.add(diceMesh)
                renderableDice = Renderable(renderableListDice)
                renderableDice.scaleLocal(Vector3f(2f))
                renderableDice.translateGlobal(Vector3f(5f, 0.5f,-0.5f))

                pointLightGlobalRO.lightColor = Vector3f(4f)
                pointLightGlobalRU.lightColor = Vector3f(4f)
                pointLightGlobalLO.lightColor = Vector3f(4f)
                pointLightGlobalLU.lightColor = Vector3f(4f)

                emitSwitch = 0
            }

        }
    }

    fun onMouseMove(xpos: Double, ypos: Double) {

        if (cameraType == 1){
            if(!firstMouseMove) {
                val bewegung = Vector2f(xpos.toFloat(), ypos.toFloat()).sub(mousePosi)
                cameraToken.rotateAroundPoint(0.0f, bewegung.x * 0.002f, 0.0f, Vector3f(0.0f))
            }
            mousePosi = Vector2f(xpos.toFloat(), ypos.toFloat())
            firstMouseMove = false
        }
        if (cameraType == 3){
            if(!firstMouseMove) {
                val bewegung = Vector2f(xpos.toFloat(), ypos.toFloat()).sub(mousePosi)
                cameraFree.rotateAroundPoint(0.0f, bewegung.x * (-0.002f), 0.0f, cameraFree.getWorldPosition())
            }
            mousePosi = Vector2f(xpos.toFloat(), ypos.toFloat())
            firstMouseMove = false
        }


    }

    private fun diceRoll(): Int {
        return kotlin.random.Random.nextInt(1, 7)
    }

    fun onMouseScroll(xoffset: Double, yoffset: Double){
        fov -= yoffset.toFloat()
        if (fov < 0.1f) fov = 0.1f
        if (fov > 1.57f) fov = 1.57f
    }

    fun cleanup() {}
}