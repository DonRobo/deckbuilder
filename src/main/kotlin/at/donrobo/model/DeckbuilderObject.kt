package at.donrobo.model

import at.donrobo.MagicCard
import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ChangeListener
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

data class PositionProperty(val xProperty: DoubleProperty, val yProperty: DoubleProperty) {
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

class CollectionDeckbuilderObject : DeckbuilderObject(), ReadOnlyProperty<Map<DeckbuilderObject, PositionProperty>> {

    private val invalidationListeners: MutableList<InvalidationListener> = ArrayList()
    private val changeListeners: MutableList<ChangeListener<in Map<DeckbuilderObject, PositionProperty>>> = ArrayList()

    private val internalDeckbuilderObjects: MutableMap<DeckbuilderObject, PositionProperty> = HashMap()

    val deckbuilderObjects: Map<DeckbuilderObject, PositionProperty> get() = internalDeckbuilderObjects

    fun addObject(deckbuilderObject: DeckbuilderObject, position: PositionProperty) {
        val oldValue = internalDeckbuilderObjects.toMap()

        internalDeckbuilderObjects[deckbuilderObject] = position

        changeListeners.forEach { it.changed(this, oldValue, deckbuilderObjects) }
    }

    fun removeObject(deckbuilderObject: DeckbuilderObject): PositionProperty? {
        val oldValue = internalDeckbuilderObjects.toMap()

        val returnValue = internalDeckbuilderObjects.remove(deckbuilderObject)

        changeListeners.forEach { it.changed(this, oldValue, deckbuilderObjects) }

        return returnValue
    }

    override fun removeListener(listener: ChangeListener<in Map<DeckbuilderObject, PositionProperty>>) {
        changeListeners.remove(listener)
    }

    override fun removeListener(listener: InvalidationListener) {
        invalidationListeners.remove(listener)
    }

    override fun getName(): String {
        return "CollectionDeckbuilderObject"
    }

    override fun addListener(listener: ChangeListener<in Map<DeckbuilderObject, PositionProperty>>) {
        changeListeners += listener
    }

    override fun addListener(listener: InvalidationListener) {
        invalidationListeners += listener
    }

    override fun getBean(): Any? {
        return null
    }

    override fun getValue(): Map<DeckbuilderObject, PositionProperty> {
        return deckbuilderObjects
    }
}
