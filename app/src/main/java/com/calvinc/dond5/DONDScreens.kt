package com.calvinc.dond5

import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.calvinc.dond5.DONDGlobals.SplashDone
import com.calvinc.dond5.DONDGlobals.hostWordFontSize
import com.calvinc.dond5.DONDGlobals.intMyBox
import com.calvinc.dond5.DONDGlobals.nBoxes
import com.calvinc.dond5.ui.theme.BankerCheatsTheme
import kotlinx.coroutines.delay

// TODO: const val for hostWords fontSize
object DONDScreens {

    @Composable
    fun DONDSplashScreen() {
        var showSplash by remember { mutableStateOf(true) }
        val SPLASH_DELAY: Long = 5000

        LaunchedEffect(key1 = Unit) {
            delay(SPLASH_DELAY)
            showSplash = false
            SplashDone = true
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
        DONDBoxesVisiblity:Map<Int,Boolean>,
        hostWords:String, congrats:String = "",
        onBoxOpen: (n:Int) -> Unit,
        miscfunctions: (f:String) -> Unit,
        DONDBoxesContents:Map<Int,Int> = mapOf()
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
            DONDUtter(hostWords)
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
                            Column () {
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

    @Composable
    fun MoneyListScreen(
        hostWords:String,
        boxOpened:Int = 0,
        amountAvail:Map<Int,Boolean>,
        onOKClick: () -> Unit,
    ) {
        val AmountFontSize = 20
        val AmountAvailColor = Color.Green
        val AmountNotAvailColor = Color.Gray
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
                    //DONDAmountButton(btnNum = p1, avail = amountAvail[p1]!!)
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        elevation = ButtonDefaults.buttonElevation(),
                    ) {
                        val btnNum = p1; val avail = amountAvail[p1]!!
                        Text(
                            text = Amount[btnNum].toString(),
                            fontSize = AmountFontSize.sp,
                            color = if (avail) AmountAvailColor else AmountNotAvailColor
                        )
                    }
                    // second box on this row
                    // DONDAmountButton(btnNum = p2, avail = amountAvail[p2]!!)
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        elevation = ButtonDefaults.buttonElevation(),
                    ) {
                        val btnNum = p2; val avail = amountAvail[p2]!!
                        Text(
                            text = Amount[btnNum].toString(),
                            fontSize = AmountFontSize.sp,
                            color = if (avail) AmountAvailColor else AmountNotAvailColor
                        )
                    }
                }
            }
            if ((nBoxes % 2) == 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val p = nBoxes
                    // DONDAmountButton(btnNum = p, avail = amountAvail[p]!!)
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        elevation = ButtonDefaults.buttonElevation(),
                    ) {
                        val btnNum = p; val avail = amountAvail[p]!!
                        Text(
                            text = Amount[btnNum].toString(),
                            fontSize = AmountFontSize.sp,
                            color = if (avail) AmountAvailColor else AmountNotAvailColor
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
    // TODO: figure out how to return a Button with the parms I want - what's here isn't it
    //  also need to figure out how to do the equivalent of Modifier.weight
    fun DONDAmountButton(contngRow:RowScope, btnNum:Int, avail: Boolean, flash: Boolean = false) {
        val AmountFontSize = 20
        val AmountAvailColor = Color.Green
        val AmountNotAvailColor = Color.Gray
        var retVal: Button
        if (!flash) {
            Button(
                modifier = Modifier,
                onClick = {},
                elevation = ButtonDefaults.buttonElevation(),
            ) {
                Text(
                    text = Amount[btnNum].toString(),
                    fontSize = AmountFontSize.sp,
                    color = if (avail) AmountAvailColor else AmountNotAvailColor
                )
            }
        } else {
            // TODO: animate the amount
        }
    }

    @Composable
    fun OfferScreen(
        theOffer: String,
        playerAnswer: (Boolean) -> Unit
    ) {
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
                modifier = Modifier.fillMaxWidth(.8f),
                horizontalArrangement = Arrangement.Center
            ) {
                val buttonWid = 70.dp
                Button(
                    onClick = { playerAnswer(true) },
                    modifier = Modifier.size(buttonWid),
                    contentPadding = PaddingValues(
                        start = 4.dp,
                        top = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    ),
                )
                {
                    Text(stringResource(id = R.string.DONDYes), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = { playerAnswer(false) },
                    modifier = Modifier.size(buttonWid),
                    contentPadding = PaddingValues(
                        start = 4.dp,
                        top = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    ),
                )
                {
                    Text(stringResource(id = R.string.DONDNo), fontSize = 14.sp)
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
