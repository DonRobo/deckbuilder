package at.donrobo.view

import at.donrobo.model.CollectionDeckbuilderObject
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

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
            val mousePosition = Point2D(event.x, event.y)
            val target = objectAt(mousePosition)
            if (target != null) {
                mouseStartPoint = mousePosition
                draggingObjectStartPoint = Point2D(target.cardLocationProperty.x, target.cardLocationProperty.y)
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

            draggingObjectPosition.cardLocationProperty.x = (event.x - mouseStartPoint.x) + draggingObjectStartPoint.x
            draggingObjectPosition.cardLocationProperty.y = (event.y - mouseStartPoint.y) + draggingObjectStartPoint.y
            event.consume()
        }
    }

    private val releasedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        val draggingObject = this.draggingObject
        if (event.button == MouseButton.PRIMARY && draggingObject != null) {
            val droppedOn = objectAt(Point2D(event.x, event.y), except = draggingObject)
            draggedListener.handle(event)
            mouseStartPoint = null
            this.draggingObject = null
            draggingObjectStartPoint = null
            when (val droppedOnDBObject = droppedOn?.deckbuilderObject) {
                is CollectionDeckbuilderObject -> {
                    deckbuilderCollection.removeObject(draggingObject.deckbuilderObject)
                    droppedOnDBObject.addObject(draggingObject.deckbuilderObject)
                }
//                is CardDeckbuilderObject -> {
//                    deckbuilderCollection.removeObject(droppedOn.deckbuilderObject)
//                    deckbuilderCollection.removeObject(draggingObject.deckbuilderObject)
//
//                    val collectionDeckbuilderObject=CollectionDeckbuilderObject()
//                    collectionDeckbuilderObject.addObject(droppedOn.deckbuilderObject)
//                    collectionDeckbuilderObject.addObject(draggingObject.deckbuilderObject)
//
//                    deckbuilderCollection.addObject(collectionDeckbuilderObject, droppedOn.cardLocationProperty)
//                }
            }
            event.consume()
        }
    }

    private fun objectAt(mousePosition: Point2D, except: DeckbuilderObjectNode? = null): DeckbuilderObjectNode? {
        return deckbuilderObjectNodes.filter { it != except && it.cardLocationProperty.bounds.contains(mousePosition) }
            .maxBy { it.node.parent.childrenUnmodifiable.indexOf(it.node) }
    }

    fun registerEventHandlers() {
        region.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedListener)
        region.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedListener)
        region.addEventFilter(MouseEvent.MOUSE_RELEASED, releasedListener)
    }


}