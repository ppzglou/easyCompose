package gr.sppzglou.easycompose

val String?.isNullOrEmptyOrBlank: Boolean
    get() = isNullOrEmpty() || this.isBlank()


val String?.isNotNullOrEmptyOrBlank: Boolean
    get() = !this.isNullOrBlank() && this.isNotEmpty()