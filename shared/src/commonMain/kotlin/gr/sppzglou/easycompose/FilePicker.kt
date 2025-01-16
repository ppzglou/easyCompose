package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
expect fun FilePicker(state: FilePickerState, onSelect: (File) -> Unit)

@Stable
class FilePickerState(
    initialValue: Boolean = false
) {
    var isVisible by mutableStateOf(initialValue)

    fun show() {
        isVisible = false
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    companion object {
        fun Saver(): Saver<FilePickerState, *> = Saver(
            save = { it.isVisible },
            restore = { FilePickerState(it) }
        )
    }
}

@Composable
fun rememberFilePickerState(
    initialValue: Boolean = false
): FilePickerState {
    return rememberSaveable(
        saver = FilePickerState.Saver()
    ) { FilePickerState(initialValue) }
}