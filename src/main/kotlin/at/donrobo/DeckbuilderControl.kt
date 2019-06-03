package at.donrobo

import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent


class DeckbuilderControl(cardLoader: CardLoader) {

    val canvas = Canvas()
    private val gc get() = canvas.graphicsContext2D
    private val magicCard = MagicCardPainter(cardLoader.getCard("Niv-Mizzet Reborn")) //Wandelnder Wildwuchs Wildgrowth Walker

    init {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED) { event: MouseEvent ->
            draw(event)
        }
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED) { event: MouseEvent ->
            draw(event)
        }
    }

    private fun draw(event: MouseEvent) {
        gc.save()
        val scale = canvas.height / magicCard.height
        gc.scale(scale, scale)
        magicCard.draw(gc)
        gc.restore()
    }

}