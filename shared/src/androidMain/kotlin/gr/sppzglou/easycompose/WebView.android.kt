package gr.sppzglou.easycompose

import android.content.Intent
import android.net.Uri

actual fun openToWeb(link: String) {

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(link)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    sharedApplication.startActivity(intent)
}