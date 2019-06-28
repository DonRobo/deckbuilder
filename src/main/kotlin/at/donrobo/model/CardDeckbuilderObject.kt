package at.donrobo.model

import at.donrobo.mtg.MagicCard
import at.donrobo.view.cardSizeRatio

class CardDeckbuilderObject(
    val card: MagicCard,
    override val defaultWidth: Double = 250.0,
    override val defaultHeight: Double = defaultWidth / cardSizeRatio
) : DeckbuilderObject()

fun MagicCard.createCardObject(): CardDeckbuilderObject = CardDeckbuilderObject(this)