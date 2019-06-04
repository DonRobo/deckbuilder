package at.donrobo.view

import at.donrobo.mtg.MagicCard
import at.donrobo.mtg.retrieveScryfallCard
import javafx.concurrent.Task
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
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URL
import java.util.concurrent.Executors

private val executor = Executors.newSingleThreadExecutor { r: Runnable -> Thread(r).apply { isDaemon = true } }

class CardPane {

    private var internalCard: MagicCard? = null
    private val artCache = File("artCache")

    private fun setArt(value: MagicCard) {
        val task: Task<Image> = object : Task<Image>() {
            override fun call(): Image {
                val cacheFile = File(artCache, "${value.uuid}.png")
                if (!cacheFile.exists()) {
                    val scryfall = retrieveScryfallCard(value)
                    val artUrl = URL(scryfall.imageUris.artCrop)
                    cacheFile.outputStream().use { ous ->
                        artUrl.openStream().use { ins ->
                            IOUtils.copy(ins, ous)
                        }
                    }
                }
                return Image(cacheFile.toURI().toString())
            }
        }
        executor.submit(task)
        task.setOnSucceeded { art = task.value }
    }

    var card: MagicCard?
        set(value) {
            if (value != null) {
                cardName = value.name
                cardText = value.text ?: ""
                setArt(value)
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

    var art: Image
        set(value) {
            ivArt.image = value
        }
        get() {
            return ivArt.image
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