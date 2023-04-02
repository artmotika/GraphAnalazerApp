package controller

import LEIDEN_OUTPUT
import LEIDEN_UTILITY
import javafx.animation.AnimationTimer
import model.Graph
import model.Vertex
import model.algorithms.communityDetection.createUtilityFileForLeiden
import tornadofx.Controller
import view.VertexView
import java.io.File
import model.algorithms.layout.ForceAtlas2
import model.algorithms.AlgoProps
import nl.cwts.networkanalysis.run.RunNetworkClustering
import tornadofx.times
import utils.ColorGenerator
import view.GraphView
import kotlin.math.abs

class GraphController(
    val graph: Graph,
    val verticesViewMap: Map<Vertex, VertexView>
) : Controller() {

    inner class Animation(
        private val forceAtlas2: ForceAtlas2
    ) : AnimationTimer() {
        override fun handle(now: Long) {
            forceAtlas2.startLayoutStep()
            val newVerticesViewMap = forceAtlas2.getNewVerticesViewMap()
            for (i in newVerticesViewMap) {
                verticesViewMap[i.key]?.apply {
                    centerX =
                        maxOf(minOf(abs(newVerticesViewMap[i.key]!!.centerX), parent.layoutBounds.width - radius), 7.0)
                    centerY =
                        maxOf(minOf(abs(newVerticesViewMap[i.key]!!.centerY), parent.layoutBounds.height - radius), 7.0)
                }
            }
        }
    }

    fun applyForceAtlas2(settings: AlgoProps): Animation {
        val forceAtlas2 = ForceAtlas2(graph, verticesViewMap)
        return Animation(forceAtlas2)
    }

    fun recolorAccordingClustering(resolution: Double) {
        val verticesId = graph.verticesId
        createUtilityFileForLeiden(graph)
        startCommunityDetection(resolution)
        File(LEIDEN_OUTPUT)
            .readLines()
            .forEach { line ->
                val (id, communityId) = line.split("\t").map { it.toInt() }
                val vertexId = verticesId[id]
                val vertex = graph.verticesMap[vertexId]
                vertex?.communityId = communityId
                verticesViewMap[graph.verticesMap[vertexId]]?.fill =
                    ColorGenerator.getColorBy(communityId)
            }
        deleteUtilityFiles()
    }

    fun resizeRadius(graphView: GraphView) {
        graphView.vertices.forEach { (vertex, vertexView) ->
           vertexView.radiusProperty().unbind()
           vertexView.radiusProperty().bind(viewProps.vertex.size * (1 + vertex.rank * 2))
       }
    }

    private fun startCommunityDetection(resolution: Double) {
        RunNetworkClustering.main(
            arrayOf(
                "-r",
                "$resolution",
                "-o",
                LEIDEN_OUTPUT,
                LEIDEN_UTILITY
            )
        )
    }

    companion object {
        private fun deleteUtilityFiles() {
            File(LEIDEN_UTILITY).delete()
            File(LEIDEN_OUTPUT).delete()
        }
    }
}

