package at.donrobo

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.mtg.CardLoader
import at.donrobo.mtg.Language
import at.donrobo.view.DeckbuilderView
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class CardShower : Application() {
    val cardLoader = CardLoader("/AllCards.json", Language.ENGLISH, Language.GERMAN)

    override fun start(stage: Stage) {
        val collection = CollectionDeckbuilderObject()
        val myRoot = DeckbuilderView(collection)
        val scrollPane = ScrollPane(myRoot)
        scrollPane.isPannable = true

        stage.title = "CardShower"
        stage.scene = Scene(scrollPane)
        stage.width = 1000.0
        stage.height = 800.0

        stage.show()

        collection.addObject(CardDeckbuilderObject(cardLoader.getCard("Wildgrowth Walker")))
        myRoot.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            if (it.button == MouseButton.SECONDARY) {
                collection.addObject(CardDeckbuilderObject(cardLoader.randomCard()))
                it.consume()
            }
        }
//        myRoot.addEventHandler(MouseEvent.MOUSE_DRAGGED) {
//            if (it.button == MouseButton.SECONDARY) {
//                collection.addObject(CardDeckbuilderObject(cardLoader.randomCard()))
//            }
//        }
    }
}

fun main(args: Array<String>) {
    Application.launch(CardShower::class.java, *args)
}