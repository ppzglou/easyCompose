package gr.sppzglou.easycompose.sharedprefs

import platform.Foundation.NSUserDefaults
import platform.darwin.NSObject

val defaults = NSUserDefaults.standardUserDefaults

fun <T> checkIfExistValue(key: String, default: T, block: () -> T) =
    defaults.objectForKey(key)?.let {
        block()
    } ?: default

actual fun getSharedPreferences(): KMMPreference =
    KMMPreference(NSObject())

actual fun KMMContext.putInt(key: String, value: Int) {
    NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
}

actual fun KMMContext.getInt(key: String, default: Int): Int =
    checkIfExistValue(key, default) {
        defaults.integerForKey(key).toInt()
    }

actual fun KMMContext.putString(key: String, value: String?) {
    NSUserDefaults.standardUserDefaults.setObject(value, key)
}

actual fun KMMContext.getString(key: String, default: String): String =
    checkIfExistValue(key, default) {
        defaults.stringForKey(key).toString()
    }

actual fun KMMContext.putBool(key: String, value: Boolean) {
    NSUserDefaults.standardUserDefaults.setBool(value, key)
}

actual fun KMMContext.getBool(key: String, default: Boolean): Boolean =
    checkIfExistValue(key, default) {
        defaults.boolForKey(key)
    }

actual fun KMMContext.putFloat(key: String, value: Float) {
    NSUserDefaults.standardUserDefaults.setFloat(value, key)
}

actual fun KMMContext.getFloat(key: String, default: Float): Float =
    checkIfExistValue(key, default) {
        defaults.floatForKey(key)
    }