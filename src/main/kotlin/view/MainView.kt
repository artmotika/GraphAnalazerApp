package view

import model.algorithms.centrality.Centrality
import controller.GraphController
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import model.algorithms.AlgoProps
import model.Graph
import tornadofx.*
import model.inputOutput.csv.GraphCsvReader
import model.inputOutput.csv.GraphCsvSaver
import model.inputOutput.neo4j.Neo4jDbController
import java.lang.NumberFormatException
import java.util.*

class MainView : View() {
    private val neo4jDbController = Neo4jDbController()
    private lateinit var graph: Graph
    private lateinit var graphController: GraphController
    private lateinit var centrality: Centrality
    private lateinit var graphView: GraphView
    private lateinit var graphCsvSaver: GraphCsvSaver
    private lateinit var layout: GraphController.Animation

    override val root: Parent = borderpane {

        top = menubar {
            style {
                backgroundColor += Color.grayRgb(220)
            }

            Menu("Load").also {
                this.menus.add(it)
                it.items.add(
                    MenuItem("From csv file").apply {
                        setOnAction {
                            val filter = FileChooser.ExtensionFilter("Graph file", "*.csv")
                            val fileChooser = FileChooser().apply { extensionFilters.add(filter) }
                            val chosenFile = fileChooser.showOpenDialog(currentWindow)
                            chosenFile?.path?.let {
                                graph = GraphCsvReader.readGraph(chosenFile.path)
                                graphView = GraphView(graph)
                                center {
                                    add(graphView)
                                }
                            }
                        }
                    }
                )
            }

            Menu("Save").also {
                this.menus.add(it)
                it.items.add(
                    MenuItem("To csv file").apply {
                        setOnAction {
                            val filter = FileChooser.ExtensionFilter("Graph file", "*.csv")
                            val fileChooser = FileChooser().apply { extensionFilters.add(filter) }
                            val chosenFile = fileChooser.showSaveDialog(currentWindow)
                            chosenFile?.path?.let {
                                graphCsvSaver = GraphCsvSaver(graph, graphView.vertices)
                                graphCsvSaver.saveByPath(chosenFile.path)
                            }
                        }
                    }
                )
                it.items.add(
                    MenuItem("To neo4j").apply {
                        setOnAction {
                            neo4jDbController.saveGraph(graph)
                        }
                    }
                )
            }
        }

        left = scrollpane {
            this.style {
                baseColor = Color.grayRgb(220)
            }
            vbox {
                titledpane("Appearance") {
                    expandedProperty().set(false)

                    checkbox("Show labels", viewProps.vertex.label)

                    Label("Vertex size").also {
                        add(it)
                    }

                    slider(0, 10) {
                        showTickLabelsProperty().set(true)
                        setOnMouseDragged { viewProps.vertex.size.set(this.value) }
                    }

                    Label("Edge width").also {
                        add(it)
                    }

                    slider(0, 2) {
                        showTickLabelsProperty().set(true)
                        setOnMouseDragged { viewProps.edge.width.set(this.value) }
                    }
                }

                titledpane("Community detection") {
                    expandedProperty().set(false)

                    Label("Select resolution for Leiden algorithm").also {
                        tooltip("The higher the resolution, the more clusters will be detected")
                        add(it)
                        it.labelFor = this
                    }

                    val resolutionField = TextField().also {
                        it.text = AlgoProps.Leiden.resolution.toString()
                        add(it)
                    }

                    button("Apply settings") {
                        action {
                            try {
                                val resolution = resolutionField.text.toDouble()
                                if (resolution >= 0) {
                                    AlgoProps.Leiden.resolution = resolution
                                } else {
                                    alert(Alert.AlertType.WARNING, "Please enter non-negative number")
                                }
                            } catch (e: NumberFormatException) {
                                alert(Alert.AlertType.WARNING, "Please enter a number")
                            }
                        }
                    }

                    button("Start Leiden") {
                        action {
                            if (this@MainView::graphView.isInitialized) {
                                graphController = graphView.graphController
                                runAsync {
                                    graphController.recolorAccordingClustering(AlgoProps.Leiden.resolution)
                                }
                            }
                        }
                    }
                }

                titledpane("Centrality") {
                    expandedProperty().set(false)
                    button("Start Own Harmonic Centrality") {
                        action {
                            if (this@MainView::graphView.isInitialized) {
                                graphController = graphView.graphController
                                centrality = Centrality()
                                runAsync {
                                    centrality.startCentrality(graph)
                                    graphController.resizeRadius(graphView)
                                }
                            }
                        }
                    }
                    button("Start Neo4j Harmonic Centrality").apply {
                        action {
                            if (this@MainView::graphView.isInitialized) {
                                neo4jDbController.saveGraph(graph)

                                graphController = graphView.graphController
                                runAsync {
                                    neo4jDbController.runHarmonicCentrality(graph)
                                    graphController.resizeRadius(graphView)
                                }
                            }
                        }
                    }
                }

                titledpane("Layout") {
                    expandedProperty().set(false)

                    Label("Set gravity value for ForceAtlas2").also {
                        add(it)
                        it.labelFor = this
                    }

                    val gravityField = TextField().also {
                        it.text = AlgoProps.ForceAtlas2.gravity.toString()
                        add(it)
                    }

                    Label("Set scaling value for ForceAtlas2").also {
                        add(it)
                        it.labelFor = this
                    }
                    val scalingField = TextField().also {
                        it.text = AlgoProps.ForceAtlas2.scaling.toString()
                        add(it)
                    }

                    button("Apply settings") {
                        action {
                            try {
                                val gravity = gravityField.text.toDouble()
                                val scaling = scalingField.text.toDouble()
                                if (gravity >= 0 && scaling >= 0) {
                                    AlgoProps.ForceAtlas2.gravity = gravity
                                    AlgoProps.ForceAtlas2.scaling = scaling
                                } else {
                                    alert(Alert.AlertType.WARNING, "Please enter non-negative number")
                                }
                            } catch (e: NumberFormatException) {
                                alert(Alert.AlertType.WARNING, "Please enter a number")
                            }
                        }
                    }

                    checkbox("Barnes Hut Optimization") {
                        action {
                            AlgoProps.ForceAtlas2.barnesHutOptimization = !AlgoProps.ForceAtlas2.barnesHutOptimization
                        }
                    }

                    checkbox("LinLog Mode") {
                        action {
                            AlgoProps.ForceAtlas2.linLogMode = !AlgoProps.ForceAtlas2.linLogMode
                        }
                    }

                    button("Start ForceAtlas2") {
                        action {
                            if (this@MainView::graphView.isInitialized) {
                                when (this.text) {
                                    "Start ForceAtlas2" -> {
                                        graphController = graphView.graphController
                                        layout = graphController.applyForceAtlas2(AlgoProps)
                                        this.text = "Stop ForceAtlas2"
                                        runAsync {
                                            currentStage.apply {
                                                layout.start()
                                            }
                                        }
                                    }
                                    else -> {
                                        this.text = "Start ForceAtlas2"
                                        currentStage.apply {
                                            layout.stop()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}