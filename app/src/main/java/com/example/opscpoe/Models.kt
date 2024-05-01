package com.example.opscpoe

import android.net.Uri
import java.util.Date

data class Models(
    val category: String? = null,
    val date: Date? = null,
    val description: String? = null,
    val endTime: String? = null,
    val startTime: String? = null,
    val uri: String? = null //link to firebase storage
)
