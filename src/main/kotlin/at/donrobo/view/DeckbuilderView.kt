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
    val objectLocationProperty: CardLocationProperty,
    val node: Node
)

class DeckbuilderView(val deckbuilderCollection: CollectionDeckbuilderObject) : Pane() {

    private val deckbuilderObjectNodes: MutableList<DeckbuilderObjectNode> = ArrayList()

    init {
        deckbuilderCollection.addObjectAddedListener { deckbuilderObject: DeckbuilderObject, cardLocationProperty: CardLocationProperty ->
            val node = createNodeFor(deckbuilderObject, cardLocationProperty)
            children.add(node)
            deckbuilderObjectNodes.add(DeckbuilderObjectNode(deckbuilderObject, cardLocationProperty, node))
        }

        deckbuilderCollection.addObjectRemovedListener { deckbuilderObject: DeckbuilderObject ->
            val obj = deckbuilderObjectNodes.single { it.deckbuilderObject == deckbuilderObject }
            val node = obj.node

            children.remove(node)
            deckbuilderObjectNodes.remove(obj)
        }

        val controls = DragAndDropControls(this, deckbuilderObjectNodes, deckbuilderCollection)
        controls.registerEventHandlers()
    }

    private fun createNodeFor(
        deckbuilderObject: DeckbuilderObject,
        cardLocationProperty: CardLocationProperty
    ): Region {
        val node = when (deckbuilderObject) {
            is CardDeckbuilderObject -> {
                CardPane.setUpCardNode(deckbuilderObject.card)
            }
            is CollectionDeckbuilderObject -> {
                CardStack(deckbuilderObject) //TODO
            }
        }
        node.layoutXProperty().bindBidirectional(cardLocationProperty.xProperty)
        node.layoutYProperty().bindBidirectional(cardLocationProperty.yProperty)
        node.prefWidthProperty().bind(cardLocationProperty.widthProperty)
        return node
    }
}