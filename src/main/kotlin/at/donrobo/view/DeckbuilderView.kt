package at.donrobo.view

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CardLocationProperty
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

data class DeckbuilderObjectNode(
    val deckbuilderObject: DeckbuilderObject,
    val cardLocationProperty: CardLocationProperty,
    val node: Node
)

class DeckbuilderView(val deckbuilderCollection: CollectionDeckbuilderObject) : Pane() {

    private val deckbuilderObjectNodes: MutableList<DeckbuilderObjectNode> = ArrayList()
    private val deckbuilderObjects: MutableMap<DeckbuilderObject, Region> = HashMap()

    init {
        deckbuilderCollection.addObjectAddedListener { deckbuilderObject: DeckbuilderObject, cardLocationProperty: CardLocationProperty ->
            val node = createNodeFor(deckbuilderObject, cardLocationProperty)
            deckbuilderObjects[deckbuilderObject] = node
            children.add(node)
            deckbuilderObjectNodes.add(DeckbuilderObjectNode(deckbuilderObject, cardLocationProperty, node))
        }

        deckbuilderCollection.addObjectRemovedListener { deckbuilderObject: DeckbuilderObject ->
            val node = deckbuilderObjects.getValue(deckbuilderObject)

            children.remove(node)
            deckbuilderObjects.remove(deckbuilderObject)
        }

        val controls = DragAndDropControls(this, deckbuilderObjectNodes, deckbuilderCollection)
        controls.registerEventHandlers()
    }

    private fun createNodeFor(
        deckbuilderObject: DeckbuilderObject,
        cardLocationProperty: CardLocationProperty
    ): Region {
        return when (deckbuilderObject) {
            is CardDeckbuilderObject -> {
                val node = CardPane.setUpCardNode(deckbuilderObject.card)
                node.layoutXProperty().bindBidirectional(cardLocationProperty.xProperty)
                node.layoutYProperty().bindBidirectional(cardLocationProperty.yProperty)
                node.prefWidthProperty().bind(cardLocationProperty.widthProperty)
                node
            }
            else -> TODO("$deckbuilderObject not implemented")
        }

    }
}