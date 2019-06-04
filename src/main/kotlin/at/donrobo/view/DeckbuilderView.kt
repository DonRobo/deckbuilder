package at.donrobo.view

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import at.donrobo.model.PositionProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

data class PositionedObject(
    val deckbuilderObject: DeckbuilderObject,
    val boundsProperty: ReadOnlyObjectProperty<Bounds>,
    val positionProperty: PositionProperty,
    val node: Node
)

class DeckbuilderView(val deckbuilderCollection: CollectionDeckbuilderObject) : Pane() {

    private val cardSizeProperty = SimpleDoubleProperty(150.0)
    var cardSize: Double
        get() = cardSizeProperty.value
        set(value) {
            cardSizeProperty.value = value
        }

    private val bounds: MutableList<PositionedObject> = ArrayList()
    private val deckbuilderObjects: MutableMap<DeckbuilderObject, Region> = HashMap()

    init {
        deckbuilderCollection.addObjectAddedListener { deckbuilderObject: DeckbuilderObject, positionProperty: PositionProperty ->
            val node = createNodeFor(deckbuilderObject, positionProperty)
            deckbuilderObjects[deckbuilderObject] = node
            children.add(node)
            bounds.add(PositionedObject(deckbuilderObject, node.boundsInParentProperty(), positionProperty, node))
        }

        deckbuilderCollection.addObjectRemovedListener { deckbuilderObject: DeckbuilderObject ->
            val node = deckbuilderObjects.getValue(deckbuilderObject)

            children.remove(node)
            deckbuilderObjects.remove(deckbuilderObject)
        }

        val controls = DragAndDropControls(this, bounds)
        controls.registerEventHandlers()
    }

    private fun createNodeFor(
        deckbuilderObject: DeckbuilderObject,
        positionProperty: PositionProperty
    ): Region {
        return when (deckbuilderObject) {
            is CardDeckbuilderObject -> {
                val node = CardPane.setUpCardNode(deckbuilderObject.card)
                node.layoutXProperty().bindBidirectional(positionProperty.xProperty)
                node.layoutYProperty().bindBidirectional(positionProperty.yProperty)
                node.prefWidthProperty().bind(cardSizeProperty)
                node
            }
            else -> TODO("$deckbuilderObject not implemented")
        }

    }
}