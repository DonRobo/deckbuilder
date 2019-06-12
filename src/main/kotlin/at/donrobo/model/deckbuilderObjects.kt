package at.donrobo.model

import at.donrobo.mtg.MagicCard
import at.donrobo.view.DeckbuilderObjectNode
import at.donrobo.view.cardSizeRatio
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.BoundingBox
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

data class ObjectLocationProperty(
    val xProperty: DoubleProperty, val yProperty: DoubleProperty,
    val widthProperty: DoubleProperty,
    val heightProperty: DoubleProperty
) {
    constructor(x: Double, y: Double, width: Double, height: Double) : this(
        SimpleDoubleProperty(x),
        SimpleDoubleProperty(y),
        SimpleDoubleProperty(width),
        SimpleDoubleProperty(height)
    )

    var x: Double
        get() = xProperty.value
        set(value) {
            xProperty.value = value
        }

    var y: Double
        get() = yProperty.value
        set(value) {
            yProperty.value = value
        }
    var width: Double
        get() = widthProperty.value
        set(value) {
            widthProperty.value = value
        }
    var height: Double
        get() = heightProperty.value
        set(value) {
            heightProperty.value = value
        }

    val bounds: BoundingBox get() = BoundingBox(x, y, width, height)
}

sealed class DeckbuilderObject : Serializable {
    val uuid: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeckbuilderObject) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    abstract val defaultWidth: Double
    abstract val defaultHeight: Double
}

data class CardDeckbuilderObject(
    val card: MagicCard,
    override val defaultWidth: Double = 250.0,
    override val defaultHeight: Double = defaultWidth / cardSizeRatio
) : DeckbuilderObject()

class CollectionDeckbuilderObject(
    override val defaultWidth: Double = 300.0,
    override val defaultHeight: Double = defaultWidth / cardSizeRatio
) : DeckbuilderObject() {
    constructor(
        initialObjects: List<DeckbuilderObjectNode>,
        cardsSideBySide: Int = max(5, ceil(sqrt(initialObjects.size.toDouble())).toInt())
    ) : this() {
        this.cardsSideBySide = cardsSideBySide
        initialObjects.forEach { addObject(it) }
    }

    private val objectAddedListeners: MutableList<(DeckbuilderObject, ObjectLocationProperty) -> Unit> = LinkedList()
    private val objectRemovedListeners: MutableList<(DeckbuilderObject) -> Unit> = LinkedList()

    private val internalDeckbuilderObjects: MutableMap<DeckbuilderObject, ObjectLocationProperty> = HashMap()

    var cardsSideBySide: Int = 5
    var gapSize: Double = 30.0

    val deckbuilderObjects: Map<DeckbuilderObject, ObjectLocationProperty> get() = internalDeckbuilderObjects

    fun addObject(
        deckbuilderObject: DeckbuilderObject,
        widthProperty: DoubleProperty = SimpleDoubleProperty(deckbuilderObject.defaultWidth),
        heightProperty: DoubleProperty = SimpleDoubleProperty(deckbuilderObject.defaultHeight)
    ) {
        var maxYInRow = 0.0
        var xIndex = 0

        val locationProperty =
            ObjectLocationProperty(SimpleDoubleProperty(0.0), SimpleDoubleProperty(0.0), widthProperty, heightProperty)
        var intersections = getObjectsIntersecting(locationProperty)
        while (intersections.isNotEmpty()) {
            maxYInRow = max(maxYInRow, intersections.map { it.second.bounds.maxY }.max()!!)
            if (xIndex + 1 < cardsSideBySide) {
                locationProperty.x = intersections.map { it.second.bounds.maxX }.max()!! + gapSize
                xIndex++
            } else {
                locationProperty.x = 0.0
                locationProperty.y = maxYInRow + gapSize
                xIndex = 0
                maxYInRow = 0.0
            }

            intersections = getObjectsIntersecting(locationProperty)
        }

        addObject(deckbuilderObject, locationProperty)
    }

    fun getObjectsIntersecting(locationProperty: ObjectLocationProperty): List<Pair<DeckbuilderObject, ObjectLocationProperty>> =
        deckbuilderObjects.filter { it.value.bounds.intersects(locationProperty.bounds) }.map { it.toPair() }

    fun getObjectsAt(x: Double, y: Double): List<Pair<DeckbuilderObject, ObjectLocationProperty>> =
        deckbuilderObjects.filter { it.value.bounds.contains(x, y) }.map { it.toPair() }

    fun addObject(deckbuilderObject: DeckbuilderObject, objectLocationProperty: ObjectLocationProperty) {
        internalDeckbuilderObjects[deckbuilderObject] = objectLocationProperty

        objectAddedListeners.forEach { it(deckbuilderObject, objectLocationProperty) }
    }

    fun addObject(deckbuilderObjectNode: DeckbuilderObjectNode) {
        addObject(deckbuilderObjectNode.deckbuilderObject, deckbuilderObjectNode.objectLocationProperty)
    }

    fun removeObject(deckbuilderObject: DeckbuilderObject): ObjectLocationProperty? {
        val returnValue = internalDeckbuilderObjects.remove(deckbuilderObject)

        if (returnValue != null)
            objectRemovedListeners.forEach { it(deckbuilderObject) }

        return returnValue
    }

    fun addObjectAddedListener(listener: (DeckbuilderObject, ObjectLocationProperty) -> Unit) {
        objectAddedListeners += listener
    }

    fun addObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners += listener
    }

    fun removeObjectAddedListener(listener: (DeckbuilderObject, ObjectLocationProperty) -> Unit) {
        objectAddedListeners -= listener
    }

    fun removeObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners -= listener
    }

}
