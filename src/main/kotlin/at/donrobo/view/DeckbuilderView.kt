package at.donrobo.view

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import at.donrobo.model.ObjectLocationProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region


class DeckbuilderView(deckbuilderCollection: CollectionDeckbuilderObject = CollectionDeckbuilderObject()) : Pane() {

    private data class DeckbuilderObjectNode(
        val deckbuilderObject: DeckbuilderObject,
        val objectLocationProperty: ObjectLocationProperty,
        val node: Node
    )

    private val deckbuilderObjectNodes: MutableList<DeckbuilderObjectNode> = ArrayList()

    val deckbuilderCollectionProperty: SimpleObjectProperty<CollectionDeckbuilderObject> = SimpleObjectProperty()
    var deckbuilderCollection: CollectionDeckbuilderObject
        get() = deckbuilderCollectionProperty.value
        set(value) {
            deckbuilderCollectionProperty.value = value
        }

    private val controls = DragAndDropControls(this)

    init {
        val addedListener = { deckbuilderObject: DeckbuilderObject, objectLocationProperty: ObjectLocationProperty ->
            renderObject(deckbuilderObject, objectLocationProperty)
        }
        val removedListener: (DeckbuilderObject) -> Unit = { deckbuilderObject: DeckbuilderObject ->
            val obj = deckbuilderObjectNodes.single { it.deckbuilderObject == deckbuilderObject }
            val node = obj.node
            children.remove(node)
            deckbuilderObjectNodes.remove(obj)
        }

        deckbuilderCollectionProperty.addListener { _, oldValue: CollectionDeckbuilderObject?, newValue: CollectionDeckbuilderObject ->
            deckbuilderObjectNodes.clear()
            children.clear()
            oldValue?.removeObjectAddedListener(addedListener)
            oldValue?.removeObjectRemovedListener(removedListener)

            newValue.addObjectAddedListener(addedListener)
            newValue.addObjectRemovedListener(removedListener)

            newValue.deckbuilderObjects.forEach { (deckBObj, loc) ->
                renderObject(deckBObj, loc)
            }
        }

        this.deckbuilderCollection = deckbuilderCollection
        controls.registerEventHandlers()
    }

    private fun renderObject(deckbuilderObject: DeckbuilderObject, objectLocationProperty: ObjectLocationProperty) {
        val node = createNodeFor(deckbuilderObject, objectLocationProperty)
        children.add(node)
        node.viewOrderProperty().bind(objectLocationProperty.zIndexProperty)
        deckbuilderObjectNodes.add(DeckbuilderObjectNode(deckbuilderObject, objectLocationProperty, node))
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
            CardStack(deckbuilderObject, objectLocationProperty, 100.0)
        }
    }
}

