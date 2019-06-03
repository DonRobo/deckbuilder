package at.donrobo

enum class Language(val humanReadableEnglish: String) {

    GERMAN("German"), ENGLISH("English")
}

enum class CardType {
    BASIC_LAND, CREATURE
}

enum class CardColor {
    GREEN
}

data class MagicCard(val name: String, val type: CardType, val language: Language, val color: CardColor)