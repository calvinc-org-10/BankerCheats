package com.calvinc.dond5

import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
    const val hostWordFontSize = 20
    const val AmountFontSize = 20
    val AmountAvailColor = Color.Green
    val AmountNotAvailColor = Color.Gray

    /************************************
     * ANIMATION ("glow") CLASSES, etc
     ***********************************/

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


    @Composable
    fun DONDSplashScreen() {
        var showSplash by remember { mutableStateOf(true) }
        @Suppress("LocalVariableName") val SPLASH_DELAY: Long = 5000

        LaunchedEffect(key1 = Unit) {
            delay(SPLASH_DELAY)
            showSplash = false
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        )
        {
            if (showSplash) {
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
                    onClick = { CalvinCheat = true }
                ) { Text(stringResource(id = R.string.DONDGameAuthor)) }
            }
        }
    }

    @Composable
    fun MainScreen(
        @Suppress("LocalVariableName") DONDBoxesVisiblity:Map<Int,Boolean>,
        hostWords:String, congrats:String = "",
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = hostWords,
                fontSize = hostWordFontSize.sp
            )
            DONDUtter(hostWords,TextToSpeech.QUEUE_FLUSH)
            Spacer(modifier = Modifier.height(12.dp))
            for (row in 1 until nBoxes step cpr) {
                Row {
                    for (col in 0..4) {
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
                                    fontSize = (boxWid/4).sp
                                )
                                if (endGameReveal && DONDBoxesVisiblity[row + col]!!) {
                                    Text(
                                        String.format(
                                            "%1$,d",
                                            Amount[DONDBoxesContents[row+col]!!]
                                        ),
                                        fontSize = (boxWid/6).sp
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
    }

    @OptIn(ExperimentalTransitionApi::class)
    @Composable
    fun MoneyListScreen(
        hostWords:String,
        AmountOpened:Int = 0,
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
        val lftSpc1 = (scrWid * .05).toInt()

        // for handling the "glowing" amount
        /*
        val animationState = remember { MutableTransitionState(AnimationState.Starting) }
        animationState.targetState = AnimationState.Finished
        val transitionData = updateTransitionData(animationState = animationState)
        */
        /* val animationState = remember { MutableTransitionState(true) }
        animationState.targetState = true
        val transition = rememberTransition(animationState, "glow")
        val openAmountColor by transition.animateColor(label = "glowColor") {state ->
            when (state) {
                true -> AmountNotAvailColor
                else -> AmountAvailColor
            }
        }
        */
        var amountShouldGlow by remember { mutableStateOf(true) }
        val openAmountColor by animateColorAsState(
            targetValue = if (amountShouldGlow) AmountAvailColor else AmountNotAvailColor,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
        )


        // show available amounts and grey out BoxContents[BoxChosen]
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // DONDUtter(hostWords) - nope - this phrase is repeated on MainScreen
            Text(
                text = hostWords,
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
                val p1 = row
                val p2 = row + nBoxes / 2
                // first box on this row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Spacer(modifier = Modifier.width(lftSpc1.dp))
                    var btnNum = p1
                    var avail = amountAvail[p1]!!
                    Box(
                        modifier = Modifier
                            // .fillMaxWidth(.5f)
                            .height(boxHgt.dp)
                            .width(boxWid.dp)
                            .background(Color.Blue, RoundedCornerShape(90)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Amount[btnNum].toString(),
                            fontSize = if (btnNum == AmountOpened)
                                    // transitionData.fontsize.sp
                                    AmountFontSize.sp
                                else
                                    AmountFontSize.sp,
                            color = if (btnNum == AmountOpened)
                                    animateColorAsState(
                                        targetValue = AmountNotAvailColor,
                                        animationSpec = tween(1000)
                                    ).value
                                else
                                    (if (avail) AmountAvailColor else AmountNotAvailColor),
                        )
                    }
                    // second box on this row
                    btnNum = p2
                    avail = amountAvail[p2]!!
                    Box(
                        modifier = Modifier
                            // .fillMaxWidth(.5f)
                            .height(boxHgt.dp)
                            .width(boxWid.dp)
                            .background(Color.Blue, RoundedCornerShape(90)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Amount[btnNum].toString(),
                            fontSize = if (btnNum == AmountOpened)
                                // transitionData.fontsize.sp
                                AmountFontSize.sp
                            else
                                AmountFontSize.sp,
                            color = if (btnNum == AmountOpened)
                                animateColorAsState(
                                    targetValue = AmountNotAvailColor,
                                    animationSpec = tween(1000)
                                ).value
                            else
                                (if (avail) AmountAvailColor else AmountNotAvailColor),
                        )
                    }
                }
            }
            @Suppress("KotlinConstantConditions")
            if ((nBoxes % 2) == 1) {
                val p = nBoxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var btnNum = p
                    var avail = amountAvail[p]!!
                    Box(
                        modifier = Modifier
                            // .fillMaxWidth(.5f)
                            .height(boxHgt.dp)
                            .width(boxWid_last.dp)
                            .background(Color.Blue, RoundedCornerShape(90)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Amount[btnNum].toString(),
                            fontSize = if (btnNum == AmountOpened)
                                // transitionData.fontsize.sp
                                AmountFontSize.sp
                            else
                                AmountFontSize.sp,
                            color = if (btnNum == AmountOpened)
                                animateColorAsState(
                                    targetValue = AmountNotAvailColor,
                                    animationSpec = tween(1000)
                                ).value
                            else
                                (if (avail) AmountAvailColor else AmountNotAvailColor),
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
    fun OfferScreen(
        theOffer: String,
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
            DONDUtter(theOffer)
            Text(
                text = theOffer,
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
        hostWords: String,
        msgAcknowleged: () -> Unit
    ) {
        val buttonFontSize = 18
        AlertDialog(
            onDismissRequest = msgAcknowleged,
            confirmButton = {
                Button(onClick = msgAcknowleged) {
                    Text("Bring It!", fontSize = buttonFontSize.sp)
                }                
            },
            icon = { Icon(Icons.Default.AccountBalance,contentDescription = null) },
            title = {
                Text("It's Time For An Offer!")
            },
            text = {
                DONDUtter(hostWords)
                Text(text = hostWords, fontSize = hostWordFontSize.sp)
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
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
    for (i in 1..nBoxes) { dummyMap[i] = true }
    BankerCheatsTheme {
        DONDScreens.MainScreen(
            DONDBoxesVisiblity = dummyMap,
            hostWords = "Host Words Go Here",
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
    for (i in 1..nBoxes) {
        dummyVisibleMap[i] = true
        dummyContentsMap[i] = i
        dummyAvailMap[i] = true
    }
    BankerCheatsTheme {
        DONDScreens.MoneyListScreen(
            hostWords = "Boxes contain Money!!",
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
            theOffer = "The Offer Sucks",
            playerAnswer = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeForOfferPreview() {
    BankerCheatsTheme {
        DONDScreens.TimeForOffer(
            "Do You Want Money?",
            {}
        )
    }
}
