package com.memora.core.model

enum class ActionType(val displayLabel: String) {
    OPEN_URL("Open Website"),
    CALL("Call"),
    EMAIL("Send Email"),
    OPEN_MAPS("Open Maps"),
    ADD_REMINDER("Add Reminder"),
    OPEN_LINK("Open Link")
}

data class SmartAction(
    val type: ActionType,
    val label: String,
    val value: String,
    val entityIndex: Int
)
