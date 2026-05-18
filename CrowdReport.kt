package com.vidyarthibus.app.model

data class CrowdReport(
    val reportId: String = "",
    val routeId: String = "",
    val userId: String = "",
    val statusName: String = CrowdStatus.EMPTY.name,
    val timestamp: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    val status: CrowdStatus get() = CrowdStatus.fromName(statusName)
}
