package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
fun Launch(block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(Unit) {
        block()
    }
}

@Composable
fun Dispose(onDisposeEffect: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
            onDisposeEffect()
        }
    }
}

enum class LogType {
    Verbose,
    Debug,
    Info,
    Warn,
    Error,
    Assert
}

@Composable
fun <T> Flow<T>?.collectAsStateWithLifecycleNullable(defaultValue: T): State<T> {
    return this?.collectAsStateWithLifecycle(defaultValue) ?: mutableStateOf(defaultValue)
}

fun log(tag: String, message: Any?, severity: LogType = LogType.Debug) =
    Logger.log(Severity.valueOf(LogType.Debug.name), tag, null, message.toString())


fun safePadding(padding: Dp) = if (padding < 0.dp) 0.dp else padding

fun safePadding(padding: Float) = if (padding < 0f) 0f else padding

fun safePadding(padding: Double) = if (padding < 0.0) 0.0 else padding

fun safePadding(padding: Int) = if (padding < 0) 0 else padding


infix fun <T> Boolean.then(then: T): TernaryCondition<T> {
    return TernaryCondition(this, then)
}

class TernaryCondition<T>(private val condition: Boolean, private val trueValue: T) {
    infix fun or(falseValue: T): T {
        return if (condition) trueValue else falseValue
    }

    infix fun orNull(falseValue: T?): T? {
        return if (condition) trueValue else falseValue
    }
}

expect fun copyToClipboard(text: String)
