package gr.sppzglou.easycompose


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


/**
import kotlinx.serialization.Serializable
@Serializable
data class User(val name: String, val age: Int)

fun example() {
    val user = User("John", 30)
    val jsonString = toJson(user)
    val deserializedUser: User = fromJson(jsonString)
}
*/



inline fun <reified T> toJson(obj: T): String {
    val json = Json { prettyPrint = true }
    return json.encodeToString(obj)
}

inline fun <reified T> fromJson(jsonString: String): T {
    return Json.decodeFromString(jsonString)
}