package gr.sppzglou.easycompose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

fun multiPaths(vararg paths: String): String {
    var path = ""
    paths.forEach { path += it }
    return path
}

data class Shape(
    val path: String,
    val w: Float,
    val h: Float,
    val v: CustomComposeShape = CustomComposeShape(listOf(path), w, h),
    val ratio: Float = w / h
)


class CustomComposeShape(
    private val paths: List<String>,
    private val w: Float,
    private val h: Float
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()

        // Scale factor για να ταιριάξουν όλα τα paths στο μέγεθος του container
        val scaleX = size.width / w
        val scaleY = size.height / h
        val scale = min(scaleX, scaleY)

        paths.forEach { pathString ->
            val parsedPath = PathParser().parsePathString(pathString).toPath()
            path.addPath(parsedPath, Offset(0f, 0f))
        }

        // Scale το συνολικό path
        path.scale(scale)

        return Outline.Generic(path)
    }
}

// Helper extension για scaling του path
fun Path.scale(scale: Float) {
    transform(androidx.compose.ui.graphics.Matrix().apply { scale(scale, scale) })
}