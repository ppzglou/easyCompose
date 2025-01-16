package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject


@Composable
actual fun FilePicker(state: FilePickerState, onSelect: (File) -> Unit) {
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            // Αντί να ορίσεις documentTypes, περνάς μια λίστα με URLs (μπορεί να είναι άδεια για γενική χρήση)
            val urlList = emptyList<NSURL>() // Ή γέμισε με συγκεκριμένα URLs αν χρειάζεται
            val controller = UIDocumentPickerViewController(
                uRLs = urlList,
                inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
            ).apply {
                setDelegate(object : NSObject(), UIDocumentPickerDelegateProtocol {
                    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                        val nsUrl = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                        nsUrl?.toFile()?.let(onSelect)
                    }
                })
            }

            UIApplication
                .sharedApplication
                .keyWindow
                ?.rootViewController
                ?.presentViewController(controller, animated = true) {
                    state.hide()
                }
        }
    }
}


@OptIn(ExperimentalForeignApi::class)
fun NSURL.toFile(): File? {
    val fileName = this.lastPathComponent ?: ""
    val filePath = this.path ?: ""
    val ext = fileName.substringAfterLast('.', "")

    // Διαβάζουμε τα δεδομένα ως NSData
    val fileData = NSData.dataWithContentsOfURL(this) ?: return null

    // Μετατροπή NSData σε ByteArray
    val byteArray = ByteArray(fileData.length.toInt())
    memScoped {
        val buffer: CPointer<ByteVar> = allocArray(fileData.length.toInt())
        fileData.getBytes(buffer, fileData.length)

        for (i in byteArray.indices) {
            byteArray[i] = buffer[i]
        }
    }

    return File(fileName, filePath, ext, byteArray)
}