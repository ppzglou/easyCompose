package gr.sppzglou.easycompose

open class NoInternetException : Exception("No internet connection!")

open class CustomExceptionTxt(var txt: String) : Exception()