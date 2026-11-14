package com.memora.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ActionType(val displayLabel: String) {
    OPEN_URL("Open Website"),
    CALL("Call"),
    EMAIL("Send Email"),
    OPEN_MAPS("Open Maps"),
    ADD_REMINDER("Add Reminder"),
    OPEN_LINK("Open Link")
}

@Serializable
data class SmartAction(
    val type: ActionType,
    val label: String,
    val value: String,
    val entityIndex: Int
)
