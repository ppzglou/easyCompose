package gr.sppzglou.easycompose

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

@Composable
actual fun AndroidBackPressHandler(
    onBackPressed: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                scope.launch {
                    currentOnBackPressed()
                }
            }
        }
    }
    val lifecycle = LocalLifecycleOwner.current

    lifecycle.LifecycleEvents { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            backPressedDispatcher?.addCallback(backCallback)
        }
        if (event == Lifecycle.Event.ON_PAUSE) {
            backCallback.remove()
        }
    }

    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}

@Composable
fun LifecycleOwner.LifecycleEvents(listener: (Lifecycle.Event) -> Unit = {}) {
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            listener(event)
        }
        this@LifecycleEvents.lifecycle.addObserver(observer)
        onDispose {
            this@LifecycleEvents.lifecycle.removeObserver(observer)
        }
    }
}