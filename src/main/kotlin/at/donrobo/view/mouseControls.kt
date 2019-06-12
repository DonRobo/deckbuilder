package at.donrobo.view

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import at.donrobo.model.ObjectLocationProperty
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

private val MouseEvent.mousePosition: Point2D
    get() = Point2D(this.x, this.y)
private val MouseEvent.sceneMousePosition: Point2D
    get() = Point2D(this.sceneX, this.sceneY)

class ResizeControls(val deckbuilderObjectNode: DeckbuilderObjectNode) {

    private var resizeStartSize: Point2D? = null
    private var mouseStartPoint: Point2D? = null

    private val pressedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        val mousePosition = event.mousePosition
        val distanceFromCorner = mousePosition.distance(
            Point2D(
                deckbuilderObjectNode.objectLocationProperty.width,
                deckbuilderObjectNode.objectLocationProperty.height
            )
        )
        if (event.button == MouseButton.PRIMARY && distanceFromCorner < 20.0) {
            resizeStartSize = Point2D(
                deckbuilderObjectNode.objectLocationProperty.width,
                deckbuilderObjectNode.objectLocationProperty.height
            )
            mouseStartPoint = mousePosition
            deckbuilderObjectNode.node.toFront()
            event.consume()
        }
    }

    private val draggedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mouseStartPoint = this.mouseStartPoint ?: return@EventHandler
            val resizeStartSize = this.resizeStartSize ?: return@EventHandler

            if (!deckbuilderObjectNode.objectLocationProperty.widthProperty.isBound)
                deckbuilderObjectNode.objectLocationProperty.width = resizeStartSize.x + (event.x - mouseStartPoint.x)
            if (!deckbuilderObjectNode.objectLocationProperty.heightProperty.isBound)
                deckbuilderObjectNode.objectLocationProperty.height = resizeStartSize.y + (event.y - mouseStartPoint.y)
            event.consume()
        }
    }

    private val releasedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY && resizeStartSize != null && mouseStartPoint != null) {
            resizeStartSize = null
            mouseStartPoint = null
            event.consume()
        }
    }

    fun registerEventHandlers() {
        deckbuilderObjectNode.node.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedListener)
        deckbuilderObjectNode.node.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedListener)
        deckbuilderObjectNode.node.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedListener)
    }
}

class StackDragAndDropControls(val cardStack: CardStack, val cardObj: DeckbuilderObject, val node: Region) {
    private var startPosition: Point2D? = null
    private var draggingNode: Region? = null

    private val outside: DeckbuilderView get() = cardStack.parent as DeckbuilderView

    private val pressedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            startPosition = event.sceneMousePosition
            draggingNode = null
        }
    }

    private val draggedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        val startPosition = startPosition
        if (event.button == MouseButton.PRIMARY && startPosition != null) {
            if (draggingNode == null) {
                node.isVisible = false
                draggingNode = createNodeFor(cardObj, ObjectLocationProperty(0.0, 0.0, node.width, node.height))
                outside.children.add(draggingNode)
            }
            val pointOutside = outside.screenToLocal(
                event.screenX - node.width / 2.0,
                event.screenY - node.height / 2.0
            )
            draggingNode?.translateX = pointOutside.x
            draggingNode?.translateY = pointOutside.y
        }
    }

    private val releasedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            startPosition = null
            outside.children.remove(draggingNode)
            draggingNode = null
            node.isVisible = true

            val bounds = cardStack.boundsInParent
            val pointOutside = outside.screenToLocal(
                event.screenX,
                event.screenY
            )
            if (!bounds.contains(pointOutside)) {
                cardStack.deckbuilderObject.removeObject(cardObj)
                val location =
                    ObjectLocationProperty(
                        pointOutside.x - cardObj.defaultWidth / 2.0,
                        pointOutside.y - cardObj.defaultHeight / 2.0,
                        cardObj.defaultWidth,
                        cardObj.defaultHeight
                    )
                outside.deckbuilderCollection.addObject(cardObj, location)
            }
        }
    }

    fun registerEventHandlers() {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedListener)
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedListener)
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedListener)
    }

}

