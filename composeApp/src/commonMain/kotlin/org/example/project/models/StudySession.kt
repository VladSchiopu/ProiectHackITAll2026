package org.example.project.models

import kotlinx.serialization.Serializable

@Serializable
data class StudySession(
    val id: String = "",
    val creatorId: String = "",
    val participantIds: List<String> = emptyList(),
    val subject: String = "",
    val startTime: Double = 0.0, // SCHIMBĂ din Long în Double
    val isActive: Boolean = true,
    val liveKitRoomName: String = "",
    val liveKitToken: String = ""
)