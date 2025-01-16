package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity


val density
    @Composable
    get() = LocalDensity.current.density

expect fun getScreenDensity(): Float

val toast
    @Composable
    get() = LocalToastText.current


val screenWidthPx = getScreenWidthPx()
val screenWidthDp = getScreenWidthPx().pxToDp

val screenHeightPx = getScreenHeightPx()
val screenHeightDp = getScreenHeightPx().pxToDp

expect fun getScreenWidthPx(): Int
expect fun getScreenHeightPx(): Int

expect fun currentTimeMillis(): Long


