package gr.sppzglou.easycompose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

val LocalToastText = staticCompositionLocalOf { Toast() }

data class Toast(
    var text: MutableStateFlow<String?> = MutableStateFlow(null),
    var millis: Long = 3000
) {
    suspend fun setToast(txt: Any?, millis: Long = 3000) {
        this.millis = millis
        clearToast()
        delay(150)
        text.value = txt.toString()
    }

    fun clearToast() {
        text.value = null
        this.millis = 3000
    }
}

@Composable
fun InitToast(txtColor: Color = White, bgColor: Color = Gray, content: @Composable () -> Unit) {
    val toast by mutableRem(Toast())

    CompositionLocalProvider(
        LocalToastText provides toast
    ) {
        Box {
            content()
            ToastView(txtColor, bgColor)
        }
    }
}

@Composable
fun ToastView(txtColor: Color, bgColor: Color) {
    val toast = LocalToastText.current
    val text by toast.text.collectAsState()
    var txt by mutableRem("")
    var time by mutableRem<Long?>(null)
    val anim by animateFloatAsState(if (text != null) 1f else 0f)

    fun stop() {
        time = null
        toast.clearToast()
    }

    LaunchedEffect(text) {
        if (text != null) {
            txt = text!!
            time = currentTimeMillis() + toast.millis

            while (currentTimeMillis() < time!!) {
                if (txt != text) break
                delay(100)
            }
            stop()

        } else stop()
    }

    if (anim > 0) {
        Column(
            Modifier
                .alpha(anim)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(bottom = 10.dp),
            Arrangement.Bottom,
            Alignment.CenterHorizontally
        ) {
            Text(
                txt,
                Modifier
                    .shadow(5.dp, CircleShape)
                    .background(bgColor.copy(0.8f))
                    .padding(15.dp),
                color = txtColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}