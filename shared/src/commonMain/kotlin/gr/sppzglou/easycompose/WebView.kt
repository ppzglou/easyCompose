package gr.sppzglou.easycompose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

val LocalWebDialog = staticCompositionLocalOf { WebDialogState() }

data class WebDialogState(
    var url: String = "",
    val isShowing: MutableState<Boolean> = mutableStateOf(false)
) {
    fun initUrl(url: String) {
        this.url = url
    }

    fun show(url: String? = null) {
        url?.let { this.url = it }
        isShowing.value = true
    }

    fun hide() {
        isShowing.value = false
    }
}

@Composable
fun InitWebDialog(bgColor: Color, color: Color, content: @Composable () -> Unit) {
    val webView by mutableRem(WebDialogState())

    CompositionLocalProvider(
        LocalWebDialog provides webView
    ) {
        val blur by animateDpAsState(webView.isShowing.value then 10.dp or 0.dp)
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.blur(blur)) {
                content()
            }
            WebDialogView(bgColor, color)
        }
    }
}

@Composable
fun WebDialogView(bgColor: Color, color: Color) {
    val state = LocalWebDialog.current
    val toast = LocalToastText.current
    val alpha by animateFloatAsState(state.isShowing.value then 1f or 0f, tween())
    val scaleView by animateFloatAsState(state.isShowing.value then 1f or 0f, tween())

    if (alpha > 0) {
        OnBackPress {
            state.hide()
        }
        var offset by mutableRem(Offset.Zero)

        val ofx by animateDpAsState(
            if (offset.x.pxToDp <= -20) (-20).dp
            else if (offset.x.pxToDp >= 20) 20.dp
            else offset.x.pxToDp.dp,
            tween(50, easing = LinearEasing)
        )
        val ofy by animateDpAsState(offset.y.pxToDp.dp, tween(50, easing = LinearEasing))

        Column(
            Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .Tap {
                    state.hide()
                }
                .alpha(alpha)
                .scale(scaleView)
                .offset(ofx, ofy)
        ) {
            Box(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(bgColor.copy(0.7f))
            ) {
                val webViewState = rememberWebViewState(state.url)

                Column {
                    Text(
                        webViewState.pageTitle ?: "",
                        Modifier.padding(10.dp),
                        fontSize = 10.sp,
                        color = color
                    )
                    if (webViewState.isLoading) {
                        LinearProgressIndicator(Modifier.fillMaxWidth(), color = color)
                    }
                    Box {
                        WebView(
                            webViewState,
                            webViewState.isLoading then Modifier.size(0.dp) or
                                    Modifier.fillMaxSize()
                        )
                        Box(
                            Modifier.fillMaxSize()
                            .Tap { }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        offset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        offset = Offset.Zero
                                    },
                                ) { _, dragAmount ->
                                    offset = Offset(
                                        offset.x + dragAmount.x * 0.3f,
                                        offset.y + dragAmount.y * 0.5f
                                    )
                                    if (offset.y.pxToDp > 100) {
                                        state.hide()
                                    }
                                }
                            }
                        )
                    }
                }
            }
            Column(
                Modifier
                    .padding(bottom = 20.dp)
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(0.4f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(bgColor.copy(0.7f))
            ) {
                Text("Open in browser", Modifier.fillMaxWidth().Click {
                    openToWeb(state.url)
                    state.hide()
                }.padding(15.dp), color = color)
                Text("Copy link", Modifier.fillMaxWidth().Click {
                    copyToClipboard(state.url)
                    toast.setToast("Copied!")
                    state.hide()
                }.padding(15.dp), color = color)
            }
        }
    }
}

expect fun openToWeb(link: String)