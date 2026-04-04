package org.example.project.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profilePic: String = "",
    val studySubject: String = "", // Materia curentă
    val isAvailable: Boolean = false, // Dacă caută partener acum
    val bio: String = ""
)