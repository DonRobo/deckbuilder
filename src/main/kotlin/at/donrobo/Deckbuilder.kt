package at.donrobo

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class Deckbuilder : Application() {
    override fun start(stage: Stage) {
        val root: Pane = FXMLLoader(this::class.java.getResource("/mainUI/main.fxml")).load()

        stage.title = "CardShower"
        stage.scene = Scene(root)
        stage.isMaximized = true
        stage.show()
    }

}

fun main(args: Array<String>) {
    Application.launch(Deckbuilder::class.java, *args)
}