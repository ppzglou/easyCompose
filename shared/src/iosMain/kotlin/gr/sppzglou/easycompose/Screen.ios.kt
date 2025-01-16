package gr.sppzglou.easycompose

import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation

actual fun getScreenOrientation(): ScreenOrientation {
    val orientation = UIDevice.currentDevice.orientation
    return when (orientation) {
        UIDeviceOrientation.UIDeviceOrientationLandscapeLeft,
        UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> ScreenOrientation.Landscape

        UIDeviceOrientation.UIDeviceOrientationPortrait,
        UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> ScreenOrientation.Portrait

        else -> ScreenOrientation.Portrait
    }
}