package at.donrobo.view

import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.createCardObject
import at.donrobo.mtg.CardLoader
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.ScrollPane
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import javafx.stage.Stage

data class CollectionListViewObject(val collectionDeckbuilderObject: CollectionDeckbuilderObject) {
    override fun toString(): String =
        collectionDeckbuilderObject.name
}

class MainUI {

    private val rootDeckbuilderViewProperty = SimpleObjectProperty(DeckbuilderView(CollectionDeckbuilderObject()))
    private var rootDeckbuilderView
        get() = rootDeckbuilderViewProperty.get()
        set(value) {
            rootDeckbuilderViewProperty.set(value)
        }

    @FXML
    lateinit var vbMain: VBox

    @FXML
    lateinit var spMainScroller: ScrollPane

    @FXML
    lateinit var tvCollectionList: TreeView<CollectionListViewObject>

    fun close() {
        (vbMain.scene.window as Stage).close()
    }

    @FXML
    fun initialize() {
        val listener: ChangeListener<DeckbuilderView> = ChangeListener { _, _, newValue ->
            spMainScroller.content = newValue
            addToTreeView(null, newValue.deckbuilderCollection)
        }
        rootDeckbuilderViewProperty.addListener(listener)
        listener.changed(null, null, rootDeckbuilderView)
        addRandomCards()
    }

    fun addToTreeView(parent: TreeItem<CollectionListViewObject>?, cObj: CollectionDeckbuilderObject) {
        val newItem = TreeItem(CollectionListViewObject(cObj))
        cObj.addObjectAddedListener { deckbuilderObject, _ ->
            if (deckbuilderObject is CollectionDeckbuilderObject)
                addToTreeView(newItem, deckbuilderObject)
        }
        cObj.addObjectRemovedListener { deckbuilderObject ->
            if (deckbuilderObject is CollectionDeckbuilderObject)
                removeFromTreeView(newItem, deckbuilderObject)
        }
        if (parent != null)
            parent.children.add(newItem)
        else
            tvCollectionList.root = newItem

    }

    fun removeFromTreeView(parent: TreeItem<CollectionListViewObject>, cObj: CollectionDeckbuilderObject) {
        parent.children.removeIf { it.value.collectionDeckbuilderObject == cObj }
    }

    private fun addRandomCards(count: Int = 30) {
        val cardLoader = CardLoader()
        repeat(count) {
            rootDeckbuilderView.deckbuilderCollection.addObject(cardLoader.randomCard().createCardObject())
        }
    }
}

