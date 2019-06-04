package at.donrobo

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


class CardShower : Application() {
    val cardLoader = CardLoader("/AllCards.json", Language.ENGLISH, Language.GERMAN)

    override fun start(stage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("/fxCard/card.fxml"))
        val root: Parent = loader.load()

        val controller = loader.getController<CardPane>()

        val myRoot = Group()
        myRoot.children.add(root)
        root.layoutX=30.0
        root.layoutY=100.0
        root.scaleX=0.33
        root.scaleY=0.33

        stage.title = "CardShower"
        stage.scene = Scene(myRoot)
        stage.show()

        controller.card = cardLoader.getCard("Llanowarelfen")
    }
}

fun main(args: Array<String>) {
    Application.launch(CardShower::class.java, *args)
}