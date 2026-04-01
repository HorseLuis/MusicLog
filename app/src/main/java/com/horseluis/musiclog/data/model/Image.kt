package com.horseluis.musiclog.data.model

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("#text")
    val text: String,
    val size: String
)
