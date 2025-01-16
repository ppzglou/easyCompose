package gr.sppzglou.easycompose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class ImageViewState {
    Loading,
    Success,
    Error;
}

@Composable
fun ImageViewLib(
    data: Any?,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale = ContentScale.Crop,
    errorColor: Color = Color.Red,
    loadingColor: Color = Color.Gray,
    errorIcon: DrawableResource? = null
) {
    var state by mutableRem(ImageViewState.Success)

    val painter = when (data) {
        is DrawableResource -> painterResource(data)
        is String -> if (data.isNullOrEmptyOrBlank) {
            null
        } else painterUrl(data) {
            state = it
        }
        else -> null
    }

    Box(modifier) {
        painter?.let {
            Image(
                painter,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
                    .alpha(if (state == ImageViewState.Success) 1f else 0f),
                contentScale = contentScale,
                colorFilter = colorFilter
            )
        } ?: Box(Modifier.fillMaxSize().background(Color.Gray.copy(0.1f)))

        if (state == ImageViewState.Loading) {
            ShimmerEffect(
                Modifier.fillMaxSize(),
                color = loadingColor
            )

        }
        if (state == ImageViewState.Error || painter == null) {
            Box(Modifier.fillMaxSize().background(Color.Gray.copy(0.1f))) {
                errorIcon?.let {
                    Image(
                        painterResource(errorIcon),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.45f)
                            .aspectRatio(1f),
                        colorFilter = ColorFilter.tint(errorColor),
                    )
                }
            }
        }
    }
}

suspend fun fetchImageData(url: String): ByteArray {
    val client = HttpClient()
    return compressAndResizeImage(client.get(url).readBytes())
}

expect fun compressAndResizeImage(
    imageData: ByteArray,
    scaleFactor: Float = 0.2f,
    quality: Float = 0.2f
): ByteArray

@Composable
fun ShimmerEffect(
    modifier: Modifier,
    color: Color = Color.White,
    alpha: Float = 1f,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {

    val shimmerColors = listOf(
        color.copy(alpha = 0f * alpha),
        color.copy(alpha = 0.3f * alpha),
        color.copy(alpha = 0.5f * alpha),
        color.copy(alpha = 1.0f * alpha),
        color.copy(alpha = 0.5f * alpha),
        color.copy(alpha = 0.3f * alpha),
        color.copy(alpha = 0f * alpha)
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }
}


@Composable
fun painterUrl(url: String, state: (ImageViewState) -> Unit = {}): Painter {
    var bytes by mutableRem<ByteArray?>(null)
    val fileName = remember(url) { encodeToBase64(url) }

    val painter = remember(bytes) {
        bytes?.let {
            try {
                val image = it.toImageBitmap()
                BitmapPainter(image)
            } catch (e: Exception) {
                log("ImageView", "Error loading image: $e", LogType.Error)
                state(ImageViewState.Error)
                EmptyPainter()
            }
        } ?: EmptyPainter()
    }

    LaunchedEffect(url) {
        if (url.isNotNullOrEmptyOrBlank) {
            state(ImageViewState.Loading)
            try {
                val localData = withContext(Dispatchers.IO) { readFile(fileName) }
                if (localData == null) {
                    val remoteData = withContext(Dispatchers.IO) { fetchImageData(url) }
                    withContext(Dispatchers.IO) { saveToFile(remoteData, fileName) }
                    bytes = withContext(Dispatchers.IO) { readFile(fileName) }
                } else {
                    bytes = localData
                }
                state(ImageViewState.Success)
            } catch (e: Exception) {
                log("ImageView", "Error loading image: $e", LogType.Error)
                state(ImageViewState.Error)
            }
        } else {
            log("ImageView", "URL is empty", LogType.Error)
            state(ImageViewState.Error)
        }
    }

    return painter
}

class EmptyPainter(override val intrinsicSize: Size = Size.Unspecified) : Painter() {
    override fun DrawScope.onDraw() {
    }
}

expect fun ByteArray.toImageBitmap(): ImageBitmap

expect fun saveToFile(data: ByteArray, fileName: String): String

expect fun readFile(fileName: String): ByteArray?

expect fun encodeToBase64(input: String): String