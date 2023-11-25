package com.calvinc.dond5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.frazo.splashscreens.CountDownSplashScreen
import com.calvinc.dond5.ui.theme.BankerCheatsTheme

class MainActivity : ComponentActivity() {
// TODO: Move DONDGlobals to companion class?
    val SPLASH_DELAY: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fnfinish = this::finish

        setContent {
            BankerCheatsTheme {
                CountDownSplashScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    totalTimeInMillis = SPLASH_DELAY,
                    beforeFinished = {
                        DONDComposables.DONDSplashScreen()
                    },
                    whenFinished = {
                        PlayDOND()
                    }
                )
            }
        }
    }

    companion object {
        lateinit var fnfinish: () -> Unit
    }
}

@Composable
// TODO: Move vars from DONDGlobals here
fun PlayDOND() {
    // this var is simply a shortcut, but it's used is several places
    val nBoxes = DONDGlobals.nCases

    /*
    var DONDGameState = remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var DONDCasescaseVisible= rememberSaveable { mutableStateMapOf<Int,Boolean>()  }
    var DONDCasescaseContents= rememberSaveable { mutableStateMapOf<Int,Int>()  }
    var amountAvail = rememberSaveable { mutableStateMapOf<Int,Boolean>() }
    */
    var DONDGameState:enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var DONDCasescaseVisible= remember { mutableStateMapOf<Int,Boolean>()  }
    var DONDCasescaseContents= remember { mutableStateMapOf<Int,Int>()  }
    var amountAvail = remember { mutableStateMapOf<Int,Boolean>() }

    var hostWords: String = ""
    var wordsCongrat = ""
    var offerAccepted: Boolean

    // set up variables
    when (DONDGameState) {
        enumDONDGameState.DONDInit,
        enumDONDGameState.DONDActivateGame -> {
            // do I need to create and check a flag to see if gameStarted ?
            DONDGlobals.offerMinPct = DONDGlobals.absOfferMinPct
            DONDGlobals.offerMaxPct = DONDGlobals.absOfferMaxPct
            DONDGlobals.offerMoney = 0
            DONDGlobals.intMyCase = 0

            for (n in 1..nBoxes) {
                amountAvail[n] = true
                DONDCasescaseVisible[n] = true
            }
            LoadCases(DONDCasescaseContents)

            DONDGlobals.roundNum = 0
            DONDGlobals.toOpen = DONDGlobals.toOpenStart
            DONDGlobals.casesOpened = 0
            val hostWords_Cheat =
                if (DONDGlobals.CalvinCheat) "Cheat: " + DONDGlobals.tmpCheatBox + System.lineSeparator() else ""
            hostWords =
                hostWords_Cheat + stringResource(id = R.string.hostWord_ChooseABox1)

            DONDGameState = enumDONDGameState.DONDPickMyBox
        }

        enumDONDGameState.DONDPickMyBox -> {
            val hostWords_Cheat =
                if (DONDGlobals.CalvinCheat) "Cheat: " + DONDGlobals.tmpCheatBox + System.lineSeparator() else ""
            hostWords =
                hostWords_Cheat + stringResource(id = R.string.hostWord_ChooseABox1)
        }

        enumDONDGameState.DONDStartNewRound -> {
            DONDGlobals.roundNum++
            if (DONDGlobals.roundNum <= DONDGlobals.FinalRound) {
                DONDGlobals.casesOpened = 0
                hostWords = ""
                if (DONDGlobals.roundNum > 1) {
                    hostWords = stringResource(
                        R.string.hostWord_OfferRefused,
                        DONDGlobals.offerMoney
                    ) + System.lineSeparator()

                    // adjust bank offer constraints
                    DONDGlobals.offerMinPct += DONDGlobals.offerMinPctDelta
                    DONDGlobals.offerMaxPct += DONDGlobals.offerMaxPctDelta

                    if (DONDGlobals.toOpen > 1) {
                        DONDGlobals.toOpen--
                    }
                }
                //TODO: Look at Plural resources or MessageFormat class
                hostWords += stringResource(
                    if (DONDGlobals.toOpen == 1) R.string.hostWord_TimeToOpen1Box else R.string.hostWord_TimeToOpenBoxes,
                    DONDGlobals.toOpen
                )
            } else {
                DONDGameState = enumDONDGameState.DONDCongratulate
            }
        }

        enumDONDGameState.DONDChooseNextBox -> {
            val nCase = DONDGlobals.lastCaseOpened
            hostWords = String.format(
                stringResource(R.string.hostWord_CaseContains),
                nCase,
                DONDGlobals.Amount[DONDCasescaseContents[nCase]!!]
            ) + System.lineSeparator()

            if (DONDGlobals.casesOpened >= DONDGlobals.toOpen) {
                hostWords += stringResource(R.string.hostWord_TimeForOffer)
                DONDGameState = enumDONDGameState.DONDPrepareForOffer
            } else {
                hostWords += stringResource(
                    id = if (DONDGlobals.toOpen - DONDGlobals.casesOpened == 1) R.string.hostWord_OpenOneMoreBox else R.string.hostWord_OpenAnotherBoxofN,
                    DONDGlobals.toOpen - DONDGlobals.casesOpened
                )
            }
        }

        enumDONDGameState.DONDShowAmountsLeft -> {
            val nCase = DONDGlobals.lastCaseOpened
            hostWords = String.format(
                stringResource(R.string.hostWord_CaseContains),
                nCase,
                DONDGlobals.Amount[DONDCasescaseContents[nCase]!!]
            )
        }

        enumDONDGameState.DONDMakeOffer -> {
            //TODO: Do string resources
            hostWords = stringResource(R.string.presentOffer, CalculateOffer(amountAvail.toMap()))
            hostWords += System.lineSeparator()
            val n = if (DONDGlobals.toOpen < 2) 1 else DONDGlobals.toOpen - 1
            hostWords += if (DONDGlobals.roundNum >= DONDGlobals.FinalRound) "This will be your last offer."
                else "If you don't accept the offer, you must open $n case(s)."
            hostWords += System.lineSeparator() + "Do you accept this offer?"
        }

        enumDONDGameState.DONDCongratulate -> {
            val heldOnToEnd = (DONDGlobals.roundNum > DONDGlobals.FinalRound)
            val CaseMoney = DONDGlobals.Amount[DONDCasescaseContents[DONDGlobals.intMyCase]!!]
            val WonMoney = if (heldOnToEnd) CaseMoney else DONDGlobals.offerMoney
            hostWords = stringResource(R.string.hostWord_Congrats1, WonMoney)

            wordsCongrat = (
                    if (heldOnToEnd)
                        String.format("Your last offer was %1$,d.", DONDGlobals.offerMoney)
                    else
                        String.format("Your Box %1\$d contains %2$,d.", DONDGlobals.intMyCase, CaseMoney)
                    ) + System.lineSeparator() + String.format("Congratulations on winning %1$,d.", WonMoney) + System.lineSeparator()
            var QualityOfDeal = WonMoney.toDouble() / (if (heldOnToEnd) DONDGlobals.offerMoney else CaseMoney)
            if (QualityOfDeal > 200) {
                // incredible
                wordsCongrat += stringResource(R.string.QualityOfDeal_incredible)
            } else if (QualityOfDeal > 30) {
                // awesome
                wordsCongrat += stringResource(R.string.QualityOfDeal_awesome)
            } else if (QualityOfDeal > 5) {
                // great
                wordsCongrat += stringResource(R.string.QualityOfDeal_great)
            } else if (QualityOfDeal > 1) {
                // good
                wordsCongrat += stringResource(R.string.QualityOfDeal_good)
            } else if (QualityOfDeal < 1) {
                // too bad
                wordsCongrat += stringResource(R.string.QualityOfDeal_notgood)
            }
        }
        enumDONDGameState.DONDGetUserEndgameDecision -> {}
        enumDONDGameState.DONDEndGame -> {}

        else -> {}
    }

    // show the correct screen
    when (DONDGameState) {
        enumDONDGameState.DONDInit,
        enumDONDGameState.DONDActivateGame -> {

        }
        enumDONDGameState.DONDPickMyBox -> {
            DONDComposables.MainScreen(
                DONDCasescaseVisible = DONDCasescaseVisible.toMap(),
                hostWords = hostWords,
                onBoxOpen = { n ->
                    DONDGlobals.lastCaseOpened = n
                    DONDCasescaseVisible[n] = false
                    if (DONDGlobals.intMyCase == 0) {  // beginning of game; MyCase has not yet been chosen
                        DONDGlobals.intMyCase = n
                    }
                    DONDGameState = enumDONDGameState.DONDStartNewRound
                },
                miscfunctions = {
                    when (it) {
                        "stop" -> MainActivity.fnfinish()
                        "amounts" -> {}
                    }
                }
            )
        }
        enumDONDGameState.DONDStartNewRound -> {
            DONDComposables.MainScreen(
                DONDCasescaseVisible = DONDCasescaseVisible.toMap(),
                hostWords = hostWords,
                miscfunctions = {
                    when (it) {
                        "stop" -> MainActivity.fnfinish()
                        "amounts" -> {}
                    }
                },
                onBoxOpen = { n ->
                    DONDGlobals.lastCaseOpened = n
                    DONDCasescaseVisible[n] = false
                    if (DONDGlobals.intMyCase == 0) {  // beginning of game; MyCase has not yet been chosen
                        /* in this state, this branch should not be true */
                        DONDGlobals.intMyCase = n
                        DONDGameState = enumDONDGameState.DONDStartNewRound
                    } else {
                        DONDGlobals.casesOpened++
                        amountAvail[DONDCasescaseContents[n]!!] = false
                        DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                    }
                },
            )

        }
        enumDONDGameState.DONDPrepareForOffer -> {
            DONDComposables.TimeForOffer(
                hostWords = hostWords
            ) {
                DONDGameState = enumDONDGameState.DONDMakeOffer
            }
        }
        enumDONDGameState.DONDMakeOffer -> {
            DONDComposables.OfferScreen(
                theOffer = hostWords,
                playerAnswer = {
                    offerAccepted = it
                    if (offerAccepted) {
                        // congratulate and leave
                        DONDGameState = enumDONDGameState.DONDCongratulate
                    } else {
                        DONDGameState = enumDONDGameState.DONDStartNewRound
                    }
                }
            )
        }
        enumDONDGameState.DONDChooseNextBox -> {
            DONDComposables.MainScreen(
                DONDCasescaseVisible = DONDCasescaseVisible.toMap(),
                hostWords = hostWords,
                miscfunctions = {
                    when (it) {
                        "stop" -> MainActivity.fnfinish()
                        "amounts" -> {}
                    }
                },
                onBoxOpen = { n ->
                    DONDGlobals.lastCaseOpened = n
                    DONDCasescaseVisible[n] = false
                    DONDGlobals.casesOpened++
                    amountAvail[DONDCasescaseContents[n]!!] = false
                    DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                },
            )
        }
        enumDONDGameState.DONDShowAmountsLeft -> {
            DONDComposables.MoneyListScreen(
                hostWords = hostWords,
                amountAvail = amountAvail.toMap(),
                onOKClick = {
                    DONDGameState = enumDONDGameState.DONDChooseNextBox
                }
            )
        }
        enumDONDGameState.DONDCongratulate -> {
            DONDComposables.MainScreen(
                DONDCasescaseVisible = DONDCasescaseVisible.toMap(),
                hostWords = hostWords,
                congrats = wordsCongrat,
                miscfunctions = {
                    when (it) {
                        "stop" -> MainActivity.fnfinish()
                        "again" -> {}
                    }
                },
                onBoxOpen = { },
                DONDCasescaseContents = DONDCasescaseContents.toMap()
            )

        }
        enumDONDGameState.DONDGetUserEndgameDecision -> {}
        enumDONDGameState.DONDEndGame -> {}

    }
}

