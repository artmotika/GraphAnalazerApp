package controller

import view.VertexView
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import tornadofx.Controller

class VertexController : Controller() {
    fun entered(event: MouseEvent) {
        event.targetAsVertexView().apply {
            if (!event.isPrimaryButtonDown) scene.cursor = Cursor.HAND
        }
    }

    fun pressed(event: MouseEvent) {
        if (event.isPrimaryButtonDown)
            event.targetAsVertexView().apply { scene.cursor = Cursor.CLOSED_HAND }
        event.consume()
    }

    fun dragged(event: MouseEvent) {
        if (event.isPrimaryButtonDown)
            event.targetAsVertexView().apply {
                centerX = getCoordinateAfterDrag(
                    event.x,
                    0.0,
                    parent.layoutBounds.width,
                    radius
                )
                centerY = getCoordinateAfterDrag(
                    event.y,
                    0.0,
                    parent.layoutBounds.height,
                    radius
                )
            }
        event.consume()
    }

    fun released(event: MouseEvent) {
        event.targetAsVertexView().apply {
            scene.cursor = Cursor.HAND
        }
        event.consume()
    }

    fun exited(event: MouseEvent) {
        if (!event.isPrimaryButtonDown)
            event.targetAsVertexView().apply { scene.cursor = Cursor.DEFAULT }
    }

    private fun MouseEvent.targetAsVertexView(): VertexView {
        require(this.target is VertexView)
        return this.target as VertexView
    }

    private fun getCoordinateAfterDrag(
        value: Double,
        min: Double,
        max: Double,
        radius: Double
    ) = when {
        value < min + radius -> min + radius
        value > max - radius -> max - radius
        else -> value
    }
}