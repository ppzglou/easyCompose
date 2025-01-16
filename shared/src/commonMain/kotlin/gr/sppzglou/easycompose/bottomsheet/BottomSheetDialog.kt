package gr.sppzglou.easycompose.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import gr.sppzglou.easycompose.mutableRem

val LocalAppBottomSheet = staticCompositionLocalOf { AppBottomSheets() }

data class Sheet(
    val state: BottomSheetState,
    val modifier: Modifier,
    val scrimColor: Color,
    val content: (@Composable () -> Unit)
) {
    override fun equals(other: Any?): Boolean {
        return other is Sheet && other.state == this.state
    }

    override fun hashCode(): Int {
        return state.hashCode()
    }
}

data class AppBottomSheets(
    var sheets: MutableList<Sheet> = mutableStateListOf()
) {
    fun addSheet(sheets: Sheet) {
        this.sheets.add(sheets)
    }

    fun removeSheet(sheets: Sheet) {
        this.sheets.removeAt(this.sheets.indexOf(sheets))
    }
}

@Composable
fun BottomSheetDialog(
    modifier: Modifier = Modifier,
    state: BottomSheetState,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    sheetContent: @Composable () -> Unit
) {
    val screenSheets = LocalAppBottomSheet.current

    DisposableEffect(state) {
        screenSheets.addSheet(
            Sheet(
                state,
                modifier,
                scrimColor,
                sheetContent
            )
        )

        onDispose {
            screenSheets.removeSheet(
                Sheet(
                    state,
                    modifier,
                    scrimColor,
                    sheetContent
                )
            )
        }
    }
}

@Composable
fun InitBottomSheet(content: @Composable () -> Unit) {
    val sheet by mutableRem(AppBottomSheets())

    CompositionLocalProvider(
        LocalAppBottomSheet provides sheet
    ) {

        Box(Modifier.fillMaxSize()) {
            content()

            sheet.sheets.forEachIndexed { i, sheet ->
                BottomSheet(
                    sheet.state,
                    sheet.modifier,
                    sheet.scrimColor,
                    sheet.content
                )
            }
        }
    }
}