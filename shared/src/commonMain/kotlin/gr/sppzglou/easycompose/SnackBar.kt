package gr.sppzglou.easycompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun InitSnackBar(error: Exception?, txtColor: Color, bgColor: Color, clearError: () -> Unit = {}, content: @Composable () -> Unit) {
    Box {
        content()
        SnackBarView(error, txtColor, bgColor, clearError)
    }
}

@Composable
fun SnackBarView(error: Exception?, txtColor: Color, bgColor: Color, clearError: () -> Unit = {}) {
    var txt by mutableRem("")
    var time by mutableRem<Long?>(null)
    val sheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)

    fun stop() {
        time = null
        clearError()
    }

    LaunchedEffect(error) {
        try {
            if (error != null) {
                val textError = error!!.message.toString()
                txt = textError
                time = currentTimeMillis() + 3000
                sheetState.show()

                while (currentTimeMillis() < time!!) {
                    if (txt != textError) break
                    delay(100)
                }
                stop()

            } else {
                sheetState.hide()
                stop()
            }
        } catch (_: Exception) {
            sheetState.hide()
            stop()
        }
    }

    ModalBottomSheetLayout(
        {
            Box(
                Modifier
                    .rotate(180f)
                    .background(bgColor)
                    .statusBarsPadding()
            ) {
                if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.isVisible) {
                    Box(Modifier
                        .fillMaxWidth()
                        .Tap {
                            stop()
                        }
                        .padding(10.dp)) {
                        Text(
                            txt,
                            color = txtColor,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        Modifier.rotate(180f),
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(15.dp),
        scrimColor = Color.Unspecified,
        sheetBackgroundColor = Color.Transparent,
        sheetGesturesEnabled = false,
        sheetElevation = 0.dp
    ) {}
}