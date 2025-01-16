package gr.sppzglou.easycompose

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val context
    @Composable
    get() = LocalContext.current

actual fun getScreenDensity(): Float {
    return Resources.getSystem().displayMetrics.density
}

actual fun getScreenHeightPx() = Resources.getSystem().displayMetrics.heightPixels

actual fun getScreenWidthPx() = Resources.getSystem().displayMetrics.widthPixels

actual fun currentTimeMillis() = System.currentTimeMillis()