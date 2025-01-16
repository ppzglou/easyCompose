package gr.sppzglou.easycompose
import kotlinx.cinterop.*
import platform.CoreFoundation.CFRelease
import platform.SystemConfiguration.*

actual object NetworkUtils {
    @OptIn(ExperimentalForeignApi::class)
    actual fun isConnected(): Boolean {
        val reachability = SCNetworkReachabilityCreateWithName(null, "www.google.com") ?: return false

        return memScoped {
            val flags = alloc<UIntVar>()
            val success = SCNetworkReachabilityGetFlags(reachability, flags.ptr)
            CFRelease(reachability)

            if (success) {
                val isReachable = flags.value and kSCNetworkFlagsReachable != 0u
                val requiresConnection = flags.value and kSCNetworkFlagsConnectionRequired != 0u
                isReachable && !requiresConnection
            } else {
                false
            }
        }
    }
}