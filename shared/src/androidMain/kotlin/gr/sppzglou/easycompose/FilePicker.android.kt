package gr.sppzglou.easycompose

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun FilePicker(state: FilePickerState, onSelect: (File) -> Unit) {
    val context = context

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        state.hide()
        uri?.let {
            it.toFile(context)?.let(onSelect)
        }
    }

    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            launcher.launch("*/*")
        }
    }
}

fun Uri.toFile(context: Context): File? {
    return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.let { path ->
        var fileName: String? = null
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }

        val outputFile = java.io.File(path, fileName ?: "${currentTimeMillis()}.csv")
        val ext = outputFile.name.substringAfter(".")

        context.contentResolver.openInputStream(this)?.use { inS ->
            inS.use { input ->
                outputFile.outputStream().use { output -> input.copyTo(output) }
            }
        }

        File(outputFile.name, outputFile.path, ext, outputFile.readBytes())
    }
}