package at.donrobo

enum class Language(val humanReadableEnglish: String) {

    GERMAN("German"), ENGLISH("English")
}

enum class CardType(val humanReadableName: String) {
    LAND("Land"),
    CREATURE("Creature"),
    SUMMON("Summon"),
    ENCHANTMENT("Enchantment"),
    INSTANT("Instant"),
    SORCERY("Sorcery"),
    ARTIFACT("Artifact"),
    TRIBAL("Tribal"),
    PLANE("Plane"),
    HERO("Hero"),
    EATURECRAY("Eaturecray"),
    OTHER("Other"),
    SCHEME("Scheme"),
    CONSPIRACY("Conspiracy"),
    PLANESWALKER("Planeswalker"),
    PHENOMENON("Phenomenon"),
    EMBLEM("Emblem"),
    CARD("Card"),
    VANGUARD("Vanguard");

    companion object {
        fun byHumanReadableName(name: String): CardType = when (name) {
            "Scariest", "You’ll", "Ever","See" -> OTHER
            "instant"->INSTANT
            else -> values().singleOrNull { it.humanReadableName == name }
                ?: throw UnsupportedOperationException("Unsupported card type: $name")
        }
    }
}

enum class CardColor {
    GREEN, BLACK, WHITE, BLUE, RED;
//    ,
//    BLACK_GREEN, BLACK_RED,
//    GREEN_RED, GREEN_BLUE, GREEN_WHITE,
//    BLUE_WHITE,
//    RED_BLUE,RED_WHITE,
//    GOLD,
//    COLORLESS

    companion object {
        fun byLetter(letter: String): CardColor = when (letter) {
            "U" -> BLUE
            "G" -> GREEN
            "W" -> WHITE
            "B" -> BLACK
            "R" -> RED
            else -> throw RuntimeException("What color is $letter?")
        }
    }
}

data class MagicCard(
    val originalName: String,
    val name: String,
    val types: List<CardType>,
    val language: Language,
    val colors: List<CardColor>
)