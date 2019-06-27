package at.donrobo.view

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.mtg.CardLoader
import javafx.fxml.FXML
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import javafx.stage.Stage


class MainUI {

    private var rootDeckbuilderView = DeckbuilderView(CollectionDeckbuilderObject())

    @FXML
    lateinit var vbMain: VBox

    @FXML
    lateinit var spMainScroller: ScrollPane

    fun close() {
        (vbMain.scene.window as Stage).close()
    }

    @FXML
    fun initialize() {
        spMainScroller.content = rootDeckbuilderView
        rootDeckbuilderView.deckbuilderCollection.addObject(CardDeckbuilderObject(CardLoader().randomCard()))
    }
}
