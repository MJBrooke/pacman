package objects

import framework.GameObject
import framework.ObjectID
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.util.*

/**
 * Created by michaelbrooke on 2016/04/24.
 */
class Pill(x: Float, y: Float, id: ObjectID) : GameObject(x, y, id){

    val width = 6
    val height = 6
    val rectangle = Rectangle(width, height)

    override fun tick(objects: LinkedList<GameObject>) {

    }

    override fun render(g: Graphics2D) {
        g.color = Color.white
        g.fillRect(x.toInt(), y.toInt(), width, height)
    }

    override fun getBounds(): Rectangle {
        rectangle.x = x.toInt()
        rectangle.y = y.toInt()
        return rectangle
    }
}