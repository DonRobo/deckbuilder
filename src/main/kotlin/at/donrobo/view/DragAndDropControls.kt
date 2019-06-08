package at.donrobo.view

import at.donrobo.model.PositionProperty
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region

class DragAndDropControls(
    val region: Region,
    val bounds: MutableList<PositionedObject>
) {
    private var mouseStartPoint: Point2D? = null
    private var draggingObjectStartPoint: Point2D? = null
    private var draggingObjectPosition: PositionProperty? = null

    private val pressedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mousePosition = Point2D(event.x, event.y)
            val objects = objectsAt(mousePosition)
            if (objects.isNotEmpty()) {
                val target = objects.maxBy { it.node.parent.childrenUnmodifiable.indexOf(it.node) }!!
                mouseStartPoint = mousePosition
                draggingObjectStartPoint = Point2D(target.positionProperty.x, target.positionProperty.y)
                draggingObjectPosition = target.positionProperty
                target.node.toFront()
                event.consume()
            }
        }
    }

    private val draggedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            val mouseStartPoint = this.mouseStartPoint ?: return@EventHandler
            val draggingObjectStartPoint = this.draggingObjectStartPoint ?: return@EventHandler
            val draggingObjectPosition = this.draggingObjectPosition ?: return@EventHandler

            draggingObjectPosition.x = (event.x - mouseStartPoint.x) + draggingObjectStartPoint.x
            draggingObjectPosition.y = (event.y - mouseStartPoint.y) + draggingObjectStartPoint.y
            event.consume()
        }
    }

    private val releasedListener: EventHandler<in MouseEvent> = EventHandler { event ->
        if (event.button == MouseButton.PRIMARY) {
            draggedListener.handle(event)
            mouseStartPoint = null
            draggingObjectPosition = null
            draggingObjectStartPoint = null
            event.consume()
        }
    }

    private fun objectsAt(mousePosition: Point2D): List<PositionedObject> =
        bounds.filter { it.boundsProperty.get().contains(mousePosition) }

    fun registerEventHandlers() {
        region.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedListener)
        region.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedListener)
        region.addEventFilter(MouseEvent.MOUSE_RELEASED, releasedListener)
    }


}