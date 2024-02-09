package com.calvinc.dond5

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.collection.arrayMapOf
import androidx.collection.arraySetOf
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.content.pm.PackageInfoCompat
import br.com.frazo.splashscreens.SplashScreen
import com.calvinc.dond5.DONDGlobals.Amount
import com.calvinc.dond5.DONDGlobals.CalvinCheat
import com.calvinc.dond5.DONDGlobals.DONDTTSInstance
import com.calvinc.dond5.DONDGlobals.absOfferMaxPct
import com.calvinc.dond5.DONDGlobals.absOfferMinPct
import com.calvinc.dond5.DONDGlobals.nBoxes
import com.calvinc.dond5.DONDGlobals.offerMaxPctDelta
import com.calvinc.dond5.DONDGlobals.offerMinPctDelta
import com.calvinc.dond5.DONDGlobals.tmpCheatBox
import com.calvinc.dond5.DONDGlobals.toOpenStart
import com.calvinc.dond5.ui.theme.BankerCheatsTheme
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val DONDFeedbackEmail = "calvinc.developer.801@gmail.com"
    private val DONDFeedbackSubject = "Feedback - BankerCheats"
    private val DONDFeedbackMailBody = "\"The Banker Will Cheat You Now\" is Awesome!!"

    private var DONDappver = object {
        var verCode: Long = 0L
        var verName: String = ""
    }

    private var hostWords: hostDialogue = hostDialogue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: wrap in try/catch
        with (DONDappver) {
            val pkgInfo = packageManager.getPackageInfo(packageName, 0)
            verCode = PackageInfoCompat.getLongVersionCode(pkgInfo)
            verName = pkgInfo.versionName
        }

        CheckforNewVer()

        startTTS()

        setContent {
            BankerCheatsTheme {
                Surface {
                    var finished by rememberSaveable { mutableStateOf(false) }
                    SplashScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        finished = finished,
                        beforeFinished = {
                            if (!finished) {
                                val DONDAudioTheme =
                                    MediaPlayer.create(
                                        LocalView.current.context,
                                        R.raw.bankercheatintrov1
                                    )
                                DONDAudioTheme.setOnCompletionListener {
                                    finished = true
                                    it.release()
                                }
                                DONDScreens.DONDSplashScreen {
                                    finished = true
                                    DONDAudioTheme.release()
                                }
                                DONDAudioTheme.start()
                            }
                        },
                        whenFinished = {
                            PlayDOND()
                        }
                    )
                }
            }
        }
    }

    private fun CheckforNewVer() {
        // TODO("Not yet implemented")
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
            if (it == TextToSpeech.SUCCESS) {
                DONDTTSInstance.setLanguage(Locale.US)
            }
        }
    }

    private fun stopTTS() {
        if (DONDGlobals.TTSOK()) {
            DONDTTSInstance.stop()
            DONDTTSInstance.shutdown()
        }
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
        /***  state variables ***/
        /* TODO: define these vars inside class, then remember the class:
        class DONDstate {
            var currentPage by mutableStateOf(0)
            var pageCount by mutableStateOf(0)
            var currentPageOffset by mutableStateOf(0F)
        }

        @Composable fun rememState() {
            val state = remember { DONDState() }
        }
        */
        // TODO: debug mode - examine state changes 
        // val saverDONDGameState = Saver<classDONDGameState,String> (
        //     save = {
        //         it.getValue().name },
        //     restore = { classDONDGameState(enumDONDGameState.valueOf(it)) }
        //     )

        // here's hoping DONDGameState works better as an Int
        val DONDGameStateCodelist = arrayOf(
            enumDONDGameState.DONDInit,
            enumDONDGameState.DONDActivateGame,
            enumDONDGameState.DONDPickMyBox,
            enumDONDGameState.DONDStartNewRound,
            enumDONDGameState.DONDChooseNextBox,
            enumDONDGameState.DONDOfferRefused,
            enumDONDGameState.DONDPrepareForOffer,
            enumDONDGameState.DONDCalculateOffer,
            enumDONDGameState.DONDMakeOffer,
            enumDONDGameState.DONDShowAmountsLeft,
            enumDONDGameState.DONDCongratulate,
            enumDONDGameState.DONDShowRules,
            enumDONDGameState.DONDGetUserEndgameDecision,
            enumDONDGameState.DONDEndGame,
        )

        var DONDGameStatecode by rememberSaveable { mutableIntStateOf(DONDGameStateCodelist.indexOf(enumDONDGameState.DONDInit)) }
        val DONDBoxesVisiblity by rememberSaveable { mutableStateOf(BooleanArray(nBoxes+1)) }
        val DONDBoxesContents by rememberSaveable { mutableStateOf(IntArray(nBoxes+1)) }
        // val amountAvail by rememberSaveable { mutableStateOf(BooleanArray(nBoxes+1)) }
        var AmountListPeekedOnly by rememberSaveable { mutableStateOf(false) }
        var offerAccepted by rememberSaveable { mutableStateOf(false) }
        var intMyBox by rememberSaveable { mutableIntStateOf(0) }
        var roundNum by rememberSaveable { mutableIntStateOf(0) }
        var toOpen by rememberSaveable { mutableIntStateOf(0) }
        var lastBoxOpened by rememberSaveable { mutableIntStateOf(0) }
        var boxesOpened by rememberSaveable { mutableIntStateOf(0) }
        var offerMoney by rememberSaveable { mutableLongStateOf(0) }
        val offerList by remember { mutableStateOf(mutableListOf(0L)) }
        /*
        Creating a MutableState object with a mutable collection type More... (Ctrl+F1)
        Inspection info:Writes to mutable collections inside a MutableState will not cause a recomposition - only writes to the MutableState itself will. In most cases you should either use a read-only collection (such as List or Map) and assign a new instance to the MutableState when your data changes, or you can use an snapshot-backed collection such as SnapshotStateList or SnapshotStateMap which will correctly cause a recomposition when their contents are modified.
        */

        /*** state change functions to pass to Composables ***/
        var onBoxOpen: (n:Int) -> Unit = { }
        var miscfunctions: (f:String) -> Unit = { }
        var onOKClick: () -> Unit = { }
        var onOfferResponse: (Boolean) -> Unit = { }
        var goback: () -> Unit = { }

        fun changeDONDState(newstate: enumDONDGameState) {
            DONDGameStatecode = DONDGameStateCodelist.indexOf(newstate)
        }
        fun getcurrDONDState(): enumDONDGameState {
            return DONDGameStateCodelist[DONDGameStatecode]
        }

        /*** non-state variables ***/
        // var hostWords = ""
        var wordsCongrat = ""
        // var afterAmountState by rememberSaveable(saver = saverDONDGameState) { classDONDGameState(enumDONDGameState.DONDChooseNextBox) }
        // var afterHowToPlayState by rememberSaveable(saver = saverDONDGameState) { classDONDGameState(enumDONDGameState.DONDChooseNextBox) }
        var afterAmountState = enumDONDGameState.DONDChooseNextBox
        var afterHowToPlayState = enumDONDGameState.DONDChooseNextBox

        println("PlayGame Main: ${getcurrDONDState()}")

        /*** supporting functions ***/
        fun amountAvail(n:Int): Boolean {
            if (n < 1 || n > nBoxes)
                return false
            if (n == DONDBoxesContents[intMyBox])
                return true

            var X = 1
            while (X <= nBoxes && n != DONDBoxesContents[X])
                X++

            return if (X > nBoxes)
                false
            else
                DONDBoxesVisiblity[X]
        }

        @Suppress("unused")
        fun nClosedCases(): Int {
            var n = 0

            for (i in 1..nBoxes) {
                if (amountAvail(i)) n++
            }

            return n
        }

        @Suppress("unused")
        fun totalNumRounds(): Int {
            var tnr = 0                 // total number of rounds counter
            var nBox = nBoxes       // number of boxes left in simulated play
            var bto = toOpenStart   // number of boxes to open in simulated round of play

            nBox--      // 1 box is My Box

            while (nBox > 1) {
                tnr++           // one more round
                nBox -= bto     // open bto boxes
                if (bto > 1) bto--  // open one less box next round
            }

            return tnr
        }

        fun offerMinPct() = absOfferMinPct + roundNum*offerMinPctDelta
        fun offerMaxPct() = absOfferMaxPct + roundNum*offerMaxPctDelta

        fun LoadBoxes() {
            val NUM_SHUFFLES = 1000

            for (n in 1..nBoxes) {
                DONDBoxesContents[n] = n
                DONDBoxesVisiblity[n] = true
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
                tmp = DONDBoxesContents[p1]
                DONDBoxesContents[p1] = DONDBoxesContents[p2]
                DONDBoxesContents[p2] = tmp
                if (DONDBoxesContents[p1] == nBoxes) {
                    tmpCheatBox = p1
                }
                if (DONDBoxesContents[p2] == nBoxes) {
                    tmpCheatBox = p2
                }
            }
        }

        fun CalculateOffer(): Long {
            var TotalMoney = 0.0
            var Divider = 0.0
            var Pct: Double
            val generosityFactor = 1.05
            @Suppress("NAME_SHADOWING") var offerMoney: Long

            for (n in 1..nBoxes) {
                if (amountAvail(n)) {
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
                Pct = offerMinPct() + Random.nextFloat() * generosityFactor * (offerMaxPct() - offerMinPct())
            } while (offerMinPct() > Pct || offerMaxPct() < Pct)
            offerMoney = (TotalMoney * Pct / Divider).toLong()

            // don't get TOO generous
            while (offerMoney > Amount[nBoxes]) { offerMoney = (Random.nextDouble(0.889900, 0.999999)* Amount[nBoxes]).toLong() }

            return offerMoney
        }

        /*********************************
         *  Play DOND - the main machine
        *********************************/
        // set up variables
        //try not to set/reset DONDGameState here; do that in presentDONDScreen
        when (getcurrDONDState()) {
            enumDONDGameState.DONDInit,
            enumDONDGameState.DONDActivateGame -> {
                offerMoney = 0
                offerList.clear()
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

                LoadBoxes()

                roundNum = 1
                toOpen = toOpenStart
                boxesOpened = 0
                val hostWords_Cheat =
                    if (CalvinCheat) "Cheat: $tmpCheatBox.\n" else ""
                with(hostWords) {
                    screen = hostWords_Cheat + stringResource(id = R.string.hostWord_ChooseABox1)
                    spoken = screen
                }

                onBoxOpen = { n ->
                    lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    if (intMyBox == 0) {  // beginning of game; MyBox has not yet been chosen
                        intMyBox = n
                    }
                    changeDONDState(newstate = enumDONDGameState.DONDChooseNextBox)
                }

                miscfunctions = {
                    when (it) {
                        "stop" -> changeDONDState(enumDONDGameState.DONDEndGame)
                        "amounts" -> {
                            afterAmountState = enumDONDGameState.DONDPickMyBox
                            AmountListPeekedOnly = true
                            changeDONDState(enumDONDGameState.DONDShowAmountsLeft)
                        }

                        "rules" -> {
                            afterHowToPlayState = enumDONDGameState.DONDPickMyBox
                            changeDONDState(enumDONDGameState.DONDShowRules)
                        }
                        "feedback" -> { sendDONDFeedback() }
                    }
                }

            }

            enumDONDGameState.DONDPickMyBox -> {
                val hostWords_Cheat =
                    if (CalvinCheat) "Cheat: $tmpCheatBox.\n" else ""
                with(hostWords) {
                    screen = hostWords_Cheat + stringResource(id = R.string.hostWord_ChooseABox1)
                    spoken = screen
                }

                onBoxOpen = { n ->
                    lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    if (intMyBox == 0) {  // beginning of game; MyBox has not yet been chosen
                        intMyBox = n
                    }
                    changeDONDState(enumDONDGameState.DONDStartNewRound)
                }

                miscfunctions = {
                    when (it) {
                        "stop" -> changeDONDState(enumDONDGameState.DONDEndGame)
                        "amounts" -> {
                            afterAmountState = getcurrDONDState()
                            AmountListPeekedOnly = true
                            changeDONDState(enumDONDGameState.DONDShowAmountsLeft)
                        }

                        "rules" -> {
                            afterHowToPlayState = getcurrDONDState()
                            changeDONDState(enumDONDGameState.DONDShowRules)
                        }
                        "feedback" -> { sendDONDFeedback() }
                    }
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
                            ) + "\n"
                        )
                    }

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
                    changeDONDState(enumDONDGameState.DONDCongratulate)
                }

                onOKClick = {
                    changeDONDState(enumDONDGameState.DONDOfferRefused)
                }
            }

            enumDONDGameState.DONDChooseNextBox -> {
                val nBox = lastBoxOpened
                with(hostWords) {
                    val moneyInBox =
                        if (nBox != 0 && nBox != intMyBox) Amount[DONDBoxesContents[nBox]] else 0
                    screen = if (nBox != intMyBox)
                        String.format(
                            stringResource(R.string.hostWord_BoxContains),
                            nBox,
                            moneyInBox
                        ) + "\n"
                    else
                        // lastOpenedBox was MyBox; we prolly opened it and checked Amounts in play.  Don't reveal contents of MyBox
                        ""

                    spoken = ""
                }

                if (boxesOpened >= toOpen) {
                    hostWords.addToall(stringResource(id = R.string.hostWord_TimeForOffer))
                    changeDONDState(enumDONDGameState.DONDPrepareForOffer)
                } else {
                    hostWords.addToall(
                        stringResource(
                            id = if (toOpen - boxesOpened == 1) R.string.hostWord_OpenOneMoreBox else R.string.hostWord_OpenAnotherBoxofN,
                            toOpen - boxesOpened
                        )
                    )
                }

                miscfunctions = {
                    when (it) {
                        "stop" -> changeDONDState(enumDONDGameState.DONDEndGame)
                        "amounts" -> {
                            afterAmountState = getcurrDONDState()
                            AmountListPeekedOnly = true
                            changeDONDState(enumDONDGameState.DONDShowAmountsLeft)
                        }

                        "rules" -> {
                            afterHowToPlayState = getcurrDONDState()
                            changeDONDState(enumDONDGameState.DONDShowRules)
                        }

                        "feedback" -> { sendDONDFeedback() }
                    }
                }

                onBoxOpen = { n ->
                    lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    boxesOpened++
                    afterAmountState = enumDONDGameState.DONDChooseNextBox
                    AmountListPeekedOnly = false
                    changeDONDState(enumDONDGameState.DONDShowAmountsLeft)
                }

            }

            enumDONDGameState.DONDOfferRefused -> {
                miscfunctions = {
                    when (it) {
                        "stop" -> changeDONDState(enumDONDGameState.DONDEndGame)
                        "amounts" -> {
                            afterAmountState = getcurrDONDState()
                            AmountListPeekedOnly = true
                            changeDONDState(enumDONDGameState.DONDShowAmountsLeft)
                        }

                        "rules" -> {
                            afterHowToPlayState = getcurrDONDState()
                            changeDONDState(enumDONDGameState.DONDShowRules)
                        }

                        "feedback" -> { sendDONDFeedback() }
                    }
                }

                onBoxOpen = { n ->
                    lastBoxOpened = n
                    DONDBoxesVisiblity[n] = false
                    boxesOpened++
                    afterAmountState = enumDONDGameState.DONDChooseNextBox
                    AmountListPeekedOnly = false
                    changeDONDState(enumDONDGameState.DONDShowAmountsLeft)
                }

            }

            enumDONDGameState.DONDShowAmountsLeft -> {
                val nBox = lastBoxOpened
                with(hostWords) {
                    val moneyInBox =
                        if (nBox != 0 && nBox != intMyBox) Amount[DONDBoxesContents[nBox]] else 0
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

                onOKClick = {
                    // println("OK clicked, pre-assignment state is ${getcurrDONDState()}, after = $afterAmountState")
                    changeDONDState(afterAmountState)
                    // println("OK clicked, post-assignment state is ${getcurrDONDState()}, after = $afterAmountState")
                }

            }

            enumDONDGameState.DONDCalculateOffer -> {
                offerMoney = CalculateOffer()
                offerList.add(offerMoney)

                onOKClick = {
                    changeDONDState(enumDONDGameState.DONDMakeOffer)
                }
            }

            enumDONDGameState.DONDMakeOffer -> {
                //TODO: Do string resources
                with(hostWords) {
                    screen =
                        stringResource(R.string.presentOffer, offerMoney)
                    screen += System.lineSeparator()
                    val n = if (toOpen < 2) 1 else toOpen - 1
                    screen += if (nClosedCases() <= 2) stringResource(id = R.string.hostWord_ThisIsLastOffer, intMyBox)
                        else stringResource(id = R.string.hostWord_IfNoAcceptYouMustOpenBoxes_pt1, n) + (if (n == 1) "box" else "boxes") + "."
                    screen += System.lineSeparator() + stringResource(id = R.string.hostWord_DoYouAcceptOffer)

                    spoken = screen
                }

                onOfferResponse = {
                    offerAccepted = it
                    changeDONDState(if (offerAccepted) enumDONDGameState.DONDCongratulate else enumDONDGameState.DONDStartNewRound)
                }
            }

            enumDONDGameState.DONDCongratulate -> {
                val heldOnToEnd = (nClosedCases() <= 2)
                val BoxMoney = Amount[DONDBoxesContents[intMyBox]]
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

                miscfunctions = {
                    when (it) {
                        "stop" -> changeDONDState(enumDONDGameState.DONDEndGame)
                        "again" -> changeDONDState(enumDONDGameState.DONDActivateGame)
                        "rules" -> {
                            afterHowToPlayState = getcurrDONDState()
                            changeDONDState(enumDONDGameState.DONDShowRules)
                        }
                        "feedback" -> { sendDONDFeedback() }
                    }
                }

                onBoxOpen = { }    // intentionally empty - clicking a box should do nothing

            }

            enumDONDGameState.DONDPrepareForOffer -> {
                onOKClick = { changeDONDState(enumDONDGameState.DONDCalculateOffer) }
            }

            enumDONDGameState.DONDShowRules -> {
                goback = {
                    changeDONDState(afterHowToPlayState)
                }
            }

            enumDONDGameState.DONDGetUserEndgameDecision -> {}
            enumDONDGameState.DONDEndGame -> { }

        }

        PresentDONDScreen(
            DONDGameState = getcurrDONDState(),
            DONDBoxesVisiblity = DONDBoxesVisiblity,
            DONDBoxesContents = DONDBoxesContents,
            amountAvail = ::amountAvail,
            roundNum, boxesOpened,  // used to supply unique signature for recomposition
            intMyBox = intMyBox,
            lastBoxOpened = lastBoxOpened,
            wordsCongrat = wordsCongrat,
            onBoxOpen = onBoxOpen,
            miscfunctions = miscfunctions,
            onOKClick = onOKClick,
            onOfferResponse =onOfferResponse,
            goback = goback,
        )
    }

    @Composable
    fun PresentDONDScreen (
        DONDGameState: enumDONDGameState,    //@Suppress("LocalVariableName")
        DONDBoxesVisiblity: BooleanArray,
        DONDBoxesContents: IntArray = IntArray(nBoxes + 1),
        amountAvail: (Int) -> Boolean,
        roundNum: Int, numBoxOpening: Int,  // used to supply unique signature for recomposition
        intMyBox: Int,
        lastBoxOpened: Int,
        wordsCongrat: String = "",
        onBoxOpen: (n: Int) -> Unit,
        miscfunctions: (it:String) -> Unit,
        onOKClick: () -> Unit,
        onOfferResponse: (Boolean) -> Unit,
        goback: () -> Unit = { }
    ) {
        //set/reset DONDGameState here; try to make it last action before finishing each case

        println("presentDONDScreen: $DONDGameState")

        when (DONDGameState) {
            enumDONDGameState.DONDInit,
            enumDONDGameState.DONDActivateGame -> {
                DONDScreens.MainScreen(
                    DONDGameState,
                    DONDBoxesVisiblity = DONDBoxesVisiblity,
                    roundNum, numBoxOpening,
                    intMyBox = intMyBox,
                    hostWords = hostWords,
                    onBoxOpen = onBoxOpen,
                    miscfunctions = miscfunctions,
                )
            }

            enumDONDGameState.DONDPickMyBox -> {
                DONDScreens.MainScreen(
                    DONDGameState,
                    DONDBoxesVisiblity = DONDBoxesVisiblity,
                    roundNum, numBoxOpening,
                    intMyBox = intMyBox,
                    hostWords = hostWords,
                    onBoxOpen = onBoxOpen,
                    miscfunctions = miscfunctions
                )
            }

            enumDONDGameState.DONDStartNewRound -> {
                onOKClick()     // this will change state to DONDOfferRefused
            }

            enumDONDGameState.DONDOfferRefused,
            enumDONDGameState.DONDChooseNextBox -> {
                DONDScreens.MainScreen(
                    DONDGameState,
                    DONDBoxesVisiblity = DONDBoxesVisiblity,
                    roundNum, numBoxOpening,
                    intMyBox = intMyBox,
                    hostWords = hostWords,
                    miscfunctions = miscfunctions,
                    onBoxOpen = onBoxOpen,
                )

            }

            enumDONDGameState.DONDShowAmountsLeft -> {
                @Suppress("UnnecessaryVariable") val nBox = lastBoxOpened
                val arrayAvail = BooleanArray(nBoxes+1) { amountAvail(it) }

                DONDScreens.MoneyListScreen(
                    DONDGameState,
                    // DONDBoxesContents,  //DONE: Remove in final build - for temp debugging only
                    hostWords = hostWords,
                    AmountOpened = if (nBox != intMyBox) DONDBoxesContents[nBox] else 0,
                    amountAvail = arrayAvail,
                    onOKClick = onOKClick,
                )

            }

            enumDONDGameState.DONDPrepareForOffer -> {
                DONDScreens.TimeForOffer( DONDGameState, hostWords, onOKClick )
            }

            enumDONDGameState.DONDCalculateOffer -> {
                onOKClick()
            }

            enumDONDGameState.DONDMakeOffer -> {
                DONDScreens.OfferScreen( DONDGameState, hostWords, onOfferResponse )
            }

            enumDONDGameState.DONDCongratulate -> {
                DONDScreens.MainScreen(
                    DONDGameState,
                    DONDBoxesVisiblity = DONDBoxesVisiblity,
                    DONDBoxesContents = DONDBoxesContents,
                    roundNum = roundNum, numBoxOpening = numBoxOpening,
                    intMyBox = intMyBox,
                    hostWords = hostWords,
                    congrats = wordsCongrat,
                    miscfunctions = miscfunctions,
                    onBoxOpen = onBoxOpen,
                )

            }

            enumDONDGameState.DONDShowRules -> {
                DONDScreens.HowToPlay(
                    DONDGameState, DONDappver.verCode, DONDappver.verName, goback,
                )
            }

            enumDONDGameState.DONDGetUserEndgameDecision -> {}

            enumDONDGameState.DONDEndGame -> {
                finish()
            }

        }
    }

    private fun sendDONDFeedback() {
        composeEmail(
            DONDFeedbackEmail,
            DONDFeedbackSubject + " ver ${DONDappver.verName}, code ${DONDappver.verCode}",
            DONDFeedbackMailBody + "\nver ${DONDappver.verName}, code ${DONDappver.verCode}\n"
        )
    }
    @Suppress("SameParameterValue")
    private fun composeEmail(recipient: String, subject: String, body: String) {
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

    companion object {
        // lateinit var fnfinish: () -> Unit
        // lateinit var mainContext: Context
    }

}

