package view.styles

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.loadFont
import tornadofx.px

class Styles : Stylesheet() {
    init {
        root {
            backgroundColor += Color.grayRgb(230)
            jbMono?.let { font = it }
            fontSize = 13.px
        }
    }

    companion object {
        private val jbMono = loadFont("/fonts/jb-mono-regular.ttf", 13)
    }
}