package com.calvinc.dond5

object hostWords {
    var screen = ""
    var spoken = ""

    /*
    I flirted with the idea of using a set function, but decided against it.
    Convenience and readability isn't really any better than hostWords.screen = whatever, etc
    */
    fun addToall(words:String = "") {
        screen += words
        spoken += words
    }
    fun setAll(words: String = "") {
        screen = words
        spoken = words
    }
    fun clearAll() {
        setAll()
    }
}