package at.donrobo.view

import at.donrobo.mtg.*
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
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

const val cardSizeRatio = 63.0 / 88.0

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
                colorClass = calculateColor(value.colors)
                if (value.power != null && value.toughness != null) {
                    lblAttackDefense.style = "visibility: visible"
                    lblAttackDefense.text = "${value.power}/${value.toughness}"
                } else if (value.loyalty != null) {
                    lblAttackDefense.style = "visibility: visible"
                    lblAttackDefense.text = value.loyalty
                } else {
                    lblAttackDefense.style = "visibility: hidden"
                }
                hbCost.alignment = Pos.CENTER_RIGHT
                hbCost.children.setAll(value.cost.map {
                    symbol(it)
                })
                cardType = value.typeText
            }
            internalCard = value
        }
        get() = internalCard

    private fun calculateColor(colors: List<CardColor>): String =
        when (colors) {
            emptyList<CardColor>() -> "gray"
            listOf(CardColor.RED) -> "red"
            listOf(CardColor.GREEN) -> "green"
            listOf(CardColor.BLACK) -> "black"
            listOf(CardColor.BLUE) -> "blue"
            listOf(CardColor.WHITE) -> "white"
            listOf(CardColor.BLACK, CardColor.GREEN) -> "blackGreen"
            listOf(CardColor.BLACK, CardColor.RED) -> "blackRed"
            listOf(CardColor.BLACK, CardColor.BLUE) -> "blackBlue"
            listOf(CardColor.BLACK, CardColor.WHITE) -> "blackWhite"
            listOf(CardColor.GREEN, CardColor.RED) -> "greenRed"
            listOf(CardColor.GREEN, CardColor.BLUE) -> "greenBlue"
            listOf(CardColor.GREEN, CardColor.WHITE) -> "greenWhite"
            listOf(CardColor.RED, CardColor.BLUE) -> "redBlue"
            listOf(CardColor.RED, CardColor.WHITE) -> "redWhite"
            listOf(CardColor.BLUE, CardColor.WHITE) -> "blueWhite"
            else -> if (colors.size > 2) "gold" else TODO("Support $colors colors")
        }

    @FXML
    private lateinit var lblCardName: Label
    @FXML
    private lateinit var taCardText: TextFlow
    @FXML
    private lateinit var ivArt: ImageView
    @FXML
    private lateinit var apCardContainer: AnchorPane
    @FXML
    private lateinit var apCardBackground: AnchorPane
    @FXML
    private lateinit var lblAttackDefense: Label
    @FXML
    private lateinit var hbCost: HBox
    @FXML
    private lateinit var lblCardType: Label

    var cardName: String
        set(value) {
            lblCardName.text = value
        }
        get() = lblCardName.text

    var art: Image
        set(value) {
            ivArt.image = value
        }
        get() = ivArt.image

    var cardText: String
        set(value) {
            taCardText.children.clear()
            taCardText.children.addAll(parseText(value))
        }
        get() = taCardText.children.filter { it is Text }.joinToString("") { (it as Text).text }
    var cardType: String
        set(value) {
            lblCardType.text = value
        }
        get() = lblCardType.text

    private fun parseText(value: String): List<Node> {
        fun ArrayList<Node>.addText(str: String?): Boolean {
            if (!str.isNullOrBlank()) {
                add(Text(str))
                return true
            }

            return false
        }

        val segments = ArrayList<Node>()

        var current = 0
        for (matchResult in symbolPattern.findAll(value)) {
            segments.addText(value.substring(current, matchResult.range.first))
            segments += symbol(manaSymbolToCost(matchResult.groupValues[1]))
            current = matchResult.range.last + 1
        }
        segments.addText(value.substring(current))

        return segments
    }

    var colorClass: String
        get() = if (apCardBackground.styleClass.size == 2) apCardBackground.styleClass[1] else ""
        set(value) {
            if (apCardBackground.styleClass.size > 1) {
                apCardBackground.styleClass.remove(1, apCardBackground.styleClass.size)
            }
            apCardBackground.styleClass.add(value)
        }

    private val cardDefaultWidth = 672.0

    companion object {
        fun setUpCardNode(card: MagicCard): Region {
            val loader = FXMLLoader(CardPane::class.java.getResource("/fxCard/card.fxml"))
            val root: Region = loader.load()

            val scaler = Pane(root)
            val controller = loader.getController<CardPane>()
            scaler.prefHeightProperty()
                .bind(scaler.prefWidthProperty().multiply(1.0 / cardSizeRatio))
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