package com.calvinc.dond5

import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import java.util.Locale

class MainActivity : ComponentActivity() {
// TODO: Move DONDGlobals to companion class?
    val SPLASH_DELAY: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DONDGlobals.DONDTTSInstance = TextToSpeech(this) {
            DONDGlobals.TTSOK = (it == TextToSpeech.SUCCESS)
            if (DONDGlobals.TTSOK) {
                DONDGlobals.DONDTTSInstance.setLanguage(Locale.US)
                // DONDGlobals.DONDTTSInstance.setPitch(0.95f)
                // DONDGlobals.DONDTTSInstance.setSpeechRate(0.8f)
                /*
                val vSet = DONDGlobals.DONDTTSInstance.voices
                val nVox = DONDGlobals.RandLong(0,(vSet.size-1))
                val myVox = vSet.elementAt(nVox)
                DONDGlobals.DONDTTSInstance.setVoice(myVox)
                val announceVox = "I am voice $nVox of ${vSet.size}."
                DONDGlobals.DONDUtter(announceVox)
                */
            }
        }
        fnfinish = this::finish

        setContent {
            BankerCheatsTheme {
                CountDownSplashScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    totalTimeInMillis = SPLASH_DELAY,
                    beforeFinished = {
                        DONDScreens.DONDSplashScreen()
                    },
                    whenFinished = {
                        PlayDOND()
                    }
                )
            }
        }
    }

    override fun onPause() {
        if (DONDGlobals.TTSOK) {
            DONDGlobals.DONDTTSInstance.stop()
            DONDGlobals.DONDTTSInstance.shutdown()
        }
        super.onPause()
    }

    companion object {
        lateinit var fnfinish: () -> Unit
    }

    /* temp code for printing out the voice set
    fun tmpprt(VSet: Set<Voice>) {
        for (v in VSet) {
            System.out.println(v.name+" / "+v.toString())

        }
    }
    */
}


@Composable
// TODO: Move vars from DONDGlobals here
fun PlayDOND() {
    // this var is simply a shortcut, but it's used is several places
    val nBoxes = DONDGlobals.nBoxes

    /*
    var DONDGameState = remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var DONDBoxesVisibility= rememberSaveable { mutableStateMapOf<Int,Boolean>()  }
    var DONDBoxesContents= rememberSaveable { mutableStateMapOf<Int,Int>()  }
    var amountAvail = rememberSaveable { mutableStateMapOf<Int,Boolean>() }
    */
    var DONDGameState:enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var afterAmountState: enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDChooseNextBox) }
    var DONDBoxesVisiblity= remember { mutableStateMapOf<Int,Boolean>()  }
    var DONDBoxesContents= remember { mutableStateMapOf<Int,Int>()  }
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
            offerAccepted = false
            DONDGlobals.intMyBox = 0
            DONDGlobals.lastBoxOpened = 0

            // reset state and global vars
            afterAmountState = enumDONDGameState.DONDChooseNextBox
            hostWords = ""
            wordsCongrat = ""

            for (n in 1..nBoxes) {
                amountAvail[n] = true
                DONDBoxesVisiblity[n] = true
            }
            LoadBoxes(DONDBoxesContents)

            DONDGlobals.roundNum = 0
            DONDGlobals.toOpen = DONDGlobals.toOpenStart
            DONDGlobals.boxesOpened = 0
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
                DONDGlobals.boxesOpened = 0
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
                if (DONDGlobals.roundNum == 1) { hostWords += stringResource(id = R.string.hostWord_YouveChosenBox, DONDGlobals.intMyBox) }

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
            val nBox = DONDGlobals.lastBoxOpened
            hostWords = String.format(
                stringResource(R.string.hostWord_BoxContains),
                nBox,
                DONDGlobals.Amount[DONDBoxesContents[nBox]!!]
            ) + System.lineSeparator()

            if (DONDGlobals.boxesOpened >= DONDGlobals.toOpen) {
                hostWords += stringResource(R.string.hostWord_TimeForOffer)
                DONDGameState = enumDONDGameState.DONDPrepareForOffer
            } else {
                hostWords += stringResource(
                    id = if (DONDGlobals.toOpen - DONDGlobals.boxesOpened == 1) R.string.hostWord_OpenOneMoreBox else R.string.hostWord_OpenAnotherBoxofN,
                    DONDGlobals.toOpen - DONDGlobals.boxesOpened
                )
            }
        }

        enumDONDGameState.DONDShowAmountsLeft -> {
            val nBox = DONDGlobals.lastBoxOpened
            hostWords = if (nBox != 0)
                String.format(
                    stringResource(R.string.hostWord_BoxContains),
                    nBox,
                    DONDGlobals.Amount[DONDBoxesContents[nBox]!!]
                )
            else
                ""
        }

        enumDONDGameState.DONDMakeOffer -> {
            //TODO: Do string resources
            hostWords = stringResource(R.string.presentOffer, CalculateOffer(amountAvail.toMap()))
            hostWords += System.lineSeparator()
            val n = if (DONDGlobals.toOpen < 2) 1 else DONDGlobals.toOpen - 1
            hostWords += if (DONDGlobals.roundNum >= DONDGlobals.FinalRound) "This will be your last offer."
                else "If you don't accept the offer, you must open $n "+(if (n==1) "box" else "boxes")+"."
            hostWords += System.lineSeparator() + "Do you accept this offer?"
        }

        enumDONDGameState.DONDCongratulate -> {
            val heldOnToEnd = (DONDGlobals.roundNum > DONDGlobals.FinalRound)
            val BoxMoney = DONDGlobals.Amount[DONDBoxesContents[DONDGlobals.intMyBox]!!]
            val WonMoney = if (heldOnToEnd) BoxMoney else DONDGlobals.offerMoney
            hostWords = stringResource(R.string.hostWord_Congrats1, WonMoney)

            wordsCongrat = (
                    if (heldOnToEnd)
                        String.format("Your last offer was %1$,d.", DONDGlobals.offerMoney)
                    else
                        String.format("Your Box %1\$d contains %2$,d.", DONDGlobals.intMyBox, BoxMoney)
                    ) + System.lineSeparator() + String.format("Congratulations on winning %1$,d.", WonMoney) + System.lineSeparator()
            var QualityOfDeal = WonMoney.toDouble() / (if (heldOnToEnd) DONDGlobals.offerMoney else BoxMoney)
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
            DONDScreens.MainScreen(
                DONDBoxesVisiblity = DONDBoxesVisiblity.toMap(),
                hostWords = hostWords,
                onBoxOpen = { n ->
                    DONDGlobals.lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    if (DONDGlobals.intMyBox == 0) {  // beginning of game; MyBox has not yet been chosen
                        DONDGlobals.intMyBox = n
                    }
                    DONDGameState = enumDONDGameState.DONDStartNewRound
                },
                miscfunctions = {
                    when (it) {
                        "stop" -> DONDGameState = enumDONDGameState.DONDEndGame
                        "amounts" -> {
                            afterAmountState = DONDGameState
                            DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                        }
                    }
                }
            )
        }
        enumDONDGameState.DONDStartNewRound -> {
            DONDScreens.MainScreen(
                DONDBoxesVisiblity = DONDBoxesVisiblity.toMap(),
                hostWords = hostWords,
                miscfunctions = {
                    when (it) {
                        "stop" -> DONDGameState = enumDONDGameState.DONDEndGame
                        "amounts" -> {
                            afterAmountState = enumDONDGameState.DONDChooseNextBox
                            DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                        }
                    }
                },
                onBoxOpen = { n ->
                    DONDGlobals.lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    if (DONDGlobals.intMyBox == 0) {  // beginning of game; MyBox has not yet been chosen
                        /* in this state, this branch should not be true */
                        DONDGlobals.intMyBox = n
                        DONDGameState = enumDONDGameState.DONDStartNewRound
                    } else {
                        DONDGlobals.boxesOpened++
                        amountAvail[DONDBoxesContents[n]!!] = false
                        afterAmountState = enumDONDGameState.DONDChooseNextBox
                        DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                    }
                },
            )

        }
        enumDONDGameState.DONDPrepareForOffer -> {
            DONDScreens.TimeForOffer(
                hostWords = hostWords
            ) {
                DONDGameState = enumDONDGameState.DONDMakeOffer
            }
        }
        enumDONDGameState.DONDMakeOffer -> {
            DONDScreens.OfferScreen(
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
            DONDScreens.MainScreen(
                DONDBoxesVisiblity = DONDBoxesVisiblity.toMap(),
                hostWords = hostWords,
                miscfunctions = {
                    when (it) {
                        "stop" -> DONDGameState = enumDONDGameState.DONDEndGame
                        "amounts" -> {
                            afterAmountState = DONDGameState
                            DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                        }
                    }
                },
                onBoxOpen = { n ->
                    DONDGlobals.lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    DONDGlobals.boxesOpened++
                    amountAvail[DONDBoxesContents[n]!!] = false
                    afterAmountState = enumDONDGameState.DONDChooseNextBox
                    DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                },
            )
        }
        enumDONDGameState.DONDShowAmountsLeft -> {
            DONDScreens.MoneyListScreen(
                hostWords = hostWords,
                amountAvail = amountAvail.toMap(),
                onOKClick = {
                    DONDGameState = afterAmountState
                }
            )
        }
        enumDONDGameState.DONDCongratulate -> {
            DONDScreens.MainScreen(
                DONDBoxesVisiblity = DONDBoxesVisiblity.toMap(),
                hostWords = hostWords,
                congrats = wordsCongrat,
                miscfunctions = {
                    when (it) {
                        "stop" -> DONDGameState = enumDONDGameState.DONDEndGame
                        "again" -> DONDGameState = enumDONDGameState.DONDActivateGame
                    }
                },
                onBoxOpen = { },    // intentionally empty - clicking a box should do nothing
                DONDBoxesContents = DONDBoxesContents.toMap()
            )

        }
        enumDONDGameState.DONDGetUserEndgameDecision -> {}
        enumDONDGameState.DONDEndGame -> {  MainActivity.fnfinish() }

    }
}

// @Composable
fun LoadBoxes(DONDBoxesContents:MutableMap<Int,Int>) {
    // this var is simply a shortcut, but it's used is several places
    val nBoxes = DONDGlobals.nBoxes
    val NUM_SHUFFLES = 1000
    val tmpCheatBox = nBoxes    // tmpCheatBox is the box that holds the 1 000 000
    for (n in 1..nBoxes) {
        DONDBoxesContents[n] = n
    }
    for (n in 1..NUM_SHUFFLES) {
        var p1: Int
        var p2: Int
        @Suppress("JoinDeclarationAndAssignment") var tmp: Int  // cannot be assigned here - p1, p2 must have values first
        do {
            p1 = DONDGlobals.RandLong(1, nBoxes)
            p2 = DONDGlobals.RandLong(1, nBoxes)
        } while (p1 == p2)
        tmp = DONDBoxesContents[p1]!!
        DONDBoxesContents[p1] = DONDBoxesContents[p2]!!
        DONDBoxesContents[p2] = tmp
        if (DONDBoxesContents[p1]!! == nBoxes) {
            DONDGlobals.tmpCheatBox = p1
        }
        if (DONDBoxesContents[p2]!! == nBoxes) {
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
    val nBoxes = DONDGlobals.nBoxes
    for (n in 1..nBoxes) {
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

