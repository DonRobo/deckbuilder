package at.donrobo.view

import at.donrobo.model.CollectionDeckbuilderObject
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import java.io.IOException

class CardStack(val deckbuilderObject: CollectionDeckbuilderObject) : Pane() {

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
    lateinit var lblTest: Label

    @FXML
    fun initialize() {
        lblTest.text = "Initialized"
    }
}