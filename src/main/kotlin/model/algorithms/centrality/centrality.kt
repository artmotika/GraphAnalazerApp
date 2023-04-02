package model.algorithms.centrality

import model.Graph
import model.Vertex

internal class Centrality {

    private fun dijkstraAlgo(graph: Graph): List<MutableList<Double>> {

        val vertices = graph.vertices

        val distance = List (vertices.size) { MutableList (vertices.size) {Double.POSITIVE_INFINITY} }

        val prevDist = List (vertices.size) { MutableList (vertices.size) {Double.POSITIVE_INFINITY} }

        val indexOfVertex: Map<Vertex, Int> = vertices.mapIndexed { i, v -> v to i }.toMap()

        graph.edges.forEach {
            prevDist[indexOfVertex[it.start]!!][indexOfVertex[it.end]!!] = it.weight
            prevDist[indexOfVertex[it.end]!!][indexOfVertex[it.start]!!] = it.weight
        }

        fun comparePaths(used: BooleanArray, distShortest: DoubleArray, indexV: Int): DoubleArray {
            vertices.indices.forEach { v ->
                if (!used[v] && prevDist[indexV][v] != 0.0 && distShortest[indexV] < Double.POSITIVE_INFINITY &&
                    distShortest[indexV] + prevDist[indexV][v] < distShortest[v]) {
                    distShortest[v] = distShortest[indexV] + prevDist[indexV][v]
                }
            }
            return distShortest
        }

        vertices.indices.forEach { startVertexIndex ->
            val used = BooleanArray(vertices.size) { false }

            var distShortest = DoubleArray(vertices.size) { Double.POSITIVE_INFINITY }

            distShortest[startVertexIndex] = 0.0
            used[startVertexIndex] = true

            distShortest = comparePaths(used, distShortest, startVertexIndex)

            vertices.indices.forEach { _ ->
                var min = Double.POSITIVE_INFINITY
                var minIndex = -1
                vertices.indices.forEach { i ->
                    if (!used[i] && distShortest[i] <= min) {
                        min = distShortest[i]
                        minIndex = i
                    }
                }
                if (minIndex != -1) {
                    used[minIndex] = true
                    distShortest = comparePaths(used, distShortest, minIndex)
                }
            }

            distShortest.indices.forEach { i ->
                distance[startVertexIndex][i] = distShortest[i]
            }
        }
        return distance
    }

    fun startCentrality(graph: Graph) {
        val distance = dijkstraAlgo(graph)

        val vertices = graph.vertices

        vertices.indices.forEach { i ->
            vertices.indices.forEach { j ->
                if (i != j) vertices[i].rank += 1.0/distance[i][j]
            }
            vertices[i].rank /= (vertices.size - 1)
        }
    }
}
