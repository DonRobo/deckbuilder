package at.donrobo

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color.BLACK
import javafx.scene.paint.Color.GREEN

class MagicCardPainter(val magicCard: MagicCard) {

    val width: Double = 672.0
    val height: Double = 936.0

    private val outerArc = 25.0
    private val innerArc = 13.0
    private val innerFrameoffset = 27.0

    fun draw(gc: GraphicsContext) {
        drawFrame(gc)
        drawCardName(gc)
        drawArt(gc)
        drawType(gc)
        drawText(gc)
        drawSetSymbol(gc)
        drawStats(gc)
        drawCost(gc)
        drawBottomBorder(gc)
    }

    private fun drawFrame(gc: GraphicsContext) {
        gc.stroke = BLACK
        gc.fill = BLACK
        gc.fillRoundRect(0.0, 0.0, width, height, outerArc, outerArc)

        gc.fill =
            if (magicCard.colors.singleOrNull() == CardColor.GREEN) GREEN else TODO("Implement color ${magicCard.colors}")
//        gc.fill = when (magicCard.color) {
//            CardColor.GREEN -> GREEN
//            else -> TODO("Implement color ${magicCard.color}")
//        }
        gc.fillRoundRect(
            innerFrameoffset, innerFrameoffset,
            width - innerFrameoffset * 2, height - innerFrameoffset * 2,
            innerArc, innerArc
        )
    }

    private fun drawCardName(gc: GraphicsContext) {

    }

    private fun drawArt(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawType(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawText(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawSetSymbol(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawStats(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawCost(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawBottomBorder(gc: GraphicsContext) {
        TODO("not implemented")
    }

}