class DragAndDropControls(
    val region: Region,
    val deckbuilderObjectNodes: List<DeckbuilderObjectNode>,
    deckbuilderCollection: CollectionDeckbuilderObject
) {
    private var mouseStartPoint: Point2D? = null
    private var draggingObjectStartPoint: Point2D? = null
    private var draggingObject: DeckbuilderObjectNode? = null

    private val pressedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mousePosition = event.mousePosition
            val target = objectAt(mousePosition)
            if (target != null) {
                mouseStartPoint = mousePosition
                draggingObjectStartPoint = Point2D(target.objectLocationProperty.x, target.objectLocationProperty.y)
                draggingObject = target
                target.node.toFront()
                event.consume()
            }
        }
    }

    private val draggedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mouseStartPoint = this.mouseStartPoint ?: return@EventHandler
            val draggingObjectStartPoint = this.draggingObjectStartPoint ?: return@EventHandler
            val draggingObjectPosition = this.draggingObject ?: return@EventHandler

            draggingObjectPosition.objectLocationProperty.x = (event.x - mouseStartPoint.x) + draggingObjectStartPoint.x
            draggingObjectPosition.objectLocationProperty.y = (event.y - mouseStartPoint.y) + draggingObjectStartPoint.y
            event.consume()
        }
    }

    private val releasedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        val draggingObject = this.draggingObject
        if (event.button == MouseButton.PRIMARY && draggingObject != null) {
            val droppedOn = objectAt(event.mousePosition, except = draggingObject)
            mouseStartPoint = null
            this.draggingObject = null
            draggingObjectStartPoint = null
            when (val droppedOnDBObject = droppedOn?.deckbuilderObject) {
                is CollectionDeckbuilderObject -> {
                    if (draggingObject.deckbuilderObject is CardDeckbuilderObject) {
                        deckbuilderCollection.removeObject(draggingObject.deckbuilderObject)
                        droppedOnDBObject.addObject(draggingObject.deckbuilderObject)
                    } else if (draggingObject.deckbuilderObject is CollectionDeckbuilderObject) {
                        deckbuilderCollection.removeObject(draggingObject.deckbuilderObject)
                        draggingObject.deckbuilderObject.deckbuilderObjects.keys.forEach {
                            droppedOnDBObject.addObject(it)
                        }
                    }
                }
                is CardDeckbuilderObject -> {
                    if (draggingObject.deckbuilderObject is CardDeckbuilderObject) {
                        deckbuilderCollection.removeObject(droppedOnDBObject)
                        deckbuilderCollection.removeObject(draggingObject.deckbuilderObject)

                        val collectionDeckbuilderObject = CollectionDeckbuilderObject()
                        collectionDeckbuilderObject.addObject(droppedOnDBObject)
                        collectionDeckbuilderObject.addObject(draggingObject.deckbuilderObject)

                        deckbuilderCollection.addObject(
                            collectionDeckbuilderObject,
                            ObjectLocationProperty(
                                droppedOn.objectLocationProperty.x,
                                droppedOn.objectLocationProperty.y,
                                collectionDeckbuilderObject.defaultWidth,
                                collectionDeckbuilderObject.defaultHeight
                            )
                        )
                    }
                }
            }
            event.consume()
        }
    }

    private fun objectAt(mousePosition: Point2D, except: DeckbuilderObjectNode? = null): DeckbuilderObjectNode? {
        return deckbuilderObjectNodes.filter { it != except && it.objectLocationProperty.bounds.contains(mousePosition) }
            .maxBy { it.node.parent.childrenUnmodifiable.indexOf(it.node) }
    }

    fun registerEventHandlers() {
        region.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedListener)
        region.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedListener)
        region.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedListener)
    }

}