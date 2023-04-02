import tornadofx.booleanProperty
import tornadofx.doubleProperty

@Suppress("Classname")
object viewProps {
    object vertex {
        var label = booleanProperty()
        var size = doubleProperty(value = 2.0)
    }
    object edge {
        var width = doubleProperty(value = 0.05)
    }
}