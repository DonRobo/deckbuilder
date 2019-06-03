package at.donrobo

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import java.net.URL

class CardLoader(cardJson: String, vararg languages: Language) {

    private val cards: Map<String, List<MagicCard>>

    init {
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

                for (language in languages) {
                    if (language == Language.ENGLISH) {
                        mutableCards.addCard(
                            cardName,
                            MagicCard(
                                originalName = cardName,
                                name = cardName,
                                language = Language.ENGLISH,
                                colors = colors,
                                types = types
                            )
                        )
                    } else if (cardData.has("foreignData")) {
                        val foreignData =
                            cardData["foreignData"].array.singleOrNull { it["language"].string == language.humanReadableEnglish }
                        if (foreignData != null) {
                            val foreignName = foreignData["name"].string
                            mutableCards.addCard(
                                foreignName,
                                MagicCard(
                                    originalName = cardName,
                                    name = foreignName,
                                    language = language,
                                    colors = colors,
                                    types = types
                                )
                            )
                        }
                    }
                }
            }
        }

        cards = mutableCards
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
}

