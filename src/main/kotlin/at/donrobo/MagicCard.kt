package at.donrobo

enum class Language(val humanReadableEnglish: String) {

    GERMAN("German"), ENGLISH("English")
}

enum class CardType(val humanReadableName: String) {
    LAND("Land"),
    CREATURE("Creature"),
    ENCHANTMENT("Enchantment"),
    INSTANT("Instant"),
    SORCERY("Sorcery"),
    ARTIFACT("Artifact"),
    TRIBAL("Tribal"),
    PLANE("Plane"),
    HERO("Hero"),
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
            "Scariest", "You’ll", "Ever", "See" -> OTHER
            "instant" -> INSTANT
            "Eaturecray" -> CREATURE
            "Summon" -> CREATURE
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

sealed class Cost
object WhiteCost : Cost()
object GreenCost : Cost()
object BlueCost : Cost()
object BlackCost : Cost()
object RedCost : Cost()
class GenericCost(val number: Int) : Cost()
object XCost : Cost()
object YCost : Cost()
object ZCost : Cost()
object SnowCost : Cost()
object ColorlessCost : Cost()
class CombinedCost(val cost1: Cost, val cost2: Cost) : Cost()
class HalfCost(val cost: Cost) : Cost()
object PhyrexianCost : Cost()

data class MagicCard(
    val originalName: String,
    val name: String,
    val types: List<CardType>,
    val language: Language,
    val colors: List<CardColor>,
    val text: String?,
    val typeText: String,
    val cost: List<Cost>,
    val power: Int?,
    val toughness: Int?
)