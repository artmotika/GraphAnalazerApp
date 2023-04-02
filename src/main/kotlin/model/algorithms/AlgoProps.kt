package model.algorithms

object AlgoProps {
    object ForceAtlas2 {
        var scaling: Double = 1000.0
        var gravity: Double = 1.0
        var linLogMode: Boolean = false
        var barnesHutOptimization: Boolean = false
    }
    object Leiden {
        var resolution: Double = 0.2
    }
}