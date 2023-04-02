package model.inputOutput.neo4j

import controller.inputOutput.neo4j.Neo4jDbConnection
import model.Graph

class Neo4jDbController {

    private val uri = "bolt://35.153.213.146:7687"
    private val username = "neo4j"
    private val password = "medium-operators-rate"

    internal fun saveGraph(graph: Graph) = Neo4jDbConnection(uri, username, password).use {
        it.replaceAllAndAddGraph(graph)
    }
    internal fun runHarmonicCentrality(graph: Graph) = Neo4jDbConnection(uri, username, password).use {
        val vertices = graph.vertices.toMutableList()
        loop@for ((key, value) in it.startHarmonicCentrality()){
            for (v in vertices) {
                if (v.id == key) {
                    v.rank = value
                    vertices.remove(v)
                    continue@loop
                }
            }
        }
    }

}