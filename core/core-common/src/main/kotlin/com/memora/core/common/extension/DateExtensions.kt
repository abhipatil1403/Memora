package com.memora.core.common.extension

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Instant.toFormattedDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

fun Instant.toFormattedTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    return formatter.format(this)
}
