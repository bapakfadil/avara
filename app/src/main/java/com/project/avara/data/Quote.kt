package com.project.avara.data

import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("q") val quote: String,
    @SerializedName("a") val author: String,
    @SerializedName("h") val formattedQuote: String
)