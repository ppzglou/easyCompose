package gr.sppzglou.easycompose

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

fun initContext(app: Application) {
    sharedApplication = app
}

var sharedApplication: Application = Application()

fun getDataStore(context: Context = sharedApplication): PrefsDataStore = createDataStoreSingleton(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)

actual fun getDataStore(): PrefsDataStore = getDataStore(sharedApplication)

@Composable
actual fun rememberDataStore(): PrefsDataStore {
    val context = LocalContext.current
    return remember {
        getDataStore(context)
    }
}

