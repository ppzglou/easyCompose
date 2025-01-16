package gr.sppzglou.easycompose

import androidx.compose.ui.graphics.Color

fun Color.toHexString(): String {
    val alpha = (alpha * 255).toInt().toString(16).padStart(2, '0')
    val red = (red * 255).toInt().toString(16).padStart(2, '0')
    val green = (green * 255).toInt().toString(16).padStart(2, '0')
    val blue = (blue * 255).toInt().toString(16).padStart(2, '0')
    return "#$alpha$red$green$blue"
}

fun String.toColor(): Color {
    val colorInt = this.removePrefix("#").toLong(16)
    return when (this.length) {
        7 -> { // Χωρίς alpha (π.χ. #RRGGBB)
            Color(
                red = ((colorInt shr 16) and 0xFF) / 255f,
                green = ((colorInt shr 8) and 0xFF) / 255f,
                blue = (colorInt and 0xFF) / 255f,
                alpha = 1f
            )
        }
        9 -> { // Με alpha (π.χ. #AARRGGBB)
            Color(
                alpha = ((colorInt shr 24) and 0xFF) / 255f,
                red = ((colorInt shr 16) and 0xFF) / 255f,
                green = ((colorInt shr 8) and 0xFF) / 255f,
                blue = (colorInt and 0xFF) / 255f
            )
        }
        else -> throw IllegalArgumentException("Invalid color format")
    }
}