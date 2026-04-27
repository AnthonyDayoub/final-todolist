package com.example.csci215_final.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    @SerialName("q") val text: String,
    @SerialName("a") val author: String
)
