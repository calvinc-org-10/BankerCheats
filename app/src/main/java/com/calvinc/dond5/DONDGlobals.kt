package com.calvinc.dond5

//TODO:  Move DONDGlobals to MainActivity.companion?
object DONDGlobals {
    const val nCases = 25
    const val toOpenStart = 6
    const val FinalRound = 8
    const val absOfferMinPct = 0.65
    const val offerMinPctDelta = 0.07
    const val absOfferMaxPct = 0.83
    const val offerMaxPctDelta = 0.0625

    /*
    changes to DONDCases drive recomposition of DONDComposables.MainScreen.
    Therefore, this variable is part of the StartActivity companion object and is passed to MainScreen
    Similarly with amountAvail and MoneyListScreen

    var DONDCases = arrayOfNulls<DONDCase>(nCases + 1)
    var amountAvail = BooleanArray(nCases + 1)
    */

    // var DONDGameState: enumDONDGameState = enumDONDGameState.DONDState0  // this is OK as local in PlayDOND

    @JvmField
    var intMyCase = 0
    @JvmField
    var roundNum = 0
    @JvmField
    var toOpen = 0
    var lastCaseOpened = 0
    var casesOpened = 0
    @JvmField
    var offerMinPct = 0.0
    @JvmField
    var offerMaxPct = 0.0
    @JvmField
    var offerMoney: Long = 0
    @JvmField
    var SplashDone = false
    var CalvinCheat = false
    var tmpCheatBox: Int = 0

    @JvmField
    val Amount = longArrayOf(0, 1, 5, 10, 25, 50, 100, 250, 500, 1000, 2500,
        5000, 10000, 20000, 25000, 30000, 40000, 50000, 60000, 75000, 100000,
        250000, 400000, 500000, 750000, 1000000)

    @JvmStatic
    fun RandLong(valMin: Int, valMax: Int): Int {
        val rndRange = valMax + 1 - valMin
        return valMin + (Math.random() * rndRange).toInt()
    }
}