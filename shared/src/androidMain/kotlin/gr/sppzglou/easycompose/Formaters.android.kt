package gr.sppzglou.easycompose

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

actual fun String.formatDate(from: DatePattern, to: String): String {
    return try {
        val inputFormat = SimpleDateFormat(from.pattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(to, Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        log("Date Formater", e)
        this
    }
}

actual fun Double?.formatToStr(decimalPlaces: Int): String {
    val format = "#." + "#".repeat(decimalPlaces)
    val decimalFormat = DecimalFormat(format)
    return this?.let { decimalFormat.format(this) } ?: ""
}