package model.inputOutput.csv

import EDGE_INPUT_LINE_LENGTH
import InvalidEdgeFormatException
import InvalidGraphFormatException
import InvalidVertexFormatException
import VERTEX_INITIAL_INPUT_LINE_LENGTH
import VERTEX_INPUT_LINE_LENGTH
import model.Graph
import java.io.File
import kotlin.random.Random

object GraphCsvReader {
    fun readGraph(pathname: String): Graph {
        val graph = Graph()
        val (verticesData, edgesData) = getVerticesAndEdgesDataFrom(pathname)
        verticesData.forEach { data ->
            when (data.size) {
                VERTEX_INITIAL_INPUT_LINE_LENGTH -> graph.addVertex(
                    id = data[0],
                    posX = Random.nextDouble(300.0, 900.0),
                    posY = Random.nextDouble(120.0, 600.0)
                )
                VERTEX_INPUT_LINE_LENGTH -> graph.addVertex(
                    id = data[0],
                    posX = data[1].toDouble(),
                    posY = data[2].toDouble(),
                    communityId = data[3].toInt(),
                    rank = data[4].toDouble()
                )
                else -> throw InvalidVertexFormatException
            }
        }
        edgesData.forEach { data ->
            when (data.size) {
                EDGE_INPUT_LINE_LENGTH -> graph.addEdge(
                    start = data[0],
                    end = data[1],
                    weight = data[2].toDouble()
                )
                else -> throw InvalidEdgeFormatException
            }
        }
        return graph
    }

    private fun getVerticesAndEdgesDataFrom(
        pathname: String
    ): Pair<List<List<String>>, List<List<String>>> {
        val data = File(pathname).readLines()
            .map { it.split(",") }
            .groupBy { it.size == 3 }

        data[false]?.let { verticesData ->
            data[true]?.let { edgesData ->
                return verticesData to edgesData
            }
        } ?: throw InvalidGraphFormatException
    }
}