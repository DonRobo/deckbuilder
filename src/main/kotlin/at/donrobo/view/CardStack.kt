package at.donrobo.view

import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.ObjectLocationProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import java.io.IOException

class CardStack(
    val deckbuilderObject: CollectionDeckbuilderObject,
    val objectLocationProperty: ObjectLocationProperty
) : AnchorPane() {

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

    @FXML
    fun initialize() {
        prefWidthProperty().bindBidirectional(objectLocationProperty.widthProperty)
        prefHeightProperty().bindBidirectional(objectLocationProperty.heightProperty)
        layoutXProperty().bindBidirectional(objectLocationProperty.xProperty)
        layoutYProperty().bindBidirectional(objectLocationProperty.yProperty)

        ResizeControls(DeckbuilderObjectNode(deckbuilderObject, objectLocationProperty, this)).registerEventHandlers()
    }
}