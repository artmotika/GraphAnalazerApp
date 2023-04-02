package controller.inputOutput.neo4j

import model.Graph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import java.io.Closeable

class Neo4jDbConnection(uri: String, username: String, password: String) : Closeable {

    private val driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
    private val session = driver.session()

    internal fun replaceAllAndAddGraph(graph: Graph) {
        session.writeTransaction {
            it.run("MATCH (n) DETACH DELETE n")
            for (vertex in graph.vertices) it.run(
                "MERGE (v:Vertex {id: \$id, rank: \$rank})",
                mutableMapOf(
                    "id" to vertex.id,
                    "rank" to vertex.rank
                ) as Map<String, Any>?
            )
            for (edge in graph.edges) it.run(
                "MERGE (v1:Vertex {id: \$id1, rank: \$rank1})" +
                        " MERGE (v2:Vertex {id: \$id2, rank: \$rank2}) MERGE (v1)-[:CONNECTED {weight: \$weight}]-(v2)",
                mutableMapOf(
                    "id1" to edge.start.id,
                    "id2" to edge.end.id,
                    "rank1" to edge.start.rank,
                    "rank2" to edge.end.rank,
                    "weight" to edge.weight
                ) as Map<String, Any>?
            )
        }
    }

    fun startHarmonicCentrality(): MutableMap<String, Double> {
        val ranks = mutableMapOf<String, Double>()
        session.writeTransaction {
            val result =
                it.run(
                    "CALL gds.alpha.closeness.harmonic.stream({\n" +
                            "  nodeProjection: 'Vertex',\n" +
                            "  relationshipProjection: 'CONNECTED'\n" +
                            "})\n" +
                            "YIELD nodeId, centrality\n" +
                            "RETURN gds.util.asNode(nodeId).id AS id, centrality As centrality\n" +
                            "ORDER BY centrality DESC"
                )

            result.forEach { rec ->
                ranks[rec["id"].asString()] = rec["centrality"].asDouble()
            }

        }
        return ranks
    }

    override fun close() {
        session.close()
        driver.close()
    }
}