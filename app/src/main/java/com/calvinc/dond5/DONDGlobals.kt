package com.calvinc.dond5

import android.speech.tts.TextToSpeech

object DONDGlobals {
    const val nBoxes = 25
    const val toOpenStart = 6
    const val absOfferMinPct = 0.65
    const val offerMinPctDelta = 0.07
    const val absOfferMaxPct = 0.83
    const val offerMaxPctDelta = 0.0625

    // TTS vars - the TTS instance, a fn to indicate DONDTTSInstance is kosher, and a var to indicate whether or not it should be used
    lateinit var DONDTTSInstance: TextToSpeech
    fun TTSOK(): Boolean = (DONDTTSInstance.voice != null)
    // var TTSOK = false
    var useTTS = true

    @JvmField
    var CalvinCheat = false
    var tmpCheatBox: Int = 0

    const val ouchwordProbability = 0.45f
    @Suppress("TrailingComma")
    val ouchWords = listOf("ouch!! ", "oooh! ","oh wow!! ", "Oh!! ", "ooh, that hurts!! ",)
    @Suppress("unused")
    val moneyinplayWords = listOf("still in play")

    @JvmField
    val Amount = longArrayOf(0, 1, 5, 10, 25, 50, 100, 250, 500, 1000, 2500,
        5000, 10000, 20000, 25000, 30000, 40000, 50000, 60000, 75000, 100000,
        250000, 400000, 500000, 750000, 1000000)
    const val bigMoneyMinimum = 100000 //100000

    fun DONDUtter(speakWords: String ="", queueMode: Int = TextToSpeech.QUEUE_ADD) {
        if (TTSOK() && useTTS) {
            DONDTTSInstance.speak(speakWords, queueMode, null, "ID0")
        }
    }
}