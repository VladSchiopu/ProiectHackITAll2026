package org.example.project.models

import kotlinx.serialization.Serializable

@Serializable
data class StudySession(
    val id: String = "",
    val creatorId: String = "",
    val participantIds: List<String> = emptyList(),
    val subject: String = "",
    val startTime: Long = 0,
    val isActive: Boolean = true,
    val liveKitRoomName: String = "", // Numele camerei LiveKit
    val liveKitToken: String? = null  // Tokenul va fi scris aici de Firebase Functions
)