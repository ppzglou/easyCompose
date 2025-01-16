package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import kotlinx.serialization.Serializable

interface Language {
    val region: String
    val locale: AppLocale
}

@Serializable
class AppLocale(
    val code: String,
    var region: String = "",
) {
    override fun equals(other: Any?): Boolean {
        return other is AppLocale && other.code == code
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + region.hashCode()
        return result
    }

}

const val LANG_KEY = "LANG_KEY"

fun getSystemLang() = AppLocale(androidx.compose.ui.text.intl.Locale.current.language)

val langList = mutableStateListOf<AppLocale>()
private val stringRes = mutableMapOf<Enum<*>, Map<AppLocale, String>>()


private fun logError(message: String): Nothing {
    log("Setup String Res", message, LogType.Error)
    error(message)
}

@Composable
fun InitLocales(initBlock: () -> Unit, content: @Composable () -> Unit) {
    Launch {
        initBlock()
    }

    if (StringRes.isLocalesInitialized) {
        content()
    }
}

class StringRes {
    companion object {
        val isLocalesInitialized
            get() = langList.isNotEmpty()

        fun register(vararg locales: String) {
            langList.clear()
            langList.addAll(locales.map { AppLocale(it) })
        }

        fun register(vararg locales: AppLocale) {
            langList.clear()
            langList.addAll(locales)
        }

        fun register(locales: List<Language>) {
            langList.clear()
            langList.addAll(locales.map { it.locale })
        }

        fun setupRes(vararg keys: Pair<Enum<*>, List<String>>) {
            keys.forEach { (key, strings) ->
                stringRes[key] = strings.mapIndexed { i, str ->
                    val lang =
                        langList.getOrNull(i) ?: logError("Too many strings for this key $key!")
                    lang to str
                }.toMap()
            }
        }

        suspend fun changeLanguage(locale: AppLocale) {
            val dataStore = getDataStore()
            dataStore.setObj(LANG_KEY, locale)
        }

        suspend fun changeLanguage(locale: String) {
            val dataStore = getDataStore()
            dataStore.setObj(LANG_KEY, AppLocale(locale))
        }

        suspend fun clearLanguage() {
            val dataStore = getDataStore()
            dataStore.clear(LANG_KEY, AppLocale::class)
        }

    }
}

@Composable
fun appLanguage(): AppLocale {
    val dataStore = rememberDataStore()

    val systemLang = getSystemLang()
    val default by mutableRem(systemLang) {
        langList.firstOrNull { it == systemLang } ?: getDefaultLang()
    }
    val locale by dataStore.collectObj(LANG_KEY, default)

    return locale
}

fun String.getLocale(): AppLocale = langList.first { it.code == this }

fun getDefaultLang() = langList.firstOrNull() ?: logError("Setup lang list first!")

enum class LettersFormat {
    CapitalizeFirst,
    CapitalizeWords,
    Uppercase,
    Lowercase,
    Unchanged;
}

@Composable
fun str(id: Enum<*>, format: LettersFormat = LettersFormat.Unchanged): String {
    val locale = appLanguage()

    return remember(locale) {
        textFormat(locale.stringResources(id), format)
    }
}

@Composable
fun str(txt: String, format: LettersFormat = LettersFormat.Unchanged): String {

    return remember {
        textFormat(txt, format)
    }
}

fun textFormat(txt: String, format: LettersFormat) = when (format) {
    LettersFormat.CapitalizeFirst -> txt.firstCapital()
    LettersFormat.CapitalizeWords -> txt.capitalizeEachWord()
    LettersFormat.Uppercase -> txt.uppercase()
    LettersFormat.Lowercase -> txt.lowercase()
    else -> txt
}

private val cache = mutableMapOf<AppLocale, MutableMap<Enum<*>, String>>()

fun AppLocale.stringResources(value: Enum<*>): String {
    val resources = stringRes[value]
    return cache.getOrPut(this) { mutableMapOf() }.getOrPut(value) {
        resources?.get(this) ?: resources?.get(getDefaultLang()) ?: ""
    }
}

fun String.firstCapital(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

fun String.capitalizeEachWord(): String {
    return this.split(" ").joinToString(" ") {
        it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
}


