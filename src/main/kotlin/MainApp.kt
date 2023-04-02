import javafx.stage.Stage
import view.styles.Styles
import view.MainView
import tornadofx.App
import tornadofx.launch

class MainApp: App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            width = 1280.0
            height = 720.0
        }
        super.start(stage)
    }
}

fun main() {
    launch<MainApp>()
}