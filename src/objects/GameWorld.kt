package objects

import framework.obj.GameArray
import framework.factory.BufferedImageLoader
import framework.factory.NodeFactory
import framework.factory.PillFactory
import framework.intf.Renderable
import framework.obj.ObjectHandler
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class GameWorld : Renderable {

    val world = GameArray()
    val handler = ObjectHandler()

    lateinit var levelImg: BufferedImage
    lateinit var pacman: PacMan

    fun tick() {
        handler.tick()
    }

    override fun render(g: Graphics2D) {
        world.render(g)
        handler.render(g)
    }

    fun loadLevelFromImage(path: String){
        getLevelImage(path)
        iterateOverImagePixels()
        addPacMan()
        addGhosts()
    }

    private fun getLevelImage(path: String){
        levelImg = BufferedImageLoader.getImage(path)
    }

    private fun iterateOverImagePixels(){
        for(x in 0..levelImg.width-1){
            for(y in 0..levelImg.height-1){
                val currentPixel = Pixel(levelImg.getRGB(x, y))
                interpretPixel(currentPixel, x, y)
            }
        }
    }

    private fun interpretPixel(pixel: Pixel, x: Int, y: Int){
        updateGameArray(pixel, x, y)
    }

    private fun updateGameArray(pixel: Pixel, x: Int, y: Int){
        val node = when {
            pixel.isWall() -> NodeFactory.getWallNode(x, y)
            else -> NodeFactory.getOpenNode(x, y)
        }

        if(pixel.isPill())
            node.pill = PillFactory.getPill(x, y)

        world.addNode(node)
    }

    private fun addPacMan(){
        pacman = PacMan(14, 23, world, handler)
        handler.addGameObject(pacman)
    }

    private fun addGhosts(){
        val redGhost = RedGhost(13, 14, world, pacman)
        val orangeGhost = OrangeGhost(13, 14, world, pacman)

        handler.addGameObject(redGhost)
        handler.addGameObject(orangeGhost)
    }
}