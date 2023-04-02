package utils

import javafx.scene.paint.Color
import kotlin.random.Random

object ColorGenerator {
    private val colors = hashMapOf<Int, Color>()

    fun getColorBy(communityId: Int) =
        colors.getOrPut(communityId) { randomRgbColor() }

    private fun randomRgbColor() =
        Color.rgb(
            Random.nextInt(0, 255),
            Random.nextInt(0, 255),
            Random.nextInt(0, 255)
        )
}