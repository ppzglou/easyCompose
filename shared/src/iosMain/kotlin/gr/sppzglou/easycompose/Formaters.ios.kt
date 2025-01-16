package gr.sppzglou.easycompose

import platform.Foundation.*

actual fun String.formatDate(from: DatePattern, to: String): String {
    return try {
        val dateFormatter = NSDateFormatter().apply {
            dateFormat = from.pattern
        }
        val outputFormatter = NSDateFormatter().apply {
            dateFormat = to
        }

        val date = dateFormatter.dateFromString(this)
        date?.let { outputFormatter.stringFromDate(it) } ?: this
    } catch (e: Exception) {
        log("Date Formater", e)
        this
    }
}

actual fun Double?.formatToStr(decimalPlaces: Int): String {
    val formatter = NSNumberFormatter().apply {
        minimumFractionDigits = decimalPlaces.toULong()
        maximumFractionDigits = decimalPlaces.toULong()
    }
    return this?.let { formatter.stringFromNumber(NSNumber(this)) } ?: ""
}