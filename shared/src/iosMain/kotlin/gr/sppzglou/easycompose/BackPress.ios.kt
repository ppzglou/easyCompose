package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable

@Composable
actual fun AndroidBackPressHandler(onBackPressed: suspend () -> Unit) {}