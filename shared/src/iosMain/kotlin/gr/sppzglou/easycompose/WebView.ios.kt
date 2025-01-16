package gr.sppzglou.easycompose

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openToWeb(link: String) {
    val url = NSURL.URLWithString(link)
    if (url != null) {
        UIApplication.sharedApplication.openURL(url, emptyMap<Any?, Any?>()) { success ->
            if (success) {
                println("URL opened successfully.")
            } else {
                println("Failed to open URL.")
            }
        }
    } else {
        println("Invalid URL: $link")
    }
}
