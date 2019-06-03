package at.donrobo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage


class Deckbuilder : Application() {

    override fun start(stage: Stage) {
        val deckbuilder = DeckbuilderControl(CardLoader("/AllCards.json",Language.ENGLISH, Language.GERMAN))

        val stackPane = StackPane(deckbuilder.canvas)
        deckbuilder.canvas.widthProperty().bind(stackPane.widthProperty())
        deckbuilder.canvas.heightProperty().bind(stackPane.heightProperty())
        val scene = Scene(stackPane)

        stage.scene = scene
        stage.show()
    }

}

fun main(args: Array<String>) {
    Application.launch(Deckbuilder::class.java, *args)
//    val cardLoader = CardLoader("/AllCards.json",Language.ENGLISH, Language.GERMAN)
//    println(cardLoader.getCard("Wandelnder Wildwuchs"))
}
