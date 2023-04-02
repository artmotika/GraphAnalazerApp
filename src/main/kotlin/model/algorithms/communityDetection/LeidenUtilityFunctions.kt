package model.algorithms.communityDetection

import LEIDEN_UTILITY
import model.Graph
import java.io.File

fun createUtilityFileForLeiden(graph: Graph) {
    val verticesMap = graph.verticesId.let { it.zip(it.indices) }.toMap()
    val edges = graph.edgesMap.keys.toList()
    File(LEIDEN_UTILITY)
        .writeText(
            edges.joinToString("\n") { (start, end) ->
                listOf(
                    verticesMap[start],
                    verticesMap[end],
                    graph.getWeight(start, end)
                ).joinToString("\t")
            }
        )
}