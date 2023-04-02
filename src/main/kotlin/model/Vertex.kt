package model

typealias VertexId = String

class Vertex(var id: VertexId) {
    var index = 0

    var degree = 0.0
        private set

    var communityId = 0

    var posX = 0.0

    var posY = 0.0

    var rank = 0.0

    fun incidentEdgeCreated() {
        degree++
    }

    override fun toString() = "$id,$posX,$posY,$communityId,$rank"
}