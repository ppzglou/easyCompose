package gr.sppzglou.easycompose


val Number.pxToDp: Float
    get() = (this.toInt() / getScreenDensity())

val Number.dpToPx: Float
    get() = this.toFloat() * getScreenDensity()

enum class DatePattern(val pattern: String) {
    ISO_DATE("yyyy-MM-dd"), // ISO 8601 ημερομηνία
    ISO_DATE_TIME("yyyy-MM-dd'T'HH:mm:ss"), // ISO 8601 ημερομηνία και ώρα
    ISO_DATE_TIME_MILLIS("yyyy-MM-dd'T'HH:mm:ss.SSS"), // ISO 8601 με millisecond
    ISO_DATE_TIME_UTC("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), // ISO 8601 UTC
    ISO_DATE_TIME_OFFSET("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"), // ISO 8601 με ζώνη ώρας (offset)

    SIMPLE_DATE("dd-MM-yyyy"), // Απλή Ευρωπαϊκή μορφή ημερομηνίας
    US_DATE("MM-dd-yyyy"), // Απλή Αμερικανική μορφή ημερομηνίας
    COMPACT_DATE("yyyyMMdd"), // Συμπαγής ημερομηνία
    COMPACT_DATE_TIME("yyyyMMddHHmmss"), // Συμπαγής ημερομηνία και ώρα

    FRIENDLY_DATE("EEEE, dd MMMM yyyy"), // Πλήρης ημερομηνία με ημέρα εβδομάδας
    FRIENDLY_DATE_TIME("EEEE, dd MMMM yyyy HH:mm:ss"), // Πλήρης ημερομηνία και ώρα

    TIME_24H("HH:mm:ss"), // Ώρα 24ωρης μορφής
    TIME_12H("hh:mm:ss a"), // Ώρα 12ωρης μορφής
    TIME_MILLIS("HH:mm:ss.SSS"), // Ώρα με millisecond

    RFC_1123("EEE, dd MMM yyyy HH:mm:ss z"), // RFC 1123 format
    ZONE_OFFSET("Z"), // Offset ζώνης ώρας
    ZONE_NAME("z"); // Όνομα ζώνης ώρας
}

expect fun String.formatDate(
    from: DatePattern = DatePattern.ISO_DATE,
    to: String = "dd MMM yyyy"
): String

fun Any?.tStr(old: String = "", new: String = "", nil: String = "") =
    this?.toString()?.replace(old, new) ?: nil


expect fun Double?.formatToStr(decimalPlaces: Int = 1): String


