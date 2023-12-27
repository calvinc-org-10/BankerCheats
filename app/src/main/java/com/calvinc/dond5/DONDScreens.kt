package com.calvinc.dond5

import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.calvinc.dond5.DONDGlobals.Amount
import com.calvinc.dond5.DONDGlobals.CalvinCheat
import com.calvinc.dond5.DONDGlobals.DONDUtter
import com.calvinc.dond5.DONDGlobals.intMyBox
import com.calvinc.dond5.DONDGlobals.nBoxes
import com.calvinc.dond5.ui.theme.BankerCheatsTheme
import kotlinx.coroutines.delay

object DONDScreens {
    private const val hostWordFontSize = 20
    private const val AmountFontSize = 20
    private val AmountAvailColor = Color.Green
    private val AmountNotAvailColor = Color.Gray
    private const val buttonFontSize = 18

    /************************************
     * ANIMATION ("glow") CLASSES, etc
     ***********************************

    // Holds the animation values.
    enum class AnimationState { Starting, Finished }

    class TransitionData(
        color: State<Color>,
        fontsize: State<Int>
    ) {
        val color by color
        val fontsize by fontsize
    }

    // Create a Transition and return its animation values.
    @Composable
    fun updateTransitionData(animationState: MutableTransitionState<AnimationState>): TransitionData {
        val transition = updateTransition(animationState, label = "glow state")
        val color = transition.animateColor(label = "color") { state ->
            when (state) {
                AnimationState.Starting -> AmountAvailColor
                AnimationState.Finished -> AmountNotAvailColor
            }
        }
        val size = transition.animateInt(label = "size") { state ->
            when (state) {
                AnimationState.Starting -> (AmountFontSize/3)
                AnimationState.Finished -> AmountFontSize
            }
        }

        return remember(transition) { TransitionData(color, size) }
    }
/*    class TransitionData(transition: Transition<AnimationState>) {
        val color by color
        val fontsize by fontsize

        @Composable
        private fun updateTransitionData(animationState: AnimationState): TransitionData {
            val transition = updateTransition(animationState, label = "glow state")
            return remember(transition) { TransitionData(color, size) }
        }
    }
*/
    */

    @Composable
    fun DONDSplashScreen(emergencyExit: ()->Unit = {}) {
        // var showSplash by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        )
        {
            // if (showSplash) {
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
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column (horizontalAlignment = Alignment.Start) {
                        Button(
                            onClick = { CalvinCheat = true }
                        ) { Text(stringResource(id = R.string.DONDGameAuthor)) }
                    }
                    /*
                    Column (horizontalAlignment = AbsoluteAlignment.Right) {
                        Button(onClick = emergencyExit) {Text("EE") }
                    }
                    */
                }
            // }
        }
    }

    @Composable
    fun MainScreen(
        @Suppress("LocalVariableName") DONDBoxesVisiblity:Map<Int,Boolean>,
        hostWords:hostDialogue, congrats:String = "", beSilent: Boolean = false,
        onBoxOpen: (n:Int) -> Unit,
        miscfunctions: (f:String) -> Unit,
        @Suppress("LocalVariableName") DONDBoxesContents:Map<Int,Int> = mapOf()
    ) {
        val endGameReveal = (congrats != "")
        val cpr = 5 // columns per row
        val scrWid = LocalConfiguration.current.screenWidthDp
        val scrHgt = LocalConfiguration.current.screenHeightDp
        val isLandscape = (scrWid > scrHgt)
        val boxWid = (arrayOf(scrWid,scrHgt).min() / cpr) - (if (isLandscape) 50 else 2)

        // Surface (modifier = Modifier.zIndex(1f)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            // Column to hold "top/main" part of screen
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = hostWords.screen,
                    textAlign = TextAlign.Center,
                    fontSize = hostWordFontSize.sp
                )
                if (!beSilent) {
                    DONDUtter(hostWords.spoken, TextToSpeech.QUEUE_FLUSH)
                }
                Spacer(modifier = Modifier.height(12.dp))
                for (row in 1 until nBoxes step cpr) {
                    Row {
                        for (col in 0..(cpr-1)) {
                            Button(
                                onClick = { onBoxOpen(row + col) },
                                modifier = Modifier.size(boxWid.dp),
                                contentPadding = PaddingValues(
                                    start = 4.dp,
                                    top = 4.dp,
                                    end = 4.dp,
                                    bottom = 4.dp
                                ),
                                enabled = DONDBoxesVisiblity[row + col]!!,
                            )
                            {
                                Column {
                                    Text(
                                        (row + col).toString(),
                                        fontSize = (boxWid / 4).sp
                                    )
                                    if (endGameReveal && DONDBoxesVisiblity[row + col]!!) {
                                        Text(
                                            String.format(
                                                "%1$,d",
                                                Amount[DONDBoxesContents[row + col]!!]
                                            ),
                                            fontSize = (boxWid / 6).sp
                                        )
                                    }
                                }
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
                    if (intMyBox != 0) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = stringResource(id = R.string.DONDYourBoxIs),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Text(
                                modifier = Modifier.padding(start = 24.dp),
                                text = intMyBox.toString(),
                                fontSize = 24.sp
                            )
                            if (endGameReveal) {
                                Text(
                                    modifier = Modifier.padding(start = 24.dp),
                                    text = String.format("%1$,d",Amount[DONDBoxesContents[intMyBox]!!]),
                                    fontSize = (boxWid / 6).sp
                                )
                            }
                        }
                    }
                    if (congrats != "") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            DONDUtter(congrats)
                            Text(
                                text = congrats,
                                fontSize = hostWordFontSize.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (!endGameReveal) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { miscfunctions("amounts") },
                        ) {
                            Text(text = "Show Amounts")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (endGameReveal) {
                    Button(
                        onClick = { miscfunctions("again") },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(text = "Play Again")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Button(
                    onClick = { miscfunctions("stop") },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(text = "Go Away!")
                }
            }
            // row at "bottom" of screen - forced there because Column above has weight(1f)
            Row() {
                Button(
                    onClick = { miscfunctions("feedback") },
                    // modifier = Modifier.align((Alignment.CenterHorizontally)),
                ) {
                    Text("feedback")
                }
                Button(
                    onClick = { miscfunctions("rules") },
                    // modifier = Modifier.align((Alignment.CenterHorizontally)),
                ) {
                    Text("How to Play")
                }
            }
        }
    }

    // @OptIn(ExperimentalTransitionApi::class)
    @Composable
    fun MoneyListScreen(
        // DONDBoxesContents: Map<Int, Int>,   //DONE: Remove from final build - debugging only
        hostWords:hostDialogue, beSilent: Boolean = false,
        AmountOpened:Int = 0,
        showOnly:Boolean = false,
        amountAvail:Map<Int,Boolean>,
        onOKClick: () -> Unit,
    ) {
        val passRange = if (showOnly) 0..0 else 1..2
        val delayDurationMillis:Long = 1600
        var pass by remember { mutableStateOf(passRange.first) }

        if (pass <= passRange.last) {
            MoneyListScreen_actual(
                // DONDBoxesContents,    //DONE: Remove from final build - debugging only
                hostWords = hostWords, beSilent = beSilent || (pass != passRange.first),
                amountAvail = amountAvail,
                AmountOpened = AmountOpened,
                onOKClick = onOKClick,
                passBoxOpen = pass,
            )
            // pause for one second between drawings of MoneyList
            LaunchedEffect(true) {
                delay(delayDurationMillis)
                pass++
            }
        }
    }
    @Composable  fun MoneyListScreen_actual(
        // DONDBoxesContents: Map<Int, Int>,   //DONE: Remove from final build - debugging only
        hostWords:hostDialogue, beSilent: Boolean = false,
        AmountOpened:Int = 0,
        passBoxOpen: Int = 0,
        amountAvail:Map<Int,Boolean>,
        onOKClick: () -> Unit,
    ) {
        // I know working with actual sizes is "wrong", but I need consistent Box sizes.
        // TODO: Find the "right" way to do this
        val scrWid = LocalConfiguration.current.screenWidthDp
        val scrHgt = LocalConfiguration.current.screenHeightDp
        val boxWid = (scrWid * .45).toInt()
        val boxWid_last = scrWid/2
        val boxHgt = (scrHgt - (hostWordFontSize*2-2+12) + (12+20) - 200)/((nBoxes +1)/2)  // if you ask nicely, I'll lovingly explain this formula to you
        var alreadySpoken by remember { mutableStateOf(false) }

        //DONE: Remove this block from final build - debugging
        /*
        var CheatMap = IntArray(26)
        for ((box,amt) in DONDBoxesContents) CheatMap[amt] = box
        */

        @Composable fun MLSBox(btnNum:Int) {
            val avail = amountAvail[btnNum]!!
            val fontSz = AmountFontSize
            var openAmountColor = (if (avail) AmountAvailColor else AmountNotAvailColor)
            if (btnNum == AmountOpened) {
                when (passBoxOpen) {
                    1 -> { openAmountColor = AmountAvailColor }
                    2 -> { openAmountColor = AmountNotAvailColor }
                }
            }
            Box(
                modifier = Modifier
                    .height(boxHgt.dp)
                    .width(boxWid.dp)
                    .background(Color.Blue, RoundedCornerShape(90)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Amount[btnNum].toString(),
                    fontSize = fontSz.sp,
                    color = openAmountColor,
                )
            }
        }

        // show available amounts and grey out BoxContents[BoxChosen]
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!beSilent && !alreadySpoken) {
                DONDUtter(hostWords.spoken)
                alreadySpoken = true
            }
            Text(
                text = hostWords.screen,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = hostWordFontSize.sp,
            )
            Text(
                text = stringResource(id = R.string.titleAmountsLeftInPlay),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = (hostWordFontSize-2).sp,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(12.dp))
            for (row in 1..nBoxes / 2) {
                @Suppress("UnnecessaryVariable")
                val p1 = row
                val p2 = row + nBoxes / 2
                // first box on this row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    MLSBox(btnNum = p1)
                    MLSBox(btnNum = p2)
                }
            }
            @Suppress("KotlinConstantConditions")
            if ((nBoxes % 2) == 1) {
                val p = nBoxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                     MLSBox(btnNum = p)
                }
            }
            Spacer(modifier = Modifier.height((12.dp)))
            Button(onClick = onOKClick) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    }

    @Composable
    fun OfferScreen(
        theOffer: hostDialogue,
        playerAnswer: (Boolean) -> Unit
    ) {
        val buttonWid = 70
        val fontSizeAnswer = buttonWid/4
        val spaceBetweenAnswers = buttonWid/2
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            DONDUtter(theOffer.spoken)
            Text(
                text = theOffer.screen,
                fontSize = hostWordFontSize.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { playerAnswer(true) },
                    modifier = Modifier.size(buttonWid.dp),
                    contentPadding = PaddingValues(
                        start = 4.dp,
                        top = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    ),
                )
                {
                    Text(stringResource(id = R.string.DONDYes), fontSize = fontSizeAnswer.sp)
                }
                Spacer(modifier = Modifier.width(spaceBetweenAnswers.dp))
                Button(
                    onClick = { playerAnswer(false) },
                    modifier = Modifier.size(buttonWid.dp),
                    contentPadding = PaddingValues(
                        start = 4.dp,
                        top = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    ),
                )
                {
                    Text(stringResource(id = R.string.DONDNo), fontSize = fontSizeAnswer.sp)
                }
            }
        }
    }

    @Composable
    fun TimeForOffer(
        hostWords: hostDialogue,
        msgAcknowleged: () -> Unit
    ) {
        // for some reason, this fn gets composed twice.  Don't speak past first compose
        var alreadySpoken by remember { mutableStateOf(false) }
        if (!alreadySpoken) {
            DONDUtter(hostWords.spoken)
            alreadySpoken = true
        }

        AlertDialog(
            onDismissRequest = msgAcknowleged,
            confirmButton = {
                Button(onClick = msgAcknowleged) {
                    Text("Bring It!", fontSize = buttonFontSize.sp)
                }                
            },
            icon = { Icon(Icons.Default.AccountBalance,contentDescription = null) },
            title = {
                Text(stringResource(id = R.string.hostWord_TimeForOffer))
            },
            /*
            text = {
                Text(text = hostWords.screen, fontSize = hostWordFontSize.sp)
            },
            */
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun HowToPlay(
        goback: () -> Unit
    ) {
        val pagerState = rememberPagerState(pageCount = { 4 })
        BackHandler { goback() }
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(0.7f)
            ) { page ->
                // Our page content
                Text(modifier = Modifier.fillMaxWidth(),
                    fontFamily = if (page==0) FontFamily(Font(R.font.lemonregular)) else FontFamily.SansSerif,
                    fontSize = if (page==0) 48.sp else 20.sp,
                    lineHeight = if (page==0) 40.sp else 20.sp,
                    textAlign = TextAlign.Center,
                    text =
                    when (page) {
                        0 -> stringResource(id = R.string.DONDGameTitle)
                        1 -> "25 distinct Amounts from 1 to 1000000 are randomly placed in 25 Boxes, and the Boxes are sealed.\n" +
                                "\n" +
                                "At the beginning of the game, you will choose a Box.  This will be your Box through the entire game.  This Box will remain sealed until you either take an offer from the Banker or refuse all of the Banker's offers through the end.  If you play the game through to the end and refuse all of the Banker's offers, your Box will be opened and you will win the Amount in Your Box.\n"
                        2 -> "After you choose Your Box, you then open 6 Boxes, one at a time.  As each Box is opened, it's Amount is revealed, and you (and the Banker) knows that that Amount is not in Your Box.\n" +
                                "\n" +
                                "Once you open 6 Boxes, the Banker will make you an offer.  The Banker's offer will depend on the Amounts in the Boxes you've opened. The offer increases if lower values are eliminated and decreases if upper values are eliminated.\n" +
                                "\n" +
                                "If you accept the Banker's offer, Your Box is opened and you can see if the offer (which is what you've won) is more or less than what's in Your Box (which is what you would have won if you had refused offers until the end).\n" +
                                "\n" +
                                "If you refuse the Banker's offer, this process continues, except you must open 5 (then 4, then 3, then 2, then 1) Boxes until the Banker's next offer.\n"
                        3 -> "When there is just one Box left in addition to Your Box, the Banker will make his final offer.  If you refuse the final offer, you will win whatever is in Your Box.\n"
                        else -> ""
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    // .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(modifier = Modifier.fillMaxWidth().weight(.1f), horizontalAlignment = Alignment.Start) {
                    if (pagerState.currentPage>0) {
                        Icon(Icons.Default.NavigateBefore, contentDescription = null)
                    }
                }
                Column(modifier = Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = goback,
                    ) {
                        Text("Go Back To Game!", fontSize = buttonFontSize.sp)
                    }
                }
                Column(modifier = Modifier.fillMaxWidth().weight(.1f), horizontalAlignment = Alignment.End) {
                    if (pagerState.currentPage<pagerState.pageCount-1) {
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                    }
                }
            }
        }
    }
}


/*
@Composable
private fun AnimatingButton(btnNum: Int, transitionData: TransitionData) {
    // UI tree
    Box(
        modifier = Modifier
            .fillMaxWidth(.5f)
            .background(Color.Blue, RoundedCornerShape(90)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = Amount[btnNum].toString(),
            fontSize = transitionData.fontsize.sp,
            color = transitionData.color,
        )
    }
}
*/


/******************************
 * PREVIEWS
 *****************************/

@Preview(showBackground = true)
@Composable
fun DONDSplashScreenPreview() {
    BankerCheatsTheme {
        DONDScreens.DONDSplashScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val dummyMap = mutableMapOf<Int,Boolean>()
    val hostWords = hostDialogue(screen = "Host Words Go Here")
    for (i in 1..nBoxes) { dummyMap[i] = true }
    BankerCheatsTheme {
        DONDScreens.MainScreen(
            DONDBoxesVisiblity = dummyMap,
            hostWords = hostWords,
            onBoxOpen = { },
            miscfunctions = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MoneyListScreenPreview() {
    val dummyVisibleMap = mutableMapOf<Int,Boolean>()
    val dummyContentsMap = mutableMapOf<Int,Int>()
    val dummyAvailMap = mutableMapOf<Int,Boolean>()
    val hostWords = hostDialogue(screen = "Boxes contain Money!!")
    // var CheatMap = mutableMapOf<Int, Int>()     //DONE: remove this in final build
    for (i in 1..nBoxes) {
        dummyVisibleMap[i] = true
        dummyContentsMap[i] = i
        dummyAvailMap[i] = true
        // CheatMap[i] = i   //DONE: Remove from final build - debugging only
    }
    BankerCheatsTheme {
        DONDScreens.MoneyListScreen(
            // CheatMap,
            hostWords = hostWords,
            amountAvail = dummyAvailMap,
            onOKClick = { },
        )
    }
}
@Preview(showBackground = true)
@Composable
fun OfferScreenPreview() {
    BankerCheatsTheme {
        DONDScreens.OfferScreen(
            theOffer = hostDialogue(screen="The Offer Sucks"),
            playerAnswer = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeForOfferPreview() {
    BankerCheatsTheme {
        DONDScreens.TimeForOffer(
            hostDialogue(screen = "Do You Want Money?", spoken=stringResource(id = R.string.hostWord_TimeForOffer)),
        ) {}
    }
}
