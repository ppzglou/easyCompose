package gr.sppzglou.easycompose

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.getScopeName

data class AppHandler(
    val inProgress: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val error: MutableStateFlow<Exception?> = MutableStateFlow(null),
    var threadsCount: Int = 0
) {

    fun startProgress() {
        threadsCount++
        if (threadsCount == 1)
            inProgress.value = true
    }

    fun endProgress() {
        threadsCount--
        if (threadsCount == 0)
            inProgress.value = false
    }

    fun setError(e: Exception?) {
        error.value = e
    }

    fun clearError() {
        error.value = null
    }

    suspend fun <T> request(
        loader: Boolean = false,
        onFailure: () -> Unit = {},
        finally: suspend () -> Unit = {},
        block: suspend () -> T
    ): T? = try {
        if (loader) {
            startProgress()
            delay(100)
        }
        log("Handler start", block.getScopeName().toString())
        block()
    } catch (e: Exception) {
        logE(e)
        onFailure()
        null
    } finally {
        log("Handler end", block.getScopeName().toString())
        finally()
        if (loader) endProgress()
    }

    private fun logE(e: Exception) {

        val eTxt = e.toString()

        log("Handler error", eTxt)
        if (e !is CancellationException)
            setError(e)
    }

}