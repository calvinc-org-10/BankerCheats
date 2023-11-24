package com.calvinc.dond5

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.frazo.splashscreens.CountDownSplashScreen
import com.calvinc.dond5.ui.theme.BankerCheatsTheme
import kotlinx.coroutines.delay
import java.util.Locale

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
                /*
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
                */
            }
        }
    }

    companion object {
        lateinit var fnfinish: () -> Unit
    }
}

@Composable
fun PlayDOND() {
    // this var is simply a shortcut, but it's used is several places
    val nBoxes = DONDGlobals.nCases

    /*
    var DONDGameState = remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var DONDCasescaseVisible= rememberSaveable { mutableStateMapOf<Int,Boolean>()  }
    var DONDCasescaseContents= rememberSaveable { mutableStateMapOf<Int,Int>()  }
    var amountAvail = rememberSaveable { mutableStateMapOf<Int,Boolean>() }
    */
    // var waitingForUserInput:Boolean by remember { mutableStateOf(false) }
    var DONDGameState:enumDONDGameState by remember { mutableStateOf(enumDONDGameState.DONDInit) }
    var DONDCasescaseVisible= remember { mutableStateMapOf<Int,Boolean>()  }
    var DONDCasescaseContents= remember { mutableStateMapOf<Int,Int>()  }
    var amountAvail = remember { mutableStateMapOf<Int,Boolean>() }

    var hostWords: String = ""
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
                }
                //TODO: Look at Plural resources or MessageFormat class
                hostWords += stringResource(
                    if (DONDGlobals.toOpen == 1) R.string.hostWord_TimeToOpen1Box else R.string.hostWord_TimeToOpenBoxes,
                    DONDGlobals.toOpen
                )
            } else {
                // Congratulate(true)
            }
        }

        enumDONDGameState.DONDShowBoxes -> {}
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

/*RESTARTHERE

        enumDONDGameState.DONDCongratulate -> {
        /*
        boxesFragment!!.MakeHostSay(String.format(AppResources.getString(R.string.hostWord_OfferTaken), DONDGlobals.offerMoney))
        Congratulate()
        */
        }
        enumDONDGameState.DONDGetUserEndgameDecision -> {}
        enumDONDGameState.DONDEndGame -> {}
*/
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
                terminatorfunction = MainActivity.fnfinish
            )
        }
        enumDONDGameState.DONDStartNewRound -> {
            DONDComposables.MainScreen(
                DONDCasescaseVisible = DONDCasescaseVisible.toMap(),
                hostWords = hostWords,
                terminatorfunction = MainActivity.fnfinish,
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
        enumDONDGameState.DONDShowBoxes -> {}
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
                        DONDGlobals.offerMinPct += DONDGlobals.offerMinPctDelta
                        DONDGlobals.offerMaxPct += DONDGlobals.offerMaxPctDelta
                        if (DONDGlobals.toOpen > 1) {
                            DONDGlobals.toOpen--
                        }
                        DONDGameState = enumDONDGameState.DONDStartNewRound
                    }
                }
            )
        }
        enumDONDGameState.DONDChooseNextBox -> {
            DONDComposables.MainScreen(
                DONDCasescaseVisible = DONDCasescaseVisible.toMap(),
                hostWords = hostWords,
                terminatorfunction = MainActivity.fnfinish,
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
        enumDONDGameState.DONDCongratulate -> {}
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

/*******************************************************
 *  THE ACTUAL SCREENS
 *******************************************************/

/*
@Composable
fun DONDSplashScreen() {
    var showSplash by remember { mutableStateOf(true) }
    val SPLASH_DELAY: Long = 5000

    LaunchedEffect(key1 = Unit) {
        delay(SPLASH_DELAY)
        showSplash = false
        DONDGlobals.SplashDone = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    )
    {
        if (showSplash) {
            Text(
                text = DONDGlobals.TID,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(id = R.drawable.banker1),
                contentDescription = "The Banker",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.DONDGameTitle),
                fontFamily = FontFamily(Font(R.font.lemonregular)),
                fontSize = 48.sp,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(id = R.string.DONDGameSubTitle),
                fontFamily = FontFamily(Font(R.font.edutasbeginnervariablefontweight)),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = { DONDGlobals.CalvinCheat = true }
            ) { Text(stringResource(id = R.string.DONDGameAuthor)) }
        }
    }
}

@Composable
fun MainScreen(
    DONDCasescaseVisible:Map<Int,Boolean>,
    hostWords:String, congrats:String = "",
    onBoxOpen: (Int) -> Unit,
    terminatorfunction: () -> Unit,
) {
    val cpr = 5 // columns per row
    val boxWid = (LocalConfiguration.current.screenWidthDp / cpr) - 2

    // Surface (modifier = Modifier.zIndex(1f)) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "This is the main game screen",
            fontSize = 30.sp,
        )
        Text(
            text = hostWords,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        for (row in 1 until DONDGlobals.nCases step cpr) {
            Row {
                for (col in 0..4) {
                    Button(
                        onClick = {
                            onBoxOpen(row + col)
                        },
                        modifier = Modifier.size(boxWid.dp),
                        enabled = DONDCasescaseVisible[row + col]!!,
                    )
                    {
                        Text((row + col).toString())
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        Spacer(modifier = Modifier.height((12.dp)))
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (DONDGlobals.intMyCase != 0) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Your Case is",
                    )
                    Text(
                        modifier = Modifier.size(25.dp),
                        text = DONDGlobals.intMyCase.toString(),
                        fontSize = 20.sp
                    )
                }
            }
            if (congrats != "") {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = congrats,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "ShowAmounts button here, r just",
                textAlign = TextAlign.End,
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = { terminatorfunction() },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(text = "Go Away!")
        }
    }
}

@Composable
fun MoneyListScreen(
    amountAvail:Map<Int,Boolean>,
    CaseNum:Int,
    onOKClick: () -> Unit,
) { // show available amounts and grey out CaseContents[CaseChosen]

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.titleAmountsLeftInPlay),
        )
        Spacer(modifier = Modifier.height(12.dp))
        for (row in 1..DONDGlobals.nCases / 2) {
            val p1 = row
            val p2 = row + DONDGlobals.nCases / 2
            // first box on this row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {},
                    elevation = ButtonDefaults.buttonElevation(),
                    // enabled = false,
                    // colors = ButtonColors()
                ) {
                    Text(
                        text = DONDGlobals.Amount[p1].toString(),
                        color = if (amountAvail[p1]!!) Color.Green else Color.Gray
                    )
                }
                // second box on this row
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {},
                    elevation = ButtonDefaults.buttonElevation(),
                    // enabled = false,
                    // colors = ButtonColors()
                ) {
                    Text(
                        text = DONDGlobals.Amount[p2].toString(),
                        color = if (amountAvail[p2]!!) Color.Green else Color.Gray
                    )
                }
            }
        }
        if (DONDGlobals.nCases % 2 == 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val p = DONDGlobals.nCases
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    elevation = ButtonDefaults.buttonElevation(),
                    // enabled = false,
                    // colors = ButtonColors()
                ) {
                    Text(
                        text = DONDGlobals.Amount[p].toString(),
                        color = if (amountAvail[p]!!) Color.Green else Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height((12.dp)))
        Button(onClick = onOKClick) {
            Text(text = stringResource(id = android.R.string.ok))
        }
    }
}

@Composable
fun OfferScreen() {

}

/* 
@Preview(showBackground = true)
@Composable
fun DONDSplashScreenPreview() {
    BankerCheatsTheme {
        DONDSplashScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val dummyMap = mutableMapOf<Int,Boolean>()
    for (i in 1..DONDGlobals.nCases) { dummyMap[i] = true }
    BankerCheatsTheme {
        MainScreen(
            DONDCasescaseVisible = dummyMap,
            hostWords = "Host Words Go Here",
            onBoxOpen = { },
            terminatorfunction = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MoneyListScreenPreview() {
    val dummyVisibleMap = mutableMapOf<Int,Boolean>()
    val dummyContentsMap = mutableMapOf<Int,Int>()
    val dummyAvailMap = mutableMapOf<Int,Boolean>()
    for (i in 1..DONDGlobals.nCases) {
        dummyVisibleMap[i] = true
        dummyContentsMap[i] = i
        dummyAvailMap[i] = true
    }
    BankerCheatsTheme {
        MoneyListScreen(
            amountAvail = dummyAvailMap,
            CaseNum = 0,
            onOKClick = { },
        )
    }
}
@Preview(showBackground = true)
@Composable
fun OfferScreenPreview() {
    BankerCheatsTheme {
        OfferScreen()
    }
}
*/