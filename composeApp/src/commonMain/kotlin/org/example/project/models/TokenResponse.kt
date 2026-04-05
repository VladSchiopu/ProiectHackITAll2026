package org.example.project.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String,
    val serverUrl: String
)