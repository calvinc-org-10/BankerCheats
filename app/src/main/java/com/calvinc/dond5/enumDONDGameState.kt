package com.calvinc.dond5

import kotlin.reflect.KProperty

enum class enumDONDGameState {
    DONDInit,
    DONDActivateGame,
    DONDPickMyBox,
    DONDStartNewRound,
    DONDChooseNextBox,
    DONDPrepareForOffer,
    DONDCalculateOffer,
    DONDMakeOffer,
    DONDOfferRefused,   // like DONDChooseNextBox, except hostWords say Offer Refused aznd gives # boxes to open
    DONDShowAmountsLeft,
    DONDCongratulate,
    DONDShowRules,
    DONDGetUserEndgameDecision,
    DONDEndGame
    ;
}
