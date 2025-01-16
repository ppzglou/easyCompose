package gr.sppzglou.easycompose

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.File

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
    return bitmap.asImageBitmap()
}


actual fun saveToFile(data: ByteArray, fileName: String): String {
    val file = File(sharedApplication.filesDir, fileName)
    file.writeBytes(data)
    return file.absolutePath
}

actual fun readFile(fileName: String): ByteArray? {
    val file = File(sharedApplication.filesDir, fileName)
    return if (file.exists()) file.readBytes() else null
}

actual fun encodeToBase64(input: String): String {
    return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP or Base64.URL_SAFE)
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

fun resizeImage(imageData: ByteArray, scaleFactor: Float): ByteArray {
    return try {
        // Μετατροπή ByteArray σε Bitmap
        val originalBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

        // Υπολογισμός νέων διαστάσεων
        val newWidth = (originalBitmap.width * scaleFactor).toInt()
        val newHeight = (originalBitmap.height * scaleFactor).toInt()

        // Δημιουργία scaled Bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        // Επιστροφή του resized image ως ByteArray (χωρίς συμπίεση)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            outputStream
        ) // Διατηρεί όλα τα δεδομένα
        outputStream.toByteArray()
    } catch (e: Exception) {
        log("ImageView", "Error resizing image: $e", LogType.Error)
        imageData
    }
}

fun compressImage(imageData: ByteArray, quality: Float): ByteArray {
    return try {
        // Μετατροπή ByteArray σε Bitmap
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

        // Συμπίεση της εικόνας σε JPEG
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, (quality * 100).toInt(), outputStream)
        outputStream.toByteArray()
    } catch (e: Exception) {
        log("ImageView", "Error resizing image: $e", LogType.Error)
        imageData
    }
}