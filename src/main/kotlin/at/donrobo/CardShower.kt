package at.donrobo

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.PositionProperty
import at.donrobo.mtg.CardLoader
import at.donrobo.mtg.Language
import at.donrobo.view.DeckbuilderView
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class CardShower : Application() {
    val cardLoader = CardLoader("/AllCards.json", Language.ENGLISH, Language.GERMAN)

    override fun start(stage: Stage) {
        val collection = CollectionDeckbuilderObject()
        val myRoot = DeckbuilderView(collection)

        stage.title = "CardShower"
        stage.scene = Scene(myRoot)

        stage.show()

        val position = PositionProperty(100.0, 0.0)
        collection.addObject(CardDeckbuilderObject(cardLoader.getCard("Wildgrowth Walker")), position)
        myRoot.addEventHandler(MouseEvent.MOUSE_CLICKED) {
            if (it.button == MouseButton.SECONDARY) {
                collection.addObject(CardDeckbuilderObject(cardLoader.randomCard()), PositionProperty(it.x, it.y))
            }
        }
        myRoot.addEventHandler(MouseEvent.MOUSE_DRAGGED) {
            if (it.button == MouseButton.SECONDARY) {
                collection.addObject(CardDeckbuilderObject(cardLoader.randomCard()), PositionProperty(it.x, it.y))
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(CardShower::class.java, *args)
}