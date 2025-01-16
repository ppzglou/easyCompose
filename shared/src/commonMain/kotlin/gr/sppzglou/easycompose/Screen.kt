package gr.sppzglou.easycompose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize


enum class ScreenOrientation {
    Portrait, Landscape, Undefined
}

expect fun getScreenOrientation(): ScreenOrientation

fun <T> checkOrientation(portraitValue: T, elseValue: T): T =
    if (getScreenOrientation() == ScreenOrientation.Portrait) portraitValue else elseValue

data class ScreenSizes(
    val px: IntSize = IntSize.Zero,
    val dp: IntSize = IntSize.Zero,
    val barsPx: SystemBars = SystemBars(),
    val barsDp: SystemBars = SystemBars(),
    val imePx: Int = 0,
    val imeDp: Int = 0,
)

data class SystemBars(
    val status: Int = 0,
    val nav: Int = 0
)

val LocalScreenSizes = staticCompositionLocalOf { ScreenSizes() }

@Composable
fun InitSizes(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    var sizes by mutableRem(ScreenSizes())
    val bars = WindowInsets.statusBars
    val ime = WindowInsets.ime

    CompositionLocalProvider(
        LocalScreenSizes provides sizes
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .onSizeChanged {
                    sizes = ScreenSizes(
                        px = it,
                        dp = IntSize(it.width.pxToDp.toInt(), it.height.pxToDp.toInt()),
                        barsPx = SystemBars(
                            bars.getTop(density),
                            bars.getBottom(density)
                        ),
                        barsDp = SystemBars(
                            bars.getTop(density).pxToDp.toInt(),
                            bars.getBottom(density).pxToDp.toInt()
                        ),
                        imePx = ime.getBottom(density),
                        imeDp = ime.getBottom(density).pxToDp.toInt()
                    )
                }
        ) {
            content()
        }
    }
}