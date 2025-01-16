package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import kotlin.reflect.KClass

typealias PrefsDataStore = DataStore<Preferences>

private val dataStoreInstanceMap = mutableMapOf<String, PrefsDataStore>()


expect fun getDataStore(): PrefsDataStore

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

fun createDataStoreSingleton(producePath: () -> String): PrefsDataStore {
    val path = producePath()
    return dataStoreInstanceMap.getOrPut(path) {
        createDataStore(producePath)
    }
}


const val dataStoreFileName = "dice.preferences_pb"

@Composable
expect fun rememberDataStore(): PrefsDataStore

suspend fun PrefsDataStore.set(key: String, value: Any?) {
    edit {
        when (value) {
            is Int -> it[intPreferencesKey(key)] = value
            is Double -> it[doublePreferencesKey(key)] = value
            is String -> it[stringPreferencesKey(key)] = value
            is Boolean -> it[booleanPreferencesKey(key)] = value
            is Float -> it[floatPreferencesKey(key)] = value
            is Long -> it[longPreferencesKey(key)] = value
            is Set<*> -> {
                if (value.all { it is String }) {
                    it[stringSetPreferencesKey(key)] = value as Set<String>
                } else {
                    throw UnsupportedOperationException("This set is not String Set. Only String Set is supported")
                }
            }

            is ByteArray -> it[byteArrayPreferencesKey(key)] = value
            else -> throw UnsupportedOperationException(
                "Unsupported set type for key '$key': $value. Please implement this operation."
            )
        }
    }
}

suspend inline fun <reified T> PrefsDataStore.setObj(key: String, value: T) {
    edit {
        try {
            val json = toJson(value)
            it[stringPreferencesKey(key)] = json
        } catch (e: Exception) {
            val errorMessage = "Error converting object to JSON: ${e.message}"
            throw UnsupportedOperationException(
                "Unsupported Obj for key '$key': $value. Please implement this operation."
            )
        }
    }
}

suspend fun PrefsDataStore.clear(key: String, type: KClass<*>) {
    edit {
        when (type) {
            Int::class -> it.remove(intPreferencesKey(key))
            Double::class -> it.remove(doublePreferencesKey(key))
            Long::class -> it.remove(longPreferencesKey(key))
            Boolean::class -> it.remove(booleanPreferencesKey(key))
            Float::class -> it.remove(floatPreferencesKey(key))
            Set::class -> it.remove(stringSetPreferencesKey(key))
            ByteArray::class -> it.remove(byteArrayPreferencesKey(key))
            else -> it.remove(stringPreferencesKey(key))
        }
    }
}

fun <T> PrefsDataStore.get(key: String, default: T?): Flow<T?> {
    return data.map { preferences ->
        when (default) {
            is Int? -> preferences[intPreferencesKey(key)] ?: default
            is Double? -> preferences[doublePreferencesKey(key)] ?: default
            is String? -> preferences[stringPreferencesKey(key)] ?: default
            is Boolean? -> preferences[booleanPreferencesKey(key)] ?: default
            is Float? -> preferences[floatPreferencesKey(key)] ?: default
            is Long? -> preferences[longPreferencesKey(key)] ?: default
            is Set<*> -> {
                if (default.all { it is String }) {
                    preferences[stringSetPreferencesKey(key)] ?: default
                } else {
                    throw UnsupportedOperationException("Default value must be a Set<String>")
                }
            }

            is ByteArray? -> preferences[byteArrayPreferencesKey(key)] ?: default
            else -> throw UnsupportedOperationException(
                "Unsupported get type for key '$key': $default. Please implement this operation."
            )
        } as T? // Τελικό casting στο γενικό τύπο T
    }
}

inline fun <reified T> PrefsDataStore.getObj(key: String, default: T): Flow<T> {
    return data.map { preferences ->
        preferences[stringPreferencesKey(key)]?.let { json ->
            fromJson<T>(json)
        } ?: default
    }
}

suspend fun <T> PrefsDataStore.getValue(key: String, default: T?): T? =
    get(key, default).first()

@Composable
fun <T> PrefsDataStore.collect(key: String, default: T?): State<T?> =
    get(key, default).collectAsStateWithLifecycle(default)

@Composable
inline fun <reified T> PrefsDataStore.collectObj(key: String, default: T): State<T> =
    getObj(key, default).collectAsStateWithLifecycle(default)

fun <T, V> PrefsDataStore.get(key: String, default: T, map: (T) -> V): Flow<V> {
    return data.map { preferences ->
        map(
            when (default) {
                is Int -> preferences[intPreferencesKey(key)] ?: default
                is Double -> preferences[doublePreferencesKey(key)] ?: default
                is String -> preferences[stringPreferencesKey(key)] ?: default
                is Boolean -> preferences[booleanPreferencesKey(key)] ?: default
                is Float -> preferences[floatPreferencesKey(key)] ?: default
                is Long -> preferences[longPreferencesKey(key)] ?: default
                is Set<*> -> {
                    if (default.all { it is String }) {
                        preferences[stringSetPreferencesKey(key)] ?: default
                    } else {
                        throw UnsupportedOperationException("Default value must be a Set<String>")
                    }
                }

                is ByteArray -> preferences[byteArrayPreferencesKey(key)] ?: default
                else -> throw UnsupportedOperationException(
                    "Unsupported type for key '$key': $default. Please implement this operation."
                )
            } as T // Τελικό casting στο γενικό τύπο T
        )
    }
}

@Composable
fun <T, V> PrefsDataStore.collect(key: String, default: T, map: (T) -> V): State<V> =
    get(key, default, map).collectAsStateWithLifecycle(map(default))


inline fun <reified T, V> PrefsDataStore.getObj(key: String, default: T, crossinline map: (T) -> V): Flow<V> {
    return data.map { preferences ->
        map(
            preferences[stringPreferencesKey(key)]?.let { json ->
                fromJson<T>(json)
            } ?: default
        )
    }
}


@Composable
inline fun <reified T, V> PrefsDataStore.collectObj(key: String, default: T, noinline map: (T) -> V): State<V> =
    getObj(key, default, map).collectAsStateWithLifecycle(map(default))

