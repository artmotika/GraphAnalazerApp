package view

import controller.VertexController
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import model.Vertex
import tornadofx.text
import tornadofx.times
import utils.ColorGenerator

class VertexView(vertex: Vertex) : Circle() {
    private val controller = VertexController()

    val label = text(vertex.id) {
        visibleProperty().bind(viewProps.vertex.label)
        xProperty().bind(centerXProperty().subtract(layoutBounds.width / 2))
        yProperty().bind(centerYProperty().add(radiusProperty()).add(layoutBounds.height))
    }

    init {
        radiusProperty().bind(viewProps.vertex.size * (1 + vertex.rank * 2))
        centerX = vertex.posX
        centerY = vertex.posY

        fill = ColorGenerator.getColorBy(vertex.communityId)

        stroke = Color.BLACK
        strokeWidth = 0.2

        this.setOnMouseEntered { event -> event?.let { controller.entered(it) } }
        this.setOnMousePressed { event -> event?.let { controller.pressed(it) } }
        this.setOnMouseDragged { event -> event?.let { controller.dragged(it) } }
        this.setOnMouseReleased { event -> event?.let { controller.released(it) } }
        this.setOnMouseExited { event -> event?.let { controller.exited(it) } }
    }
}
