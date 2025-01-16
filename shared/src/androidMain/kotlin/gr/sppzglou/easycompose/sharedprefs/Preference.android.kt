package gr.sppzglou.easycompose.sharedprefs

import android.content.Context
import gr.sppzglou.easycompose.sharedApplication

const val APP_PREFS = "APP_PREFS"


actual fun getSharedPreferences(): KMMPreference = KMMPreference(sharedApplication)

actual fun KMMContext.putInt(key: String, value: Int) {
    getSpEditor().putInt(key, value).apply()
}

actual fun KMMContext.getInt(key: String, default: Int): Int {
    return getSp().getInt(key, default)
}

actual fun KMMContext.putString(key: String, value: String?) {
    getSpEditor().putString(key, value).apply()
}

actual fun KMMContext.getString(key: String, default: String): String =
    getSp().getString(key, default) ?: default

actual fun KMMContext.putBool(key: String, value: Boolean) {
    getSpEditor().putBoolean(key, value).apply()
}

actual fun KMMContext.getBool(key: String, default: Boolean): Boolean {
    return getSp().getBoolean(key, default)
}

actual fun KMMContext.putFloat(key: String, value: Float) {
    getSpEditor().putFloat(key, value).apply()
}

actual fun KMMContext.getFloat(key: String, default: Float): Float {
    return getSp().getFloat(key, default)
}


private fun KMMContext.getSp() = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)

private fun KMMContext.getSpEditor() = getSp().edit()