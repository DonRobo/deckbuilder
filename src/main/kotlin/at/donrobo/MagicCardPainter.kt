package at.donrobo

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color.*
import javafx.scene.text.Font
import org.apache.commons.lang.WordUtils

class MagicCardPainter(val card: MagicCard) {

    val width: Double = 672.0
    val height: Double = 936.0

    private val outerArc = 25.0
    private val innerArc = 13.0
    private val innerFrameoffset = 27.0

    private val nameOffset = 57.0
    private val nameFontSize = 40.0

    fun draw(gc: GraphicsContext) {
        drawFrame(gc)
        drawCardName(gc)
//        drawArt(gc)
        drawType(gc)
        drawText(gc)
//        drawSetSymbol(gc)
        drawStats(gc)
        drawCost(gc)
//        drawBottomBorder(gc)
    }

    private fun drawFrame(gc: GraphicsContext) {
        gc.stroke = BLACK
        gc.fill = BLACK
        gc.fillRoundRect(0.0, 0.0, width, height, outerArc, outerArc)

        gc.fill =
            when {
                card.colors.singleOrNull() == CardColor.GREEN -> GREEN
                card.colors.size > 2 -> GOLD
                else -> TODO("Implement color ${card.colors}")
            }
        gc.fillRoundRect(
            innerFrameoffset, innerFrameoffset,
            width - innerFrameoffset * 2, height - innerFrameoffset * 2,
            innerArc, innerArc
        )
        gc.fill = LIGHTGRAY
        gc.fillRoundRect(
            nameOffset - 3.0,
            nameOffset - 3.0,
            width - (nameOffset - 3.0) * 2.0,
            nameFontSize + 6.0,
            5.0,
            5.0
        )

        gc.fill = WHITE
        gc.fillRect(50.0, 590.0, width - 100.0, 864.0 - 590.0)

        gc.fill = LIGHTGRAY
        gc.fillRoundRect(41.0, 527.0, width - 41.0 * 2, 581.0 - 527.0, 5.0, 15.0)

        if (card.types.contains(CardType.CREATURE)) {
            gc.fill = LIGHTGRAY
            gc.fillRect(537.0, 841.0, 623.0 - 537.0, 879.0 - 841.0)
        }
    }

    private fun drawCardName(gc: GraphicsContext) {
        gc.fill = BLACK
        gc.font = Font.font("Arial", nameFontSize)
        gc.fillText(card.name, nameOffset, nameOffset + nameFontSize - 5.0)
    }

    private fun drawArt(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawType(gc: GraphicsContext) {
        gc.fill = BLACK
        gc.font = Font.font("Times New Roman", 30.0)
        gc.fillText(card.typeText, 55.0, 540.0 + 25.0, width - 55.0 * 2)
    }

    private fun drawText(gc: GraphicsContext) {
        if (card.text != null) {
            val textLines = WordUtils.wrap(card.text, 60)

            gc.fill = BLACK
            gc.font = Font.font("Times New Roman", 25.0)
            gc.fillText(textLines, 60.0, 608.0 + 30.0, width - 60.0 * 2)
        }
    }

    private fun drawSetSymbol(gc: GraphicsContext) {
        TODO("not implemented")
    }

    private fun drawStats(gc: GraphicsContext) {
        gc.fill = BLACK
        gc.font = Font.font("Arial", 35.0)

        val attack = card.power
        val defense = card.toughness

        if (attack != null && defense != null)
            gc.fillText("$attack / $defense", 545.0, 873.0)
    }

    private fun drawCost(gc: GraphicsContext) {
        val costSize = 86.0 - 55.0
        val offset = costSize + 1.0
        card.cost.forEachIndexed { index, cost ->
            val x = 612.0 - offset * (card.cost.size - index)
            val y = 62.0

            drawCostSymbol(gc, x, y, costSize, cost)
        }
    }

    private fun drawBottomBorder(gc: GraphicsContext) {
        TODO("not implemented")
    }

}

