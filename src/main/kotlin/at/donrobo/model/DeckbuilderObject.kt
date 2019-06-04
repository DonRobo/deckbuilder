package at.donrobo.model

import at.donrobo.mtg.MagicCard
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

data class PositionProperty(val xProperty: DoubleProperty, val yProperty: DoubleProperty) {
    constructor(x: Double, y: Double) : this(SimpleDoubleProperty(x), SimpleDoubleProperty(y))

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

class CollectionDeckbuilderObject : DeckbuilderObject() {

    private val objectAddedListeners: MutableList<(DeckbuilderObject, PositionProperty) -> Unit> = LinkedList()
    private val objectRemovedListeners: MutableList<(DeckbuilderObject) -> Unit> = LinkedList()

    private val internalDeckbuilderObjects: MutableMap<DeckbuilderObject, PositionProperty> = HashMap()

    val deckbuilderObjects: Map<DeckbuilderObject, PositionProperty> get() = internalDeckbuilderObjects

    fun addObject(deckbuilderObject: DeckbuilderObject, position: PositionProperty) {
        internalDeckbuilderObjects[deckbuilderObject] = position

        objectAddedListeners.forEach { it(deckbuilderObject, position) }
    }

    fun removeObject(deckbuilderObject: DeckbuilderObject): PositionProperty? {
        val returnValue = internalDeckbuilderObjects.remove(deckbuilderObject)

        if (returnValue != null)
            objectRemovedListeners.forEach { it(deckbuilderObject) }

        return returnValue
    }

    fun addObjectAddedListener(listener: (DeckbuilderObject, PositionProperty) -> Unit) {
        objectAddedListeners += listener
    }

    fun addObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners += listener
    }

    fun removeObjectAddedListener(listener: (DeckbuilderObject, PositionProperty) -> Unit) {
        objectAddedListeners -= listener
    }

    fun removeObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners -= listener
    }

}
