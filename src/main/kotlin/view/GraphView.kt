package view

import controller.GraphController
import javafx.scene.layout.Pane
import model.Graph
import tornadofx.add

class GraphView(graph: Graph) : Pane() {
    val vertices by lazy {
        graph.vertices.associateWith { vertex ->
            VertexView(vertex)
        }
    }
    private val edges by lazy {
        graph.edges.associateWith { edge ->
            val startVertexView = vertices.getValue(edge.start)
            val endVertexView = vertices.getValue(edge.end)
            EdgeView(startVertexView, endVertexView)
        }
    }

    val graphController = GraphController(graph, vertices)

    init {
        edges.values.forEach { view ->
            add(view)
        }
        vertices.values.forEach { view ->
            add(view)
            add(view.label)
        }
    }
}