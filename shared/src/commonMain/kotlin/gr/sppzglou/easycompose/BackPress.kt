package gr.sppzglou.easycompose

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource

@Composable
expect fun AndroidBackPressHandler(onBackPressed: suspend () -> Unit)

val LocalBackPress = staticCompositionLocalOf { AppBack() }

data class AppBack(
    var actions: MutableList<(suspend () -> Unit)> = mutableStateListOf()
) {
    fun addAction(action: (suspend () -> Unit)) {
        this.actions.add(action)
    }

    fun removeAction(action: (suspend () -> Unit)) {
        this.actions.removeAt(this.actions.indexOf(action))
    }
}

@Composable
fun OnBackPress(action: suspend () -> Unit) {
    val back = LocalBackPress.current

    DisposableEffect(Unit) {

        back.addAction(action)

        onDispose {
            back.removeAction(action)
        }
    }

    AndroidBackPressHandler(action)
}

@Composable
fun InitBackPress(icoBg: DrawableResource, ico: DrawableResource, bg: Color = Gray, tint: Color = Black, content: @Composable () -> Unit) {
    val back by mutableRem(AppBack())

    CompositionLocalProvider(
        LocalBackPress provides back
    ) {
        Box(Modifier.fillMaxSize()) {
            content()
            SwipeBack(icoBg, ico, bg, tint)
        }
    }
}

@Composable
fun SwipeBack(icoBg: DrawableResource, ico: DrawableResource, bg: Color = Gray, tint: Color = Black) {
    val haptic = LocalHapticFeedback.current
    val back = LocalBackPress.current
    val nav = LocalNavigator.currentOrThrow

    if (back.actions.isNotEmpty() || nav.canPop) {
        val scope = rememberCoroutineScope()
        var sizeDp by mutableRem(0.dp)
        var top by mutableRem(0.dp)
        val size by animateDpAsState(sizeDp)
        val height = 200

        var vibrate by mutableRem(false)

        fun back() = scope.launch {
            back.actions.lastOrNull()?.invoke() ?: nav.pop()
        }

        AndroidBackPressHandler {
            back()
        }

        Box(
            Modifier
                .zIndex(100f)
                .width(10.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (sizeDp > 30.dp) {
                                back()
                            }
                            sizeDp = 0.dp
                        },
                        onDragCancel = {
                            sizeDp = 0.dp
                        }
                    ) { change, _ ->
                        val tempTop = change.position.y.toDp() - (height * 0.5).dp
                        top = if (tempTop < 0.dp) 0.dp else tempTop

                        val dp = change.position.x.toDp()
                        if (dp <= 50.dp) sizeDp = dp
                        vibrate = dp > 30.dp
                    }
                }
        )

        LaunchedEffect(vibrate) {
            if (vibrate) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }

        Column(
            Modifier.zIndex(100f).padding(top = top).height(height.dp),
            Arrangement.Center
        ) {
            Box(Modifier.size(size, height.dp)) {
                ImageViewLib(
                    data = icoBg,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(bg)
                )
                Column(Modifier.fillMaxSize(), Arrangement.Center) {
                    ImageViewLib(
                        data = ico,
                        modifier = Modifier.size((size.value * 0.6f).dp),
                        colorFilter = ColorFilter.tint(tint)
                    )
                }
            }
        }
    }
}