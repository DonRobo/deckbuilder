package at.donrobo.mtg

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import java.net.URL
import kotlin.random.Random

class CardLoader(
    cardJson: String = "/AllCards.json",
    vararg languages: Language = arrayOf(Language.ENGLISH, Language.GERMAN)
) {

    private val cards: Map<String, List<MagicCard>>

    init {
        if (languages.isEmpty()) throw RuntimeException("I need *some* languages for this to work")

        val mutableCards = HashMap<String, MutableList<MagicCard>>().withDefault { ArrayList() }

        fun MutableMap<String, MutableList<MagicCard>>.addCard(cardName: String, cardLoaderCard: MagicCard) {
            val list = getOrPut(cardName) { ArrayList() }
            list += cardLoaderCard
        }


        val jsonUrl: URL =
            CardLoader::class.java.getResource(cardJson) ?: throw IllegalArgumentException("Can't load $cardJson")

        jsonUrl.openStream().use { inputStream ->
            val gson = Gson()
            val allCardsJson = gson.fromJson<JsonObject>(JsonReader(inputStream.bufferedReader()))
            allCardsJson.forEach { cardName, cardDataElement ->
                val cardData = cardDataElement.obj

                val types = cardData["types"].array.map { CardType.byHumanReadableName(it.string) }
                val colors = cardData["colorIdentity"].array.map { it.string }.map { CardColor.byLetter(it) }
                val cost = parseCost(if (cardData.has("manaCost")) cardData["manaCost"].string else "")
                val power = 0
                val toughness = 0
                val uuid = cardData["uuid"].string

                for (language in languages) {
                    if (language == Language.ENGLISH) {
                        val text = if (cardData.has("text")) cardData["text"].string else null
                        val typeText = if (cardData.has("type")) cardData["type"].string else "N/A"

                        mutableCards.addCard(
                            cardName,
                            MagicCard(
                                originalName = cardName,
                                name = cardName,
                                language = Language.ENGLISH,
                                colors = colors,
                                types = types,
                                text = if (text.isNullOrBlank()) null else text.trim(),
                                typeText = typeText,
                                cost = cost,
                                power = power,
                                toughness = toughness,
                                uuid = uuid
                            )
                        )
                    } else if (cardData.has("foreignData")) {
                        val foreignData =
                            cardData["foreignData"].array.singleOrNull { it["language"].string == language.humanReadableEnglish }
                                ?.obj
                        if (foreignData != null) {
                            val foreignName = foreignData["name"].string
                            val text = if (foreignData.has("text")) foreignData["text"].string else null
                            val typeText = if (foreignData.has("type")) foreignData["type"].string else "N/A"

                            mutableCards.addCard(
                                foreignName,
                                MagicCard(
                                    originalName = cardName,
                                    name = foreignName,
                                    types = types,
                                    language = language,
                                    colors = colors,
                                    text = if (text.isNullOrBlank()) null else text.trim(),
                                    typeText = typeText,
                                    cost = cost,
                                    power = power,
                                    toughness = toughness,
                                    uuid = uuid
                                )
                            )
                        }
                    }
                }
            }
        }

        cards = mutableCards
    }

    private fun parseCost(costString: String): List<Cost> {
        fun manaSymbolToCost(value: String): Cost {
            val generic = value.toIntOrNull()


            return if (generic != null)
                GenericCost(generic)
            else if (value.matches(Regex(".+/.+"))) {
                val costs = value.split("/")

                if (costs.size != 2) {
                    throw RuntimeException("Weird cost: $costs")
                }

                CombinedCost(manaSymbolToCost(costs[0]), manaSymbolToCost(costs[1]))
            } else when (value) {
                "U" -> BlueCost
                "R" -> RedCost
                "G" -> GreenCost
                "B" -> BlackCost
                "W" -> WhiteCost
                "P" -> PhyrexianCost
                "X" -> XCost
                "Y" -> YCost
                "Z" -> ZCost
                "S" -> SnowCost
                "C" -> ColorlessCost
                "HW" -> HalfCost(WhiteCost)
                else -> throw UnsupportedOperationException("Can't parse $value")
            }
        }

        val cost = ArrayList<Cost>()

        val pattern = Regex("\\{([^}]+)}")
        for (match in pattern.findAll(costString)) {
            val value = match.groupValues[1]
            cost += manaSymbolToCost(value)
        }

        return cost
    }


//    private fun calculateColor(colors: List<String>): CardColor {
//        val colorString = colors.joinToString("")
//        return when (colorString) {
//            "G" -> CardColor.GREEN
//            "B" -> CardColor.BLACK
//            "R" -> CardColor.RED
//            "U" -> CardColor.BLUE
//            "W" -> CardColor.WHITE
//            "BG" -> CardColor.BLACK_GREEN
//            "BR" -> CardColor.BLACK_RED
//            "GR" -> CardColor.GREEN_RED
//            "GU" -> CardColor.GREEN_BLUE
//            "GW" -> CardColor.GREEN_WHITE
//            "RU" -> CardColor.RED_BLUE
//            "RW" -> CardColor.RED_WHITE
//            "UW" -> CardColor.BLUE_WHITE
//            "BGRUW", "BGU", "BGW", "BRU", "BGR" -> CardColor.GOLD
//            "" -> CardColor.COLORLESS
//            else -> throw UnsupportedOperationException("Unsupported card color: $colors")
//        }
//    }

    fun getCard(name: String): MagicCard {
        return cards.getValue(name).singleOrNull() ?: throw IllegalArgumentException("Card $name doesn't exist!")
    }

    fun randomCard(): MagicCard {
        val allCards = cards.values.flatten()

        return allCards[Random.nextInt(allCards.size)]
    }
}

