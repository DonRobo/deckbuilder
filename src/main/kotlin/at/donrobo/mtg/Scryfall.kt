package at.donrobo.mtg

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mashape.unirest.http.Unirest
import java.util.*

private val scryfallApiBase = "https://api.scryfall.com"
private val gson = Gson()

fun retrieveScryfallCard(card: MagicCard): ScryfallCard = loadCard(card.originalName)

data class ScryfallImageUris(
    val small: String,
    val normal: String,
    @SerializedName("art_crop")
    val artCrop: String
)

data class ScryfallCard(
    val id: UUID,
    val name: String,
    val lang: String,
    @SerializedName("image_uris")
    val imageUris: ScryfallImageUris
)

private fun loadCard(cardname: String): ScryfallCard {
    return gson.fromJson(Unirest.get("$scryfallApiBase/cards/named").queryString("exact", cardname).asString().body)
}

fun main() {
    println(loadCard("Wildgrowth Walker"))
}