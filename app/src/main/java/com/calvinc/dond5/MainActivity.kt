package com.calvinc.dond5

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import br.com.frazo.splashscreens.SplashScreen
import com.calvinc.dond5.DONDGlobals.Amount
import com.calvinc.dond5.DONDGlobals.CalvinCheat
import com.calvinc.dond5.DONDGlobals.DONDTTSInstance
import com.calvinc.dond5.DONDGlobals.absOfferMaxPct
import com.calvinc.dond5.DONDGlobals.absOfferMinPct
import com.calvinc.dond5.DONDGlobals.boxesOpened
import com.calvinc.dond5.DONDGlobals.intMyBox
import com.calvinc.dond5.DONDGlobals.lastBoxOpened
import com.calvinc.dond5.DONDGlobals.nBoxes
import com.calvinc.dond5.DONDGlobals.offerMaxPct
import com.calvinc.dond5.DONDGlobals.offerMaxPctDelta
import com.calvinc.dond5.DONDGlobals.offerMinPct
import com.calvinc.dond5.DONDGlobals.offerMinPctDelta
import com.calvinc.dond5.DONDGlobals.offerMoney
import com.calvinc.dond5.DONDGlobals.roundNum
import com.calvinc.dond5.DONDGlobals.tmpCheatBox
import com.calvinc.dond5.DONDGlobals.toOpen
import com.calvinc.dond5.DONDGlobals.toOpenStart
import com.calvinc.dond5.ui.theme.BankerCheatsTheme
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    // val SPLASH_DELAY: Long = 5000
    val DONDFeedbackEmail = "calvinc404@gmail.com"
    val DONDFeedbackSubject = "Feedback - BankerCheats"
    val DONDFeedbackMailBody = "\"The Banker Will Cheat You Now\" is Awesome!!"

    var hostWords: hostDialogue = hostDialogue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startTTS()
        // fnfinish = this::finish
        // mainContext = applicationContext

        setContent {
            BankerCheatsTheme {
                var finished by remember { mutableStateOf(false) }
                SplashScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    finished = finished,
                    beforeFinished = {
                        val DONDAudioTheme =
                            MediaPlayer.create(LocalView.current.context, R.raw.bankercheatintrov1)
                        DONDAudioTheme.setOnCompletionListener {
                            finished = true
                            it.release()
                        }
                        DONDScreens.DONDSplashScreen {
                            finished = true
                            DONDAudioTheme.release()
                        }
                        DONDAudioTheme.start()
                    },
                    whenFinished = {
                        PlayDOND()
                    }
                )
            }
        }
    }

    override fun onPause() {
        stopTTS()
        super.onPause()
    }

    override fun onRestart() {
        startTTS()
        super.onRestart()
    }

    private fun startTTS() {
        DONDTTSInstance = TextToSpeech(this) {
            DONDGlobals.TTSOK = (it == TextToSpeech.SUCCESS)
            if (DONDGlobals.TTSOK) {
                DONDTTSInstance.setLanguage(Locale.US)
            }
        }
    }

    private fun stopTTS() {
        if (DONDGlobals.TTSOK) {
            DONDTTSInstance.stop()
            DONDTTSInstance.shutdown()
        }
    }

    companion object {
        // lateinit var fnfinish: () -> Unit
        // lateinit var mainContext: Context
    }

    /* temp code for printing out the voice set
    fun tmpprt(VSet: Set<Voice>) {
        for (v in VSet) {
            System.out.println(v.name+" / "+v.toString())

        }
    }
    */


    @Composable
    fun PlayDOND() {
        /*
    var DONDGameState = remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var DONDBoxesVisibility= rememberSaveable { mutableStateMapOf<Int,Boolean>()  }
    var DONDBoxesContents= rememberSaveable { mutableStateMapOf<Int,Int>()  }
    var amountAvail = rememberSaveable { mutableStateMapOf<Int,Boolean>() }
    */
        var DONDGameState: enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDInit) }
        var afterAmountState: enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDChooseNextBox) }
        var afterHowToPlayState: enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDChooseNextBox) }
        val DONDBoxesVisiblity = remember { mutableStateMapOf<Int, Boolean>() }
        val DONDBoxesContents = remember { mutableStateMapOf<Int, Int>() }
        val amountAvail = remember { mutableStateMapOf<Int, Boolean>() }
        var AmountListPeekedOnly by remember { mutableStateOf(false) }
        var offerAccepted by remember { mutableStateOf(false) }

        // var hostWords = ""
        var wordsCongrat = ""

        @Suppress("unused")
        fun nClosedCases(): Int {
            var n = 0

            for (i in 1..nBoxes) {
                if (amountAvail[i]!!) n++
            }

            return n
        }

        // set up variables
        when (DONDGameState) {
            enumDONDGameState.DONDInit,
            enumDONDGameState.DONDActivateGame -> {
                offerMinPct = absOfferMinPct
                offerMaxPct = absOfferMaxPct
                offerMoney = 0
                offerAccepted = false
                intMyBox = 0
                lastBoxOpened = 0

                // reset state and global vars
                afterAmountState = enumDONDGameState.DONDChooseNextBox
                AmountListPeekedOnly = false
                with(hostWords) {
                    screen = ""
                    spoken = ""
                }
                wordsCongrat = ""

                for (n in 1..nBoxes) {
                    amountAvail[n] = true
                    DONDBoxesVisiblity[n] = true
                }
                LoadBoxes(DONDBoxesContents)

                roundNum = 0
                toOpen = toOpenStart + 1  // 1 added because 1 will be subtracted at start of round
                boxesOpened = 0
                val hostWords_Cheat =
                    if (CalvinCheat) "Cheat: " + tmpCheatBox + "." + System.lineSeparator() else ""
                with(hostWords) {
                    screen = hostWords_Cheat + stringResource(id = R.string.hostWord_ChooseABox1)
                    spoken = screen
                }

                DONDGameState = enumDONDGameState.DONDPickMyBox
            }

            enumDONDGameState.DONDPickMyBox -> {
                val hostWords_Cheat =
                    if (CalvinCheat) "Cheat: " + tmpCheatBox + "." + System.lineSeparator() else ""
                with(hostWords) {
                    screen = hostWords_Cheat + stringResource(id = R.string.hostWord_ChooseABox1)
                    spoken = screen
                }
            }

            enumDONDGameState.DONDStartNewRound -> {
                roundNum++
                if (nClosedCases() > 2) {
                    boxesOpened = 0
                    hostWords.clearAll()
                    if (roundNum > 1) {
                        hostWords.setAll(
                            stringResource(
                                R.string.hostWord_OfferRefused,
                                offerMoney
                            ) + System.lineSeparator()
                        )
                    }

                    // adjust bank offer constraints
                    offerMinPct += offerMinPctDelta
                    offerMaxPct += offerMaxPctDelta

                    if (toOpen > 1) {
                        toOpen--
                    }
                    if (roundNum == 1) {
                        hostWords.addToall(
                            stringResource(
                                id = R.string.hostWord_YouveChosenBox,
                                intMyBox
                            )
                        )
                    }

                    //TODO: Look at Plural resources or MessageFormat class
                    hostWords.addToall(
                        stringResource(
                            if (toOpen == 1) R.string.hostWord_TimeToOpen1Box else R.string.hostWord_TimeToOpenBoxes,
                            toOpen
                        )
                    )
                } else {
                    DONDGameState = enumDONDGameState.DONDCongratulate
                }
            }

            enumDONDGameState.DONDChooseNextBox -> {
                val nBox = lastBoxOpened
                with(hostWords) {
                    val moneyInBox =
                        if (nBox != 0 && nBox != intMyBox) Amount[DONDBoxesContents[nBox]!!] else 0
                    screen = if (nBox != intMyBox)
                        String.format(
                            stringResource(R.string.hostWord_BoxContains),
                            nBox,
                            moneyInBox
                        ) + System.lineSeparator()
                    else
                    // lastOpenedBox was MyBox; we prolly opened it and checked Amounts in play.  Don't reveal contents of MyBox
                        ""

                    spoken = ""
                }

                if (boxesOpened >= toOpen) {
                    hostWords.addToall(stringResource(id = R.string.hostWord_TimeForOffer))
                    DONDGameState = enumDONDGameState.DONDPrepareForOffer
                } else {
                    hostWords.addToall(
                        stringResource(
                            id = if (toOpen - boxesOpened == 1) R.string.hostWord_OpenOneMoreBox else R.string.hostWord_OpenAnotherBoxofN,
                            toOpen - boxesOpened
                        )
                    )
                }
            }

            enumDONDGameState.DONDShowAmountsLeft -> {
                val nBox = lastBoxOpened
                with(hostWords) {
                    val moneyInBox =
                        if (nBox != 0 && nBox != intMyBox) Amount[DONDBoxesContents[nBox]!!] else 0
                    screen = if (nBox != 0 && !AmountListPeekedOnly)
                        String.format(
                            stringResource(R.string.hostWord_BoxContains),
                            nBox,
                            moneyInBox
                        )
                    else
                        ""

                    spoken = ""
                    if (!AmountListPeekedOnly)
                        if (moneyInBox >= DONDGlobals.bigMoneyMinimum)
                            if (Random.nextFloat() <= (DONDGlobals.ouchwordProbability + (0.6f * moneyInBox) / (Amount[nBoxes] - DONDGlobals.bigMoneyMinimum)))
                                spoken = DONDGlobals.ouchWords[Random.nextInt(DONDGlobals.ouchWords.size)]
                    spoken += screen
                }
            }

            enumDONDGameState.DONDMakeOffer -> {
                //TODO: Do string resources
                with(hostWords) {
                    screen =
                        stringResource(R.string.presentOffer, CalculateOffer(amountAvail.toMap()))
                    screen += System.lineSeparator()
                    val n = if (toOpen < 2) 1 else toOpen - 1
                    screen += if (nClosedCases() <= 2) stringResource(id = R.string.hostWord_ThisIsLastOffer, intMyBox)
                        else stringResource(id = R.string.hostWord_IfNoAcceptYouMustOpenBoxes_pt1, n) + (if (n == 1) "box" else "boxes") + "."
                    screen += System.lineSeparator() + stringResource(id = R.string.hostWord_DoYouAcceptOffer)

                    spoken = screen
                }
            }

            enumDONDGameState.DONDCongratulate -> {
                val heldOnToEnd = (nClosedCases() <= 2)
                val BoxMoney = Amount[DONDBoxesContents[intMyBox]!!]
                val WonMoney = if (heldOnToEnd && !offerAccepted) BoxMoney else offerMoney
                with(hostWords) {
                    screen = stringResource(R.string.hostWord_Congrats1, WonMoney)
                    spoken = ""
                }

                wordsCongrat = (
                        if (heldOnToEnd && !offerAccepted)
                            String.format("Your last offer was %1$,d.", offerMoney)
                        else
                            String.format("Your Box %1\$d contains %2$,d.", intMyBox, BoxMoney)
                        ) +
                        System.lineSeparator() +
                        String.format("Congratulations on winning %1$,d.",WonMoney) +
                        System.lineSeparator()
                val QualityOfDeal =
                    WonMoney.toDouble() / (if (heldOnToEnd && !offerAccepted) offerMoney else BoxMoney)
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

            enumDONDGameState.DONDShowRules -> {}

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
                        lastBoxOpened = n
                        DONDBoxesVisiblity[n] = false
                        if (intMyBox == 0) {  // beginning of game; MyBox has not yet been chosen
                            intMyBox = n
                        }
                        DONDGameState = enumDONDGameState.DONDStartNewRound
                    },
                    miscfunctions = {
                        when (it) {
                            "stop" -> DONDGameState = enumDONDGameState.DONDEndGame
                            "amounts" -> {
                                afterAmountState = DONDGameState
                                AmountListPeekedOnly = true
                                DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                            }

                            "rules" -> {
                                afterHowToPlayState = DONDGameState
                                DONDGameState = enumDONDGameState.DONDShowRules
                            }
                            "feedback" -> { sendDONDFeedback() }
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
                                AmountListPeekedOnly = true
                                DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                            }

                            "rules" -> {
                                afterHowToPlayState = enumDONDGameState.DONDChooseNextBox
                                DONDGameState = enumDONDGameState.DONDShowRules
                            }
                            "feedback" -> { sendDONDFeedback() }
                        }
                    },
                    onBoxOpen = { n ->
                        lastBoxOpened = n
                        DONDBoxesVisiblity[n] = false
                        if (intMyBox == 0) {  // beginning of game; MyBox has not yet been chosen
                            /* in this state, this branch should not be true */
                            intMyBox = n
                            DONDGameState = enumDONDGameState.DONDStartNewRound
                        } else {
                            boxesOpened++
                            amountAvail[DONDBoxesContents[n]!!] = false
                            afterAmountState = enumDONDGameState.DONDChooseNextBox
                            AmountListPeekedOnly = false
                            DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                        }
                    },
                )

            }

            enumDONDGameState.DONDPrepareForOffer -> {
                DONDScreens.TimeForOffer( hostWords ) {
                    DONDGameState = enumDONDGameState.DONDMakeOffer
                }
            }

            enumDONDGameState.DONDMakeOffer -> {
                DONDScreens.OfferScreen( hostWords ) {
                    offerAccepted = it
                    DONDGameState =
                        if (offerAccepted) enumDONDGameState.DONDCongratulate else enumDONDGameState.DONDStartNewRound
                }
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
                                AmountListPeekedOnly = true
                                DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                            }

                            "rules" -> {
                                afterHowToPlayState = DONDGameState
                                DONDGameState = enumDONDGameState.DONDShowRules
                            }

                            "feedback" -> { sendDONDFeedback() }
                        }
                    },
                    onBoxOpen = { n ->
                        lastBoxOpened = n
                        DONDBoxesVisiblity[n] = false
                        boxesOpened++
                        amountAvail[DONDBoxesContents[n]!!] = false
                        afterAmountState = enumDONDGameState.DONDChooseNextBox
                        AmountListPeekedOnly = false
                        DONDGameState = enumDONDGameState.DONDShowAmountsLeft
                    },
                )
            }

            enumDONDGameState.DONDShowAmountsLeft -> {
                val nBox = lastBoxOpened
                DONDScreens.MoneyListScreen(
                    DONDBoxesContents,  //TODO: Remove in final build - for temp debugging only
                    hostWords = hostWords,
                    AmountOpened = if (nBox != intMyBox) DONDBoxesContents[nBox]!! else 0,
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
                            "rules" -> {
                                afterHowToPlayState = DONDGameState
                                DONDGameState = enumDONDGameState.DONDShowRules
                            }
                            "feedback" -> { sendDONDFeedback() }
                        }
                    },
                    onBoxOpen = { },    // intentionally empty - clicking a box should do nothing
                    DONDBoxesContents = DONDBoxesContents.toMap()
                )

            }

            enumDONDGameState.DONDShowRules -> {
                DONDScreens.HowToPlay() {
                    DONDGameState = afterHowToPlayState
                }
            }

            enumDONDGameState.DONDGetUserEndgameDecision -> {}
            enumDONDGameState.DONDEndGame -> {
                finish()
            }

        }
    }

    fun sendDONDFeedback() {
        composeEmail(DONDFeedbackEmail, DONDFeedbackSubject, DONDFeedbackMailBody)
    }
    fun composeEmail(recipient: String, subject: String, body: String) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
        }
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            selector = selectorIntent
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        }
    }

    // @Composable
    fun LoadBoxes(DONDBoxesContents: MutableMap<Int, Int>) {
        val NUM_SHUFFLES = 1000

        for (n in 1..nBoxes) {
            DONDBoxesContents[n] = n
        }
        tmpCheatBox = nBoxes

        for (n in 1..NUM_SHUFFLES) {
            var p1: Int
            var p2: Int
            @Suppress("JoinDeclarationAndAssignment") var tmp: Int  // cannot be assigned here - p1, p2 must have values first
            do {
                p1 = Random.nextInt(1, nBoxes + 1)
                p2 = Random.nextInt(1, nBoxes + 1)
            } while (p1 == p2)
            tmp = DONDBoxesContents[p1]!!
            DONDBoxesContents[p1] = DONDBoxesContents[p2]!!
            DONDBoxesContents[p2] = tmp
            if (DONDBoxesContents[p1]!! == nBoxes) {
                tmpCheatBox = p1
            }
            if (DONDBoxesContents[p2]!! == nBoxes) {
                tmpCheatBox = p2
            }
        }
    }

    // @Composable
    private fun CalculateOffer(
        amountAvail: Map<Int, Boolean>
    ): Long {
        var TotalMoney = 0.0
        var Divider = 0.0
        var Pct: Double
        val generosityFactor = 1.05
        for (n in 1..nBoxes) {
            if (amountAvail[n]!!) {
                TotalMoney += Amount[n].toDouble()
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
            Pct = offerMinPct + Random.nextFloat() * generosityFactor * (offerMaxPct - offerMinPct)
        } while (offerMinPct > Pct || offerMaxPct < Pct)
        offerMoney = (TotalMoney * Pct / Divider).toLong()

        // don't get TOO generous
        while (offerMoney > Amount[nBoxes]) { offerMoney = (Random.nextDouble(0.889900, 0.999999)* Amount[nBoxes]).toLong() }

        return offerMoney
    }

}
