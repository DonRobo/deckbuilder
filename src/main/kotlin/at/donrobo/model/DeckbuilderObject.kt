package at.donrobo.model

import at.donrobo.MagicCard
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import java.io.Serializable

interface DeckbuilderObject : Serializable

class CardDeckbuilderObject(val card: MagicCard) : DeckbuilderObject

class PositionedObject(val deckbuilderObject: DeckbuilderObject, val container: DeckbuilderContainer) {
    init {
        container.addPositionedObject(this)
    }

    val xProperty: DoubleProperty = SimpleDoubleProperty()
    val yProperty: DoubleProperty = SimpleDoubleProperty()

    var x: Double
        set(value) {
            xProperty.value = value
        }
        get() = xProperty.value

    var y: Double
        set(value) {
            xProperty.value = value
        }
        get() = xProperty.value

}

interface PositionedDeckbuilderContainer {
    val positionedObjects: List<PositionedObject>

    fun addPositionedObject(positionedObject: PositionedObject)
}