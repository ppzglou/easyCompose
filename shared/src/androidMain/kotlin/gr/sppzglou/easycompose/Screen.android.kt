package gr.sppzglou.easycompose

import android.content.res.Configuration

actual fun getScreenOrientation(): ScreenOrientation {
    val configuration = sharedApplication.resources.configuration.orientation
    return when (configuration) {
        Configuration.ORIENTATION_LANDSCAPE -> ScreenOrientation.Landscape
        Configuration.ORIENTATION_PORTRAIT -> ScreenOrientation.Portrait
        else -> ScreenOrientation.Portrait
    }
}