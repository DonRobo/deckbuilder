package at.donrobo.view

import at.donrobo.CardPane
import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import at.donrobo.model.PositionProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

class DeckbuilderView(val deckbuilderCollection: CollectionDeckbuilderObject) : Pane() {

    private val cardSizeProperty = SimpleDoubleProperty(150.0)
    var cardSize: Double
        get() = cardSizeProperty.value
        set(value) {
            cardSizeProperty.value = value
        }

    private val deckbuilderObjects: MutableMap<DeckbuilderObject, Region> = HashMap()

    init {
        deckbuilderCollection.addObjectAddedListener { deckbuilderObject: DeckbuilderObject, positionProperty: PositionProperty ->
            val node = createNodeFor(deckbuilderObject)
            node.layoutXProperty().bindBidirectional(positionProperty.xProperty)
            node.layoutYProperty().bindBidirectional(positionProperty.yProperty)
            node.prefWidthProperty().bind(cardSizeProperty)
            deckbuilderObjects[deckbuilderObject] = node
            children.add(node)
        }

        deckbuilderCollection.addObjectRemovedListener { deckbuilderObject: DeckbuilderObject ->
            val node = deckbuilderObjects.getValue(deckbuilderObject)

            children.remove(node)
            deckbuilderObjects.remove(deckbuilderObject)
        }
    }

    private fun createNodeFor(deckbuilderObject: DeckbuilderObject): Region {
        return when (deckbuilderObject) {
            is CardDeckbuilderObject -> CardPane.setUpCardNode(deckbuilderObject.card)
            else -> TODO("$deckbuilderObject not implemented")
        }

    }
}