package gr.sppzglou.easycompose

data class File(
    val name: String,
    val path: String,
    val ext: String,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as File

        if (name != other.name) return false
        if (path != other.path) return false
        if (ext != other.ext) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + ext.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}