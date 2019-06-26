package at.donrobo.view

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage


class MainUI {
    @FXML
    lateinit var apMain: AnchorPane

    fun close() {
        (apMain.scene.window as Stage).close()
    }
}