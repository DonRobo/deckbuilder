package at.donrobo

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import java.net.URL

private data class CardLoaderCard(val originalName: String)

class CardLoader(cardJson: String, vararg languages: Language) {

    private val cards: Map<String, List<CardLoaderCard>>

    init {
        val mutableCards = HashMap<String, MutableList<CardLoaderCard>>().withDefault { ArrayList() }

        fun MutableMap<String, MutableList<CardLoaderCard>>.addCard(cardName: String, cardLoaderCard: CardLoaderCard) {
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

                for (language in languages) {
                    if (language == Language.ENGLISH) {
                        mutableCards.addCard(cardName, CardLoaderCard(cardName))
                    } else if (cardData.has("foreignData")) {
                        val foreignData =
                            cardData["foreignData"].array.singleOrNull { it["language"].string == language.humanReadableEnglish }
                        if (foreignData != null) {
                            val foreignName = foreignData["name"].string
                            mutableCards.addCard(foreignName, CardLoaderCard(cardName))
                        }
                    }
                }
            }
        }

        cards = mutableCards
    }

    fun loadCard(name: String): MagicCard {
        val foundCard = cards.getValue(name).singleOrNull()

        if (foundCard != null) {
            return MagicCard(foundCard.originalName, CardType.CREATURE, Language.ENGLISH, CardColor.GREEN)
        } else {
            throw IllegalArgumentException("Card $name doesn't exist!")
        }
    }
}

