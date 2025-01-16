package gr.sppzglou.easycompose

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIScreen
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.math.roundToInt


actual fun getScreenDensity(): Float {
    return UIScreen.mainScreen.scale.toFloat()
}

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenHeightPx() = UIScreen.mainScreen.bounds.useContents { size.height }.roundToInt()

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenWidthPx() = UIScreen.mainScreen.bounds.useContents { size.width }.roundToInt()

actual fun currentTimeMillis() = (NSDate().timeIntervalSince1970 * 1000).toLong()