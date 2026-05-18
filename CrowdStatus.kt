package com.vidyarthibus.app.model

enum class CrowdStatus(val label: String, val progressValue: Int) {
    EMPTY("Empty — Seats Available", 33),
    SEATED("Seated — Few Seats Left", 66),
    FULL("Full — No More Seats", 100);

    companion object {
        fun fromName(name: String): CrowdStatus =
            values().firstOrNull { it.name == name } ?: EMPTY
    }
}
