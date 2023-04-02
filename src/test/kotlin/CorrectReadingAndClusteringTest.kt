import controller.GraphController
import model.Graph
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import model.inputOutput.csv.GraphCsvReader
import org.junit.jupiter.api.Assertions.assertNotNull
import view.GraphView
import java.io.File
import kotlin.streams.asStream

class CorrectReadingAndClusteringTest {
    private val reader = GraphCsvReader
    private lateinit var graph: Graph
    private lateinit var graphView: GraphView
    private lateinit var controller: GraphController

    @ParameterizedTest(name = "{displayName} <- {arguments}")
    @ArgumentsSource(FilepathProvider::class)
    fun `Correct reading and each node is in different cluster after Leiden with resolution = 1`(
        path: String
    ) {
        graph = reader.readGraph(path)

        assertGraphIsCorrectlyRead(path, graph)

        graphView = GraphView(graph)
        controller = graphView.graphController
        controller.recolorAccordingClustering(resolution = 1.0)

        graph.vertices
            .map { it.communityId }
            .run { assertTrue(this.allElementsAreDifferent()) }
    }

    private fun assertGraphIsCorrectlyRead(
        filepath: String,
        graph: Graph
    ) {
        File(filepath)
            .readLines()
            .forEach { line ->
                val data = line.split(",")
                when (data.size) {
                    EDGE_INPUT_LINE_LENGTH -> assertNotNull(graph.edgesMap[data[0] to data[1]])
                    else -> assertNotNull(graph.verticesMap[data[0]])
                }
            }
    }

    private fun <T> List<T>.allElementsAreDifferent() = this.size == this.toSet().size

    companion object {
        private const val INPUT_FILES_DIRECTORY = "src/test/resources/graphExamples/"

        private val INPUT_FILES = File(INPUT_FILES_DIRECTORY).listFiles() ?: arrayOf<File>()

        object FilepathProvider : ArgumentsProvider {
            override fun provideArguments(context: ExtensionContext?) =
                INPUT_FILES
                    .map { Arguments.of(it.path) }
                    .asSequence()
                    .asStream()
        }
    }
}
