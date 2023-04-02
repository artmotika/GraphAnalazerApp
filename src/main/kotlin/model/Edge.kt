package model

data class Edge(
    var start: Vertex,
    var end: Vertex,
    var weight: Double
) {
    override fun toString() = "${start.id},${end.id},$weight"
}