package gr.sppzglou.easycompose.sharedprefs

expect fun getSharedPreferences(): KMMPreference

expect fun KMMContext.putInt(key: String, value: Int)

expect fun KMMContext.getInt(key: String, default: Int): Int

expect fun KMMContext.putString(key: String, value: String?)

expect fun KMMContext.getString(key: String, default: String): String

expect fun KMMContext.putBool(key: String, value: Boolean)

expect fun KMMContext.getBool(key: String, default: Boolean): Boolean

expect fun KMMContext.putFloat(key: String, value: Float)

expect fun KMMContext.getFloat(key: String, default: Float): Float

operator fun KMMPreference.set(key: String, value: Any?) {
    when (value) {
        is String? -> put(key, value)
        is Int -> put(key, value)
        is Boolean -> put(key, value)
        is Float -> put(key, value)
        else -> throw UnsupportedOperationException("Access PreferencesHelper to implement this kind of operation")
    }
}

inline operator fun <reified T : Any> KMMPreference.get(
    key: String,
    defaultValue: T
): T {
    return when (T::class) {
        String::class -> getString(key, defaultValue as String) as T
        Int::class -> getInt(key, defaultValue as Int) as T
        Boolean::class -> getBool(key, defaultValue as Boolean) as T
        Float::class -> getFloat(key, defaultValue as Float) as T
        else -> throw UnsupportedOperationException("Access PreferencesHelper to implement this kind of operation")
    }
}