package at.donrobo

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.transform.Scale

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
    @FXML
    private lateinit var apCardContainer: AnchorPane

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

    private val cardRatio = 63.0 / 88.0
    private val cardDefaultWidth = 672.0

    companion object {
        fun setUpCardNode(card: MagicCard): Region {
            val loader = FXMLLoader(CardPane::class.java.getResource("/fxCard/card.fxml"))
            val root: Region = loader.load()

            val scaler = Pane(root)
            val controller = loader.getController<CardPane>()
            scaler.prefHeightProperty()
                .bind(scaler.prefWidthProperty().multiply(1.0 / controller.cardRatio))
            scaler.minWidthProperty().bind(scaler.prefWidthProperty())
            scaler.maxWidthProperty().bind(scaler.prefWidthProperty())
            scaler.minHeightProperty().bind(scaler.prefHeightProperty())
            scaler.maxHeightProperty().bind(scaler.prefHeightProperty())

            val scaleTransform = Scale()

            root.transforms.add(scaleTransform)

            scaleTransform.xProperty().bindBidirectional(scaleTransform.yProperty())
            scaleTransform.xProperty().bind(scaler.widthProperty().divide(controller.cardDefaultWidth))

            controller.card = card

            return scaler
        }
    }
}