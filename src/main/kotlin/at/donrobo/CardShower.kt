package at.donrobo

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


class CardShower : Application() {
    override fun start(stage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxCard/card.fxml"))
        stage.title = "CardShower"
        stage.scene = Scene(root)
        stage.show()
    }

}

fun main(args: Array<String>) {
    Application.launch(CardShower::class.java, *args)

}