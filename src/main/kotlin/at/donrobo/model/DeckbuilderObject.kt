package at.donrobo.model

import at.donrobo.mtg.MagicCard
import at.donrobo.view.DeckbuilderObjectNode
import at.donrobo.view.cardSizeRatio
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.BoundingBox
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

data class CardLocationProperty(
    val xProperty: DoubleProperty, val yProperty: DoubleProperty,
    val widthProperty: DoubleProperty = SimpleDoubleProperty(400.0)
) {
    constructor(x: Double, y: Double, width: Double = 400.0) : this(
        SimpleDoubleProperty(x),
        SimpleDoubleProperty(y),
        SimpleDoubleProperty(width)
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
    val heightProperty: ReadOnlyDoubleProperty =
        SimpleDoubleProperty.readOnlyDoubleProperty(
            SimpleDoubleProperty().apply { bind(widthProperty.divide(cardSizeRatio)) }
        )
    val height: Double
        get() = heightProperty.value

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
}

data class CardDeckbuilderObject(val card: MagicCard) : DeckbuilderObject()

class CollectionDeckbuilderObject() : DeckbuilderObject() {
    constructor(
        initialObjects: List<DeckbuilderObjectNode>,
        cardsSideBySide: Int = max(5, ceil(sqrt(initialObjects.size.toDouble())).toInt())
    ) : this() {
        this.cardsSideBySide = cardsSideBySide
        initialObjects.forEach { addObject(it) }
    }

    private val objectAddedListeners: MutableList<(DeckbuilderObject, CardLocationProperty) -> Unit> = LinkedList()
    private val objectRemovedListeners: MutableList<(DeckbuilderObject) -> Unit> = LinkedList()

    private val internalDeckbuilderObjects: MutableMap<DeckbuilderObject, CardLocationProperty> = HashMap()

    var cardsSideBySide: Int = 5
    var gapSize: Double = 30.0

    val deckbuilderObjects: Map<DeckbuilderObject, CardLocationProperty> get() = internalDeckbuilderObjects

    fun addObject(deckbuilderObject: DeckbuilderObject, cardSize: Double? = null) {
        var maxYInRow = 0.0
        var xIndex = 0

        val locationProperty =
            if (cardSize != null) CardLocationProperty(0.0, 0.0, cardSize) else CardLocationProperty(0.0, 0.0)
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

    fun getObjectsIntersecting(locationProperty: CardLocationProperty): List<Pair<DeckbuilderObject, CardLocationProperty>> =
        deckbuilderObjects.filter { it.value.bounds.intersects(locationProperty.bounds) }.map { it.toPair() }

    fun getObjectsAt(x: Double, y: Double): List<Pair<DeckbuilderObject, CardLocationProperty>> =
        deckbuilderObjects.filter { it.value.bounds.contains(x, y) }.map { it.toPair() }

    fun addObject(deckbuilderObject: DeckbuilderObject, cardLocationProperty: CardLocationProperty) {
        internalDeckbuilderObjects[deckbuilderObject] = cardLocationProperty

        objectAddedListeners.forEach { it(deckbuilderObject, cardLocationProperty) }
    }

    private fun addObject(positionedObject: DeckbuilderObjectNode) {
        addObject(positionedObject.deckbuilderObject, positionedObject.objectLocationProperty)
    }

    fun removeObject(deckbuilderObject: DeckbuilderObject): CardLocationProperty? {
        val returnValue = internalDeckbuilderObjects.remove(deckbuilderObject)

        if (returnValue != null)
            objectRemovedListeners.forEach { it(deckbuilderObject) }

        return returnValue
    }

    fun addObjectAddedListener(listener: (DeckbuilderObject, CardLocationProperty) -> Unit) {
        objectAddedListeners += listener
    }

    fun addObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners += listener
    }

    fun removeObjectAddedListener(listener: (DeckbuilderObject, CardLocationProperty) -> Unit) {
        objectAddedListeners -= listener
    }

    fun removeObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners -= listener
    }

}
