package gr.sppzglou.easycompose

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.ContextCompat.getSystemService

actual fun copyToClipboard(text: String) {
    val clipboard =
        getSystemService(sharedApplication, ClipboardManager::class.java) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}