package at.donrobo.view

import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.DeckbuilderObject
import at.donrobo.model.ObjectLocationProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import java.io.IOException

class CardStack(
    val deckbuilderObject: CollectionDeckbuilderObject,
    val objectLocationProperty: ObjectLocationProperty,
    val cardWidth: Double
) : AnchorPane() {

    @FXML
    private lateinit var lblTitle: Label

    @FXML
    private lateinit var spCards: ScrollPane
    @FXML
    private lateinit var fpCards: FlowPane
    private val cardNodes: MutableMap<DeckbuilderObject, Node> = HashMap()

    private var title: String
        get() = lblTitle.text
        set(value) {
            lblTitle.text = value
        }

    private fun updateTitle() {
        title = "${deckbuilderObject.deckbuilderObjects.size} cards in this stack"
    }

    private fun addCard(cardObj: DeckbuilderObject) {
        val location = ObjectLocationProperty(0.0, 0.0, cardWidth, cardWidth / cardSizeRatio)
        val node = createNodeFor(cardObj, location)
        val dndControls = StackDragAndDropControls(this, cardObj, node)
        dndControls.registerEventHandlers()
        cardNodes[cardObj] = node
        fpCards.children.add(node)
    }

    private fun removeCard(obj: DeckbuilderObject) {
        val node = cardNodes.getValue(obj)
        fpCards.children.remove(node)
        cardNodes.remove(obj)
    }

    @FXML
    fun initialize() {
        prefWidthProperty().bindBidirectional(objectLocationProperty.widthProperty)
        prefHeightProperty().bindBidirectional(objectLocationProperty.heightProperty)
        layoutXProperty().bindBidirectional(objectLocationProperty.xProperty)
        layoutYProperty().bindBidirectional(objectLocationProperty.yProperty)

        deckbuilderObject.addObjectAddedListener { newObj, _ ->
            updateTitle()
            addCard(newObj)
        }
        deckbuilderObject.addObjectRemovedListener {
            updateTitle()
            removeCard(it)
        }
        fpCards.prefWidthProperty().bind(spCards.widthProperty())
        deckbuilderObject.deckbuilderObjects.keys.forEach { addCard(it) }
        updateTitle()

        ResizeControls(DeckbuilderObjectNode(deckbuilderObject, objectLocationProperty, this)).registerEventHandlers()
    }

    init {
        val loader = FXMLLoader(this::class.java.getResource("/fxCardStack/card_stack.fxml"))
        loader.setRoot(this)
        loader.setController(this)

        try {
            loader.load<CardStack>()
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }
}