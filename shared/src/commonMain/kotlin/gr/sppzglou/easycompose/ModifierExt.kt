package gr.sppzglou.easycompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.applyIf(bool: Boolean, block: Modifier.(Modifier) -> Modifier): Modifier {
    return if (bool) this.then(block(this))
    else this
}

@Composable
fun Modifier.Tap(function: () -> Unit) = this.pointerInput(Unit) {
    detectTapGestures(onTap = {
        function()
    })
}

@Composable
fun Modifier.Click(
    ripple: Color = Color.Gray,
    scope: CoroutineScope = rememberCoroutineScope(),
    interaction: MutableInteractionSource = remember { MutableInteractionSource() },
    bounded: Boolean = true,
    click: suspend () -> Unit
): Modifier {
    return this.clickable(
        interactionSource = interaction,
        indication = ripple(bounded = bounded, color = ripple),
        onClick = {
            scope.launch {
                click()
            }
        }
    )
}

@Composable
fun pxToDp(px: Number) = with(LocalDensity.current) { px.toFloat().toDp() }

@Composable
fun Modifier.heightPx(px: Int) = this.height(pxToDp(px))

@Composable
fun Modifier.widthPx(px: Int) = this.width(pxToDp(px))

@Composable
fun Modifier.widthPx(block: Density.()-> Number) =
    this.width(LocalDensity.current.block().pxToDp.dp)