package model.algorithms.layout

import model.Vertex
import view.VertexView
import java.util.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

class Region(val vertices: List<Pair<Vertex, VertexView>>) {
    var mass = 0.0
    var centerX = 0.0
    var centerY = 0.0
    var size = 0.0
    val subRegions = ArrayList<Region>()

    init {
        updateRegion()
    }

    private fun updateRegion() {
        if (vertices.size > 1) {
            mass = 0.0
            var massX = 0.0
            var massY = 0.0

            for (vertex in vertices) {
                mass += vertex.first.degree + 1
                massX += vertex.second.centerX * (vertex.first.degree + 1)
                massY += vertex.second.centerY * (vertex.first.degree + 1)
            }
            centerX = massX / mass
            centerY = massY / mass

            size = 0.0
            for (vertex in vertices) {
                val distance = sqrt((vertex.second.centerX - centerX).pow(2) + (vertex.second.centerY - centerY).pow(2))
                size = maxOf(size, 2 * distance)
            }
        }
    }

    fun buildSubregions() {
        if (vertices.size > 1) {
            val leftVertices = vertices.filter {
                it.second.centerX < centerX
            }

            val rightVertices = vertices.filter {
                it.second.centerX >= centerX
            }

            buildSubregion(leftVertices.filter { it.second.centerY < centerY })
            buildSubregion(leftVertices.filter { it.second.centerY >= centerY })
            buildSubregion(rightVertices.filter { it.second.centerY < centerY })
            buildSubregion(rightVertices.filter { it.second.centerY >= centerY })

            for (subRegion in subRegions)
                subRegion.buildSubregions()
        }
    }

    private fun buildSubregion(subVertices: List<Pair<Vertex, VertexView>>) {
        if (subVertices.isNotEmpty()) {
            if (subVertices.size < vertices.size) {
                subRegions.add(Region(subVertices))
            } else {
                for (vertex in subVertices) {
                    val vertexList = listOf(vertex)
                    subRegions.add(Region(vertexList))
                }
            }
        }
    }
}