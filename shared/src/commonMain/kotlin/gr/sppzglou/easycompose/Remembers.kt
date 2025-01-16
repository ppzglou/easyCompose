package gr.sppzglou.easycompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Composable
fun <T> mutableRem(v: T) = remember { mutableStateOf(v) }

@Composable
fun <T> mutableRem(vararg keys: Any?, v: () -> T) = remember(keys) { mutableStateOf(v.invoke()) }

@Composable
fun <T> mutableRemS(v: T) = rememberSaveable { mutableStateOf(v) }

@Composable
fun <T> mutableRemS(vararg keys: Any?, v: () -> T) = rememberSaveable(keys) { mutableStateOf(v.invoke()) }

@Composable
fun <T> mutableRemList() = remember { mutableStateListOf<T>() }

@Composable
fun <T> mutableRemList(vararg items: T) =
    remember { mutableStateListOf<T>().apply { addAll(items) } }

@Composable
fun <K, V> mutableRemMap() = remember { mutableStateMapOf<K, V>() }

@Composable
fun <K, V> mutableRemMap(vararg pairs: Pair<K, V>) = remember {
    mutableStateMapOf<K, V>().apply {
        putAll(pairs)
    }
}

@Composable
inline fun <reified T> mutableRemObj(initialValue: T): MutableState<T> {
    // Δημιουργία Custom Saver για το αντικείμενο
    val saver = Saver<MutableState<T>, String>(
        save = {
            toJson(it.value) // Μετατροπή του αντικειμένου σε JSON string για αποθήκευση
        },
        restore = {
            val restoredValue = fromJson<T>(it) // Επαναφορά του αντικειμένου από JSON string
            mutableStateOf(restoredValue)
        }
    )

    // Χρήση του rememberSaveable με τον προσαρμοσμένο Saver
    return rememberSaveable(saver = saver) {
        mutableStateOf(initialValue)
    }
}

@Composable
inline fun <reified T> mutableRemObjList(initialValue: List<T>): SnapshotStateList<T> {
    // Custom Saver για την αποθήκευση και επαναφορά της λίστας
    val saver = Saver<SnapshotStateList<T>, String>(
        save = {
            toJson(it) // Μετατροπή της λίστας σε JSON string για αποθήκευση
        },
        restore = {
            val list = fromJson<List<T>>(it) // Επαναφορά της λίστας από JSON string
            SnapshotStateList<T>().apply { addAll(list) }
        }
    )

    return rememberSaveable(saver = saver) {
        SnapshotStateList<T>().apply { addAll(initialValue) }
    }
}
