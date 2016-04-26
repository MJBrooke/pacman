package objects

import framework.*
import window.GAME_WIDTH
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.geom.AffineTransform
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class PacMan(arrayX: Int, arrayY: Int, x: Float, y: Float, id: ObjectID, val array: GameArray) : GameObject(arrayX, arrayY, x, y, id){

    val width = 32
    val height = 32
    val rectangle = Rectangle(width, height)
    val image = ImageIO.read(File("res/Pacman.png"))
    val path = findPill(array, Tuple(arrayX, arrayY), Tuple(16, 2))

    var counter = 0

    init{
        right = true
    }

    override fun tick(objects: LinkedList<GameObject>) {

//        if(x >= (GAME_WIDTH-32) || x <= 0){
//            changeDirection()
//        }
//
//        if(right)
//            velX = 3F
//        else if(left)
//            velX = -3F
//
//        x += velX

        updateGridPosition()
        checkCollision(objects)

        if(counter == 0)
            println(path)

        counter++

    }

    private fun updateGridPosition(){
        arrayX = (x/32).toInt()
        arrayY = (y/32).toInt()
//        println("($arrayX, $arrayY)")
    }

    private fun findPill(graph: GameArray, fromTuple: Tuple, toTuple: Tuple): LinkedList<Node>{

        //println("Start")

        //Function for getting the distance between 2 nodes
        fun getDistanceBetweenNodes(from: Node, to: Node): Float =
                (Math.abs(from.x - to.x) + Math.abs(from.y - to.y)).toFloat()

        fun getNeighbours(node: Node): LinkedList<Node>{

            //println("Inside getNeighbour")
            val neighbours = LinkedList<Node>()

            with(node) {
                //TODO - refactor to use fewer cycles
                for (arrX in (x-1)..(x+1)){
                    for(arrY in (y-1)..(y+1)){
                        //TODO - refactor into a method
                        if(arrX >= 0 && arrX < GameArray.WIDTH){
                            if(arrY >= 0 && arrY < GameArray.HEIGHT){
                                if(arrX != x || arrY != y) {
                                    val neighbourNode = graph.getNode(arrX, arrY)
                                    neighbours.add(neighbourNode)
                                }
                            }
                        }
                    }
                }
            }
            return neighbours
        }

        //Create lists to hold the un/explored game world
        val unexplored = LinkedList<Node>()
        val explored = LinkedList<Node>()

        var goalFound = false

        //Get the goal and start nodes from the game array
        val goalNode = graph.getNode(toTuple)
        val startNode = graph.getNode(fromTuple)

        //Setup the initial node's values for the A* search
//        startNode.costToGetHereSoFarG = 0F
//        startNode.distanceToGoalH = getDistanceToGoal(startNode, goalNode)
        startNode.nodeCostF = 0F

        //The start node is the first node that will be explored
        unexplored.add(startNode)

        //println("Setup finished")

        var currentNode = startNode

        while(!unexplored.isEmpty()){

            //println("Loop cycle")
            //Find the node with the lowest heuristic cost in the open list
            unexplored.forEach {
                if(currentNode.nodeCostF > it.nodeCostF)
                    currentNode = it
            }

            //Remove this node from the list
            unexplored.remove(currentNode)

            //Generate the neighbours of currentNode
            val neighbours = getNeighbours(currentNode)

            for(successor in neighbours){
                println(successor)
                //TODO - possible point of failure - check here if not working correctly
                if(successor.x == goalNode.x && successor.y == goalNode.y) {
                    goalFound = true
                    break
                }

                successor.costToGetHereSoFarG = currentNode.costToGetHereSoFarG + getDistanceBetweenNodes(successor, currentNode)
                successor.distanceToGoalH = getDistanceBetweenNodes(successor, goalNode)
                successor.nodeCostF = successor.costToGetHereSoFarG + successor.distanceToGoalH

                        // && unexplored.get(successor).nodeCostF < successor.nodeCostF)

                if(unexplored.contains(successor)) {
                    val nodeIndex = unexplored.indexOf(successor)
                    val nodeFromList = unexplored[nodeIndex]

                    if(nodeFromList.nodeCostF < successor.nodeCostF)
                        continue
                }

                if(explored.contains(successor)){
                    val nodeIndex = explored.indexOf(successor)
                    val nodeFromList = explored[nodeIndex]

                    if(nodeFromList.nodeCostF < successor.nodeCostF)
                        continue
                }

                unexplored.add(successor)
            }
            if(goalFound)
                break

            explored.add(currentNode)
        }

        //TODO - Make this the proper list
        return explored
    }

    private fun changeDirection(){
        right = !right
        left = !left
    }

    private fun checkCollision(objects: LinkedList<GameObject>){

        var objectToRemove: GameObject? = null

        objects.forEach {
            if(it.id == ObjectID.PILL) {
                if (it.arrayX == arrayX && it.arrayY == arrayY) {
                    objectToRemove = it

                    val pill = it as Pill
                    with(pill) {
                        array.togglePill(arrayX, arrayY)
                    }
                }
            }
        }

        objects.remove(objectToRemove)

//        var objectToRemove: GameObject? = null
//
//        objects.forEach {
//            if(it.id == ObjectID.PILL) {
//                if (getBounds().intersects(it.getBounds())) {
//                    objectToRemove = it
//
//                    val pill = it as Pill
//                    with(pill) {
//                        array.togglePill(arrayX, arrayY)
//                    }
//                }
//            }
//        }
//
//        objects.remove(objectToRemove)
    }

    override fun render(g: Graphics2D) {
        affineTransform = AffineTransform()
        affineTransform.translate(x.toDouble(), y.toDouble())
        affineTransform.scale(1.0, 1.0)
        g.drawImage(image, affineTransform, null)
    }

    override fun getBounds(): Rectangle {
        rectangle.x = x.toInt()
        rectangle.y = y.toInt()
        return rectangle
    }
}