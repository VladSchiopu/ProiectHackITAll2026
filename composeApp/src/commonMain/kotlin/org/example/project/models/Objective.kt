package org.example.project.models

import kotlinx.serialization.Serializable

@Serializable
data class Objective(
    // Pentru Firebase e recomandat să ai și un constructor gol (valori default)
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val tag: String = "",
    val totalMinutesSpent: Int = 0,
    val isCompleted: Boolean = false
)