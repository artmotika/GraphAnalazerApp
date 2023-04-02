package view

import javafx.scene.shape.Line

class EdgeView(
    startVertexView: VertexView,
    endVertexView: VertexView
): Line() {
    init {
        strokeWidthProperty().bind(viewProps.edge.width)
        startXProperty().bind(startVertexView.centerXProperty())
        startYProperty().bind(startVertexView.centerYProperty())
        endXProperty().bind(endVertexView.centerXProperty())
        endYProperty().bind(endVertexView.centerYProperty())
    }
}