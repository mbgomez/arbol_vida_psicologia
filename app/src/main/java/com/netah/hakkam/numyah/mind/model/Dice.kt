package com.netah.hakkam.numyah.mind.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiceData(
    val firstDieValue: Int = 0,
    val secondDieValue: Int = 0,
)  : Parcelable