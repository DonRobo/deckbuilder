package at.donrobo

import at.donrobo.model.CardDeckbuilderObject
import at.donrobo.model.CollectionDeckbuilderObject
import at.donrobo.model.PositionProperty
import at.donrobo.view.DeckbuilderView
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Duration

class CardShower : Application() {
    val cardLoader = CardLoader("/AllCards.json", Language.ENGLISH, Language.GERMAN)

    override fun start(stage: Stage) {
        val collection = CollectionDeckbuilderObject()
        val myRoot = DeckbuilderView(collection)

        stage.title = "CardShower"
        stage.scene = Scene(myRoot)

        stage.show()

        val position = PositionProperty(100.0, 0.0)
        collection.addObject(CardDeckbuilderObject(cardLoader.getCard("Wildgrowth Walker")), position)

        val tl = Timeline()
        val kv1 = KeyValue(position.yProperty, 100)
        val kf1 = KeyFrame(Duration.seconds(1.0), kv1)
        val kv2 = KeyValue(position.yProperty, 0)
        val kf2 = KeyFrame(Duration.seconds(2.0), kv2)

        tl.keyFrames.addAll(kf1, kf2)
        tl.cycleCount = Animation.INDEFINITE
        tl.play()
    }
}

fun main(args: Array<String>) {
    Application.launch(CardShower::class.java, *args)
}