package gr.sppzglou.easycompose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.seiko.imageloader.Image
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.*
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun saveToFile(data: ByteArray, fileName: String): String {
    val fileManager = NSFileManager.defaultManager
    val directory =
        fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
    val fileUrl = directory.URLByAppendingPathComponent(fileName)!!

    // Μετατροπή ByteArray σε NSData
    val nsData = data.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = data.size.toULong())
    }

    fileUrl.path?.let { path ->
        if (!NSFileManager.defaultManager.createFileAtPath(path, nsData, null)) {
            throw IllegalStateException("Failed to create file at $path")
        }
    }

    return fileUrl.absoluteString!!
}

actual fun readFile(fileName: String): ByteArray? {
    val fileManager = NSFileManager.defaultManager
    val directory =
        fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
    val fileUrl = directory.URLByAppendingPathComponent(fileName)!!

    return fileUrl.path?.let { path ->
        val nsData = NSData.dataWithContentsOfFile(path) ?: return null
        return nsData.toByteArray()
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val byteArray = ByteArray(length)
    val bytes = this.bytes
    if (bytes != null) {
        byteArray.usePinned {
            memcpy(it.addressOf(0), bytes, this.length)
        }
    }
    return byteArray
}

// Μετατροπή ByteArray -> NSData
@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }
}

actual fun encodeToBase64(input: String): String {
    val data = input.encodeToByteArray().toNSData()
    return data.base64EncodedStringWithOptions(0u)
}

actual fun compressAndResizeImage(
    imageData: ByteArray,
    scaleFactor: Float,
    quality: Float
): ByteArray {
    val resizedData = resizeImage(imageData, scaleFactor)
    val compressedData = compressImage(resizedData, quality)

    return compressedData
}

fun compressImage(imageData: ByteArray, quality: Float): ByteArray {
    return try {
        val nsData = imageData.toNSData() // Μετατροπή σε NSData
        val uiImage = UIImage(data = nsData)
        val compressedData = UIImageJPEGRepresentation(uiImage, quality.toDouble())
        compressedData?.toByteArray() ?: byteArrayOf()
    } catch (e: Exception) {
        log("ImageView", "Error resizing image: $e", LogType.Error)
        imageData
    }
}

@OptIn(ExperimentalForeignApi::class)
fun resizeImage(imageData: ByteArray, scaleFactor: Float): ByteArray {
    return try {
        // Μετατροπή ByteArray σε UIImage
        val nsData = imageData.toNSData()
        val originalImage = UIImage(data = nsData)

        // Υπολογισμός νέων διαστάσεων
        val newWidth = originalImage.size.useContents {
            this.width * scaleFactor
        }
        val newHeight = originalImage.size.useContents {
            this.height * scaleFactor
        }
        val newSize = CGSizeMake(newWidth, newHeight)

        // Αλλαγή μεγέθους χρησιμοποιώντας UIGraphicsImageRenderer
        val resizedImage = UIGraphicsImageRenderer(size = newSize).run {
            this.imageWithActions { context ->
                originalImage.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
            }
        }

        // Επιστροφή του resized image ως ByteArray
        val resizedData = UIImagePNGRepresentation(resizedImage)
        resizedData?.toByteArray() ?: byteArrayOf()
    } catch (e: Exception) {
        log("ImageView", "Error resizing image: $e", LogType.Error)
        imageData
    }
}