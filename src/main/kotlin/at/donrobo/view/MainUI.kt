package at.donrobo.view

import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.createCardObject
import at.donrobo.mtg.CardLoader
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import javafx.stage.Stage

data class CollectionListViewObject(val collectionDeckbuilderObject: CollectionDeckbuilderObject) {
    override fun toString(): String =
        collectionDeckbuilderObject.name
}

class MainUI {

    private val viewedCollectionProperty = SimpleObjectProperty(CollectionDeckbuilderObject())

    private var viewedCollection
        get() = viewedCollectionProperty.get()
        set(value) {
            viewedCollectionProperty.set(value)
        }
    @FXML
    lateinit var vbMain: VBox

    @FXML
    lateinit var spMainScroller: ScrollPane

    @FXML
    lateinit var tvCollectionList: TreeView<CollectionListViewObject>

    @FXML
    lateinit var dvDeckbuilderView: DeckbuilderView

    private val contextMenu = ContextMenu()

    fun close() {
        (vbMain.scene.window as Stage).close()
    }

    @FXML
    fun initialize() {
        addToTreeView(null, dvDeckbuilderView.deckbuilderCollection)

        viewedCollectionProperty.addListener { _, _, newValue ->
            dvDeckbuilderView.deckbuilderCollection = newValue
        }
        tvCollectionList.selectionModel.selectionMode = SelectionMode.SINGLE
        tvCollectionList.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            viewedCollection = newValue.value.collectionDeckbuilderObject
        }

        dvDeckbuilderView.onMousePressed = EventHandler { event ->
            if (event.button == MouseButton.SECONDARY) {
                contextMenu.items.clear()
                dvDeckbuilderView.deckbuilderCollection.actionsFor(event.mousePosition)
                    .forEach { (actionName, actionFunction) ->
                        contextMenu.items.add(MenuItem(actionName).apply {
                            onAction = EventHandler { actionFunction() }
                        })
                    }
                if (contextMenu.items.isNotEmpty()) {
                    contextMenu.show(dvDeckbuilderView, event.screenX, event.screenY)
                    event.consume()
                }
            } else {
                contextMenu.hide()
            }
        }

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
        if (parent != null) {
            parent.children.add(newItem)
            parent.isExpanded = true
        } else {
            tvCollectionList.root = newItem
        }
    }

    fun removeFromTreeView(parent: TreeItem<CollectionListViewObject>, cObj: CollectionDeckbuilderObject) {
        parent.children.removeIf { it.value.collectionDeckbuilderObject == cObj }
    }

    private fun addRandomCards(count: Int = 30) {
        repeat(count) {
            dvDeckbuilderView.deckbuilderCollection.addObject(CardLoader.instance.randomCard().createCardObject())
        }
    }
}

