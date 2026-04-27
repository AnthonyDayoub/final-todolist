package com.example.csci215_final.ui.components

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toDisplayDate(): String {
    val dt = toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "$month ${dt.dayOfMonth}, ${dt.year}"
}

fun Instant.toDisplayDateTime(): String {
    val dt = toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')
    return "$month ${dt.dayOfMonth}, ${dt.year}  $hour:$minute"
}
