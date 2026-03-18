package com.netah.hakkam.numyah.mind.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val title: String? = null,
    val body: String? = null
) : Parcelable
