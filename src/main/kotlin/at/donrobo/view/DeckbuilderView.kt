package at.donrobo.view

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import at.donrobo.model.ObjectLocationProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

data class DeckbuilderObjectNode(
    val deckbuilderObject: DeckbuilderObject,
    val objectLocationProperty: ObjectLocationProperty,
    val node: Node
)

class DeckbuilderView(val deckbuilderCollection: CollectionDeckbuilderObject) : Pane() {

    private val deckbuilderObjectNodes: MutableList<DeckbuilderObjectNode> = ArrayList()

    init {
        deckbuilderCollection.addObjectAddedListener { deckbuilderObject: DeckbuilderObject, objectLocationProperty: ObjectLocationProperty ->
            val node = createNodeFor(deckbuilderObject, objectLocationProperty)
            children.add(node)
            deckbuilderObjectNodes.add(DeckbuilderObjectNode(deckbuilderObject, objectLocationProperty, node))
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

}

fun createNodeFor(
    deckbuilderObject: DeckbuilderObject,
    objectLocationProperty: ObjectLocationProperty
): Region {
    return when (deckbuilderObject) {
        is CardDeckbuilderObject -> {
            CardPane(deckbuilderObject, objectLocationProperty)
        }
        is CollectionDeckbuilderObject -> {
            CardStack(deckbuilderObject, objectLocationProperty)
        }
    }
}
