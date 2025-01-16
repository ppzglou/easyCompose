package gr.sppzglou.easycompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.Spacer(dp: Number = 10) {
    androidx.compose.foundation.layout.Spacer(Modifier.width(dp.toFloat().dp))
}

@Composable
fun ColumnScope.Spacer(dp: Number = 10) {
    androidx.compose.foundation.layout.Spacer(Modifier.height(dp.toFloat().dp))
}

@Composable
fun ColumnScope.SpacerNav(dp: Number = 0) {
    androidx.compose.foundation.layout.Spacer(Modifier.navigationBarsPadding().height(dp.toFloat().dp))
}

@Composable
fun ColumnScope.SpacerStatus(dp: Number = 0) {
    androidx.compose.foundation.layout.Spacer(Modifier.statusBarsPadding().height(dp.toFloat().dp))
}

@Composable
fun SpacerH(dp: Number = 10) {
    androidx.compose.foundation.layout.Spacer(Modifier.width(dp.toFloat().dp))
}

@Composable
fun SpacerV(dp: Number = 10) {
    androidx.compose.foundation.layout.Spacer(Modifier.height(dp.toFloat().dp))
}

@Composable
fun SpacerNav(dp: Number = 0) {
    androidx.compose.foundation.layout.Spacer(Modifier.navigationBarsPadding().height(dp.toFloat().dp))
}

@Composable
fun SpacerStatus(dp: Number = 0) {
    androidx.compose.foundation.layout.Spacer(Modifier.statusBarsPadding().height(dp.toFloat().dp))
}

fun LazyListScope.Spacer(dp: Number = 10) {
    item {
        androidx.compose.foundation.layout.Spacer(Modifier.height(dp.toFloat().dp))
    }
}

fun LazyListScope.SpacerNav(dp: Number = 10) {
    item {
        androidx.compose.foundation.layout.Spacer(Modifier.navigationBarsPadding().height(dp.toFloat().dp))
    }
}

fun LazyListScope.SpacerStatus(dp: Number = 10) {
    item {
        androidx.compose.foundation.layout.Spacer(Modifier.statusBarsPadding().height(dp.toFloat().dp))
    }
}

@Composable
fun LazyItemScope.Spacer(dp: Number = 10) {
    Column {
        Spacer(dp)
    }
}

@Composable
fun LazyItemScope.SpacerNav(dp: Number = 10) {
    Column {
        SpacerNav(dp)
    }
}

@Composable
fun LazyItemScope.SpacerStatus(dp: Number = 10) {
    Column {
        SpacerStatus(dp)
    }
}