// @Composable
fun LoadCases(DONDCasescaseContents:MutableMap<Int,Int>) {
    // this var is simply a shortcut, but it's used is several places
    val nBoxes = DONDGlobals.nCases
    val NUM_SHUFFLES = 1000
    val tmpCheatBox = nBoxes    // tmpCheatBox is the box that holds the 1 000 000
    for (n in 1..nBoxes) {
        DONDCasescaseContents[n] = n
    }
    for (n in 1..NUM_SHUFFLES) {
        var p1: Int
        var p2: Int
        @Suppress("JoinDeclarationAndAssignment") var tmp: Int  // cannot be assigned here - p1, p2 must have values first
        do {
            p1 = DONDGlobals.RandLong(1, nBoxes)
            p2 = DONDGlobals.RandLong(1, nBoxes)
        } while (p1 == p2)
        tmp = DONDCasescaseContents[p1]!!
        DONDCasescaseContents[p1] = DONDCasescaseContents[p2]!!
        DONDCasescaseContents[p2] = tmp
        if (DONDCasescaseContents[p1]!! == nBoxes) {
            DONDGlobals.tmpCheatBox = p1
        }
        if (DONDCasescaseContents[p2]!! == nBoxes) {
            DONDGlobals.tmpCheatBox = p2
        }
    }
}

// @Composable
private fun CalculateOffer(
    amountAvail:Map<Int,Boolean>
): Long {
    var TotalMoney = 0.0
    var Divider = 0.0
    var Pct: Double
    val generosityFactor = 1.15
    val nCases = DONDGlobals.nCases
    for (n in 1..nCases) {
        if (amountAvail[n]!!) {
            TotalMoney += DONDGlobals.Amount[n].toDouble()
            Divider += 1.0
            if (n <= 5) {
                Divider += .25
            } else if (n <= 9) {
                Divider += .25 + .5
            } else if (n >= 25) {
                Divider -= .05 + .075 + .1
            } else if (n >= 23) {
                Divider -= .05 + .075
            } else if (n >= 21) {
                Divider -= .05
            }
        }
    }
    if (Divider < 0.75) {
        Divider = 0.75
    }
    do {
        Pct = DONDGlobals.offerMinPct + Math.random() * generosityFactor * (DONDGlobals.offerMaxPct - DONDGlobals.offerMinPct)
    } while (DONDGlobals.offerMinPct > Pct || DONDGlobals.offerMaxPct < Pct)
    DONDGlobals.offerMoney = (TotalMoney * Pct / Divider).toLong()
    return DONDGlobals.offerMoney
}

