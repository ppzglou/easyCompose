package gr.sppzglou.easycompose.sharedprefs

class KMMPreference(private val context: KMMContext) {

    fun put(key: String, value: Int) {
        context.putInt(key, value)
    }

    fun put(key: String, value: String?) {
        context.putString(key, value)
    }

    fun put(key: String, value: Boolean) {
        context.putBool(key, value)
    }

    fun put(key: String, value: Float) {
        context.putFloat(key, value)
    }

    fun getInt(key: String, default: Int): Int =
        context.getInt(key, default)


    fun getString(key: String, default: String) =
        context.getString(key, default)


    fun getBool(key: String, default: Boolean): Boolean =
        context.getBool(key, default)

    fun getFloat(key: String, default: Float): Float =
        context.getFloat(key, default)


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

}