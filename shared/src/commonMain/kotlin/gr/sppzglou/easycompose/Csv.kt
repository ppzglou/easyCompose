package gr.sppzglou.easycompose

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


fun ByteArray.readLines(): List<String> {
    val content = this.decodeToString()
    return content.split("\n").map { it.trimEnd() }
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun <T : Any> getPropertyNamesUsingSerialization(clazz: KClass<T>): List<String> {
    return clazz.serializer().descriptor.elementNames.toList()
}

inline fun <reified T : Any> File.csvToData(clazz: KClass<T>, withHeader: Boolean = true): List<T> {
    val lines = this.data.readLines().toMutableList()
    val elementNames = getPropertyNamesUsingSerialization(clazz)

    if (lines.size == 0) {
        throw Exception("not data found")
    }
    val header = lines.first().split(";")
    if (header.size != elementNames.size) {
        throw Exception("not same columns count\nfile: ${header.size}\nclass: ${elementNames.size}")
    }

    if (withHeader) {
        if (lines.size < 2) {
            throw Exception("not data found")
        }
        header.forEach {
            if (!elementNames.contains(it)) {
                throw Exception("property \"$it\" not exist in class ${clazz.simpleName}")
            }
        }
        lines.removeAt(0)
    }

    val objects = lines.map {
        val data = it.split(";")
        var json = "{\n"
        data.forEachIndexed { index, s ->
            json += "\"${header[index]}\": \"$s\"${if (index == data.size - 1) "\n" else ",\n"}"
        }
        json += "\n}".trimIndent()
        fromJson<T>(json)
    }

    return objects

}