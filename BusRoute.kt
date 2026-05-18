package com.vidyarthibus.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusRoute(
    val routeId: String = "",
    val busNumber: String = "",
    val routeName: String = "",
    val stops: List<String> = emptyList()
) : Parcelable
