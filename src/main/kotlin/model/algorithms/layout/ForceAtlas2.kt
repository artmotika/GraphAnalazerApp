package model.algorithms.layout

import model.algorithms.AlgoProps
import model.Graph
import model.Vertex
import view.VertexView
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.ln

class ForceAtlas2(
    val graph: Graph,
    val verticesViewMap: Map<Vertex, VertexView>,
) {
    private var speedEfficiency: Double = 0.1
    private var globalSpeed: Double = 1.0
    private val antiCollisionCoefficient: Double = 10.0
    private val maxSpeedEfficiency: Double = 0.1
    private val minSpeedEfficiency: Double = 0.05
    private val jitterTolerance: Double = 0.1
    private val barnesHutTheta: Double = 1.3
    private val graphEdges = graph.edges
    private val verticesViewArray: Array<Pair<Vertex, VertexView>> =
        verticesViewMap.map { Pair(it.key, VertexView(it.key)) }.toTypedArray()

    private val verticesViewArrayDelta: Array<Pair<Vertex, VertexView>> =
        verticesViewMap.map { Pair(it.key, VertexView(it.key)) }.toTypedArray()

    private val verticesViewArrayDeltaOld: Array<Pair<Vertex, VertexView>> =
        verticesViewMap.map { Pair(it.key, VertexView(it.key)) }.toTypedArray()

    fun getNewVerticesViewMap(): Map<Vertex, VertexView> {
        return verticesViewArray.map { it.first to it.second }.toMap()
    }

    fun createLayout() {
        for (i in verticesViewArray.indices) {
            verticesViewArray[i].first.index = i
            verticesViewArray[i].second.centerX =
                verticesViewMap[verticesViewArray[i].first]!!.centerX + (0..100).random().toDouble() / 100.0
            verticesViewArray[i].second.centerY =
                verticesViewMap[verticesViewArray[i].first]!!.centerY + (0..100).random().toDouble() / 100.0
        }
    }

    init {
        createLayout()
    }

    private fun attractVertices(vertexAIndex: Int, vertexBIndex: Int, edgeWeight: Double) {
        if (!AlgoProps.ForceAtlas2.linLogMode)
            attractVerticesDefault(vertexAIndex, vertexBIndex, edgeWeight)
        else
            attractVerticesLinLog(vertexAIndex, vertexBIndex, edgeWeight)
    }

    private fun applyRegionRepulsionForce(vertexAIndex: Int, region: Region) {
        if (region.vertices.size < 2) {
            val regionVertex = region.vertices
            for (vertex in regionVertex) {
                repulseVertices(vertexAIndex, vertex.first.index)
            }
        } else {
            val distance = sqrt(
                (verticesViewArray[vertexAIndex].second.centerX - region.centerX).pow(2)
                        + (verticesViewArray[vertexAIndex].second.centerY - region.centerY).pow(2)
            )

            if (distance * barnesHutTheta > region.size) {
                repulseVertexAndRegion(vertexAIndex, region)
            } else {
                for (subRegion in region.subRegions)
                    applyRegionRepulsionForce(vertexAIndex, subRegion)
            }
        }
    }

    private fun repulseVertexAndRegion(vertexAIndex: Int, region: Region) {
        val distX = verticesViewArray[vertexAIndex].second.centerX - region.centerX
        val distY = verticesViewArray[vertexAIndex].second.centerY - region.centerY
        val distance = sqrt(distX.pow(2) + distY.pow(2))
        if (distance > 0.0) {
            val factor =
                -AlgoProps.ForceAtlas2.scaling * (verticesViewArray[vertexAIndex].first.degree + 1) * (region.mass) / distance.pow(
                    2
                )

            verticesViewArrayDelta[vertexAIndex].second.centerX -= distX * factor
            verticesViewArrayDelta[vertexAIndex].second.centerY -= distY * factor
        }
    }

    private fun attractVerticesDefault(vertexAIndex: Int, vertexBIndex: Int, edgeWeight: Double) {
        val distX = verticesViewArray[vertexAIndex].second.centerX - verticesViewArray[vertexBIndex].second.centerX
        val distY = verticesViewArray[vertexAIndex].second.centerY - verticesViewArray[vertexBIndex].second.centerY
        val factor = 1.0 / edgeWeight

        verticesViewArrayDelta[vertexAIndex].second.centerX -= distX * factor / (verticesViewArray[vertexAIndex].first.degree + 1.0)
        verticesViewArrayDelta[vertexAIndex].second.centerY -= distY * factor / (verticesViewArray[vertexAIndex].first.degree + 1.0)
        verticesViewArrayDelta[vertexBIndex].second.centerX += distX * factor / (verticesViewArray[vertexBIndex].first.degree + 1.0)
        verticesViewArrayDelta[vertexBIndex].second.centerY += distY * factor / (verticesViewArray[vertexBIndex].first.degree + 1.0)
    }

    private fun attractVerticesLinLog(vertexAIndex: Int, vertexBIndex: Int, edgeWeight: Double) {
        val distX = verticesViewArray[vertexAIndex].second.centerX - verticesViewArray[vertexBIndex].second.centerX
        val distY = verticesViewArray[vertexAIndex].second.centerY - verticesViewArray[vertexBIndex].second.centerY
        val distance = sqrt(distX.pow(2) + distY.pow(2))
        if (distance > 0.0) {
            val factor = (1.0 / edgeWeight) * ln(distance + 1.0)
            verticesViewArrayDelta[vertexAIndex].second.centerX -= distX * factor / (verticesViewArray[vertexAIndex].first.degree + 1.0)
            verticesViewArrayDelta[vertexAIndex].second.centerY -= distY * factor / (verticesViewArray[vertexAIndex].first.degree + 1.0)
            verticesViewArrayDelta[vertexBIndex].second.centerX += distX * factor / (verticesViewArray[vertexBIndex].first.degree + 1.0)
            verticesViewArrayDelta[vertexBIndex].second.centerY += distY * factor / (verticesViewArray[vertexBIndex].first.degree + 1.0)
        }
    }

    private fun attractVertexToCenter(vertexAIndex: Int) {
        val distX = verticesViewArray[vertexAIndex].second.centerX - (verticesViewMap[verticesViewArray[vertexAIndex].first]!!.parent.layoutBounds.width / 2.0)
        val distY = verticesViewArray[vertexAIndex].second.centerY - (verticesViewMap[verticesViewArray[vertexAIndex].first]!!.parent.layoutBounds.height / 2.0)
        val factor = AlgoProps.ForceAtlas2.gravity * (verticesViewArray[vertexAIndex].first.degree + 1.0)

        verticesViewArrayDelta[vertexAIndex].second.centerX -= distX * factor
        verticesViewArrayDelta[vertexAIndex].second.centerY -= distY * factor
    }

    private fun repulseVertices(vertexAIndex: Int, vertexBIndex: Int) {
        val distX = verticesViewArray[vertexAIndex].second.centerX - verticesViewArray[vertexBIndex].second.centerX
        val distY = verticesViewArray[vertexAIndex].second.centerY - verticesViewArray[vertexBIndex].second.centerY
        val distance = sqrt(distX.pow(2) + distY.pow(2))
        -verticesViewArray[vertexAIndex].second.radius
        -verticesViewArray[vertexBIndex].second.radius

        val factor = if (distance > 0.0)
            -AlgoProps.ForceAtlas2.scaling * (verticesViewArray[vertexAIndex].first.degree + 1.0) * (verticesViewArray[vertexBIndex].first.degree + 1.0) / distance.pow(
                2
            )
        else
            -AlgoProps.ForceAtlas2.scaling * (verticesViewArray[vertexAIndex].first.degree + 1.0) * (verticesViewArray[vertexBIndex].first.degree + 1.0) * antiCollisionCoefficient

        verticesViewArrayDelta[vertexAIndex].second.centerX -= distX * factor
        verticesViewArrayDelta[vertexAIndex].second.centerY -= distY * factor
        verticesViewArrayDelta[vertexBIndex].second.centerX += distX * factor
        verticesViewArrayDelta[vertexBIndex].second.centerY += distY * factor
    }

    fun startLayoutStep() {
        globalSpeed = 1.0
        speedEfficiency = 1.0

        for (i in verticesViewArray.indices) {
            verticesViewArrayDeltaOld[i].second.centerX = verticesViewArrayDelta[i].second.centerX
            verticesViewArrayDeltaOld[i].second.centerY = verticesViewArrayDelta[i].second.centerY
            verticesViewArrayDelta[i].second.centerX = 0.0
            verticesViewArrayDelta[i].second.centerY = 0.0
        }

        for (it in graphEdges) {
            attractVertices(it.start.index, it.end.index, it.weight)
        }

        if (AlgoProps.ForceAtlas2.barnesHutOptimization) {
            val rootRegion = Region(verticesViewArray.toList())
            rootRegion.buildSubregions()
            for (i in verticesViewArray.indices) {
                applyRegionRepulsionForce(i, rootRegion)
                attractVertexToCenter(i)
            }
        } else {
            for (i in verticesViewArray.indices) {
                for (j in 0 until i) {
                    repulseVertices(i, j)

                }
                attractVertexToCenter(i)
            }
        }

        var totalSwinging = 0.0
        var totalEffectiveTraction = 0.0
        for (i in verticesViewArray.indices) {
            val swinging = sqrt(
                (verticesViewArrayDelta[i].second.centerX - verticesViewArrayDeltaOld[i].second.centerX).pow(2) + (verticesViewArrayDelta[i].second.centerY - verticesViewArrayDeltaOld[i].second.centerY).pow(
                    2
                )
            )
            totalSwinging += (verticesViewArrayDelta[i].first.degree + 1.0) * swinging
            totalEffectiveTraction += (verticesViewArrayDelta[i].first.degree + 1.0) * 0.5 * sqrt(
                (verticesViewArrayDelta[i].second.centerX + verticesViewArrayDeltaOld[i].second.centerX).pow(
                    2
                ) + (verticesViewArrayDelta[i].second.centerY + verticesViewArrayDeltaOld[i].second.centerY).pow(2)
            )
        }
        val estimatedOptimalJitterTolerance = 0.05 * sqrt(verticesViewArray.size.toDouble())
        val minJT = sqrt(estimatedOptimalJitterTolerance)
        val maxJT = 10.0
        var jt = jitterTolerance * maxOf(
            minJT,
            minOf(
                maxJT,
                estimatedOptimalJitterTolerance * totalEffectiveTraction / verticesViewArray.size.toDouble().pow(2)
            )
        )
        if (totalSwinging / totalEffectiveTraction > 2.0) {
            if (speedEfficiency > minSpeedEfficiency) speedEfficiency *= 0.5
            jt = maxOf(jt, jitterTolerance)
        }
        val speed = jt * speedEfficiency * totalEffectiveTraction / totalSwinging
        if (totalSwinging > jt * totalEffectiveTraction) {
            if (speedEfficiency > minSpeedEfficiency) speedEfficiency *= 0.7
            else if (globalSpeed < 1000.0) speedEfficiency *= 1.3
        }
        speedEfficiency = maxOf(maxSpeedEfficiency, speedEfficiency)
        val maxRise = 0.5
        globalSpeed += minOf(speed - globalSpeed, maxRise * globalSpeed)
        for (i in verticesViewArray.indices) {
            val swinging =
                (verticesViewArray[i].first.degree + 1.0) * sqrt(
                    (verticesViewArrayDelta[i].second.centerX - verticesViewArrayDeltaOld[i].second.centerX).pow(
                        2
                    ) + (verticesViewArrayDelta[i].second.centerY - verticesViewArrayDeltaOld[i].second.centerY).pow(2)
                )
            var factor = 0.1 * globalSpeed / (1.0 + sqrt(globalSpeed * swinging))
            val df = sqrt(
                (verticesViewArrayDelta[i].second.centerX).pow(2) + (verticesViewArrayDelta[i].second.centerY).pow(2)
            )
            factor = minOf(factor * df, 10.0) / df

            verticesViewArray[i].second.centerX += verticesViewArrayDelta[i].second.centerX * factor
            verticesViewArray[i].second.centerY += verticesViewArrayDelta[i].second.centerY * factor
        }
    }
}