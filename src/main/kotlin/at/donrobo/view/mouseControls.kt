package at.donrobo.view

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

class ResizeControls(val node: Region, val locationProperty: ObjectLocationProperty) {

    private var resizeStartSize: Point2D? = null
    private var mouseStartPoint: Point2D? = null

    private val pressedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        val mousePosition = event.mousePosition
        val distanceFromCorner = mousePosition.distance(
            Point2D(
                locationProperty.width,
                locationProperty.height
            )
        )
        if (event.button == MouseButton.PRIMARY && distanceFromCorner < 20.0) {
            resizeStartSize = Point2D(
                locationProperty.width,
                locationProperty.height
            )
            mouseStartPoint = mousePosition
            locationProperty.toFront()
            event.consume()
        }
    }

    private val draggedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mouseStartPoint = this.mouseStartPoint ?: return@EventHandler
            val resizeStartSize = this.resizeStartSize ?: return@EventHandler

            if (!locationProperty.widthProperty.isBound)
                locationProperty.width = resizeStartSize.x + (event.x - mouseStartPoint.x)
            if (!locationProperty.heightProperty.isBound)
                locationProperty.height = resizeStartSize.y + (event.y - mouseStartPoint.y)
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
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedListener)
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedListener)
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedListener)
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
                draggingNode = createNodeFor(cardObj, ObjectLocationProperty(0.0, 0.0, node.width, node.height, -1))
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
                        cardObj.defaultHeight,
                        -1
                    )
                outside.deckbuilderCollection.dropObject(cardObj, location, pointOutside)
            }

            if (cardStack.deckbuilderObject.deckbuilderObjects.isEmpty()) {
                outside.deckbuilderCollection.removeObject(cardStack.deckbuilderObject)
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
    val deckbuilderView: DeckbuilderView
) {
    private val deckbuilderCollection get() = deckbuilderView.deckbuilderCollection
    private var mouseStartPoint: Point2D? = null
    private var draggingObjectStartPoint: Point2D? = null
    private var draggingObject: Pair<DeckbuilderObject, ObjectLocationProperty>? = null

    private val pressedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mousePosition = event.mousePosition
            val target = deckbuilderCollection.objectAt(mousePosition)
            if (target != null) {
                mouseStartPoint = mousePosition
                draggingObjectStartPoint = Point2D(target.second.x, target.second.y)
                draggingObject = target
                target.second.toFront()
                event.consume()
            }
        }
    }

    private val draggedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mouseStartPoint = this.mouseStartPoint ?: return@EventHandler
            val draggingObjectStartPoint = this.draggingObjectStartPoint ?: return@EventHandler
            val draggingObjectPosition = this.draggingObject ?: return@EventHandler

            draggingObjectPosition.second.x = (event.x - mouseStartPoint.x) + draggingObjectStartPoint.x
            draggingObjectPosition.second.y = (event.y - mouseStartPoint.y) + draggingObjectStartPoint.y
            event.consume()
        }
    }

    private val releasedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        val draggingObject = this.draggingObject
        if (event.button == MouseButton.PRIMARY && draggingObject != null) {
            deckbuilderCollection.dropObject(
                draggingObject.first,
                draggingObject.second,
                event.mousePosition
            )
            mouseStartPoint = null
            this.draggingObject = null
            draggingObjectStartPoint = null
            event.consume()
        }
    }


    fun registerEventHandlers() {
        deckbuilderView.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedListener)
        deckbuilderView.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedListener)
        deckbuilderView.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedListener)
    }

}