package model.inputOutput.csv

import model.Graph
import model.Vertex
import view.VertexView
import java.io.File

class GraphCsvSaver(
    private val graph: Graph,
    verticesViewMap: Map<Vertex, VertexView>,
) {
    init {
        verticesViewMap.forEach { (vertex, view) ->
            vertex.posX = view.centerX
            vertex.posY = view.centerY
        }
    }

    fun saveByPath(pathname: String) {
        val file = File(pathname)
        val verticesData = graph.vertices
            .joinToString("\n") { it.toString() }
        val edgesData = graph.edges
            .joinToString("\n") { it.toString() }
        file.writeText(verticesData + "\n" + edgesData)
    }
}