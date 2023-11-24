package com.calvinc.dond5

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.calvinc.dond5.ui.theme.BankerCheatsTheme
import kotlinx.coroutines.delay

object DONDComposables {

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
        onBoxOpen: (n:Int) -> Unit,
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
                            onClick = { onBoxOpen(row + col) },
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
        hostWords:String,
        amountAvail:Map<Int,Boolean>,
        onOKClick: () -> Unit,
    ) { // show available amounts and grey out CaseContents[CaseChosen]
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hostWords,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.titleAmountsLeftInPlay),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
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
    fun OfferScreen(
        theOffer: String,
        playerAnswer: (Boolean) -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = theOffer,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(.8f),
                horizontalArrangement = Arrangement.Center
            ) {
                val buttonWid = 100.dp
                Button(
                    onClick = { playerAnswer(true) },
                    modifier = Modifier.size(buttonWid),
                )
                {
                    Text(stringResource(id = R.string.DONDYes))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = { playerAnswer(false) },
                    modifier = Modifier.size(buttonWid),
                )
                {
                    Text(stringResource(id = R.string.DONDNo))
                }
            }
        }
    }

    @Composable
    fun TimeForOffer(
        hostWords: String,
        msgAcknowleged: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = msgAcknowleged,
            confirmButton = {
                Button(onClick = msgAcknowleged) {
                    Text("Bring It!")
                }                
            },
            icon = { Icon(Icons.Default.AccountBalance,contentDescription = null) },
            title = {
                Text("It's Time For An Offer!")
            },
            text = {
                Text(text = hostWords)
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
        DONDComposables.DONDSplashScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val dummyMap = mutableMapOf<Int,Boolean>()
    for (i in 1..DONDGlobals.nCases) { dummyMap[i] = true }
    BankerCheatsTheme {
        DONDComposables.MainScreen(
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
        DONDComposables.MoneyListScreen(
            hostWords = "Cases contain Money!!",
            amountAvail = dummyAvailMap,
            onOKClick = { },
        )
    }
}
@Preview(showBackground = true)
@Composable
fun OfferScreenPreview() {
    BankerCheatsTheme {
        DONDComposables.OfferScreen(
            theOffer = "The Offer Sucks",
            playerAnswer = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeForOfferPreview() {
    BankerCheatsTheme {
        DONDComposables.TimeForOffer(
            "Do You Want Money?",
            {}
        )
    }
}
