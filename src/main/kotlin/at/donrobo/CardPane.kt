package at.donrobo

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class CardPane {

    private var internalCard: MagicCard? = null

    var card: MagicCard?
        set(value) {
            if (value != null) {
                cardName = value.name
                cardText = value.text ?: ""
                artUrl = "https://img.scryfall.com/cards/art_crop/en/xln/216.jpg?1527430784"
            }
            internalCard = value
        }
        get() = internalCard


    @FXML
    private lateinit var lblCardName: Label
    @FXML
    private lateinit var taCardText: TextFlow
    @FXML
    private lateinit var ivArt: ImageView

    var cardName: String
        set(value) {
            lblCardName.text = value
        }
        get() {
            return lblCardName.text
        }

    var artUrl: String
        set(value) {
            ivArt.image = Image(value, true) //TODO cachen
        }
        get() {
            return ivArt.image.url
        }

    var cardText: String
        set(value) {
            taCardText.children.clear()
            taCardText.children.add(Text(value))
        }
        get() {
            return taCardText.children.filter { it is Text }.map { (it as Text).text }.joinToString("")
        }

    companion object{
//        private val cardLoader:CardLoader= CardLoader("/AllCards.json", Language.ENGLISH, Language.GERMAN)

        fun setUpCardNode(card:MagicCard):Node{
            val loader = FXMLLoader(CardPane::class.java.getResource("/fxCard/card.fxml"))
            val root: Parent = loader.load()

            val controller = loader.getController<CardPane>()

            controller.card = card

            return root
        }
    }
}