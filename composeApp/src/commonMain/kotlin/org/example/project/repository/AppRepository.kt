package org.example.project.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.models.Objective
import org.example.project.models.User
import org.example.project.models.StudySession

class AppRepository {
    private val db = Firebase.firestore

    // --- LOGICĂ OBIECTIVE (Tasks) ---

    suspend fun saveObjective(objective: Objective) {
        // Dacă id-ul e gol, Firebase va genera unul automat
        if (objective.id.isEmpty()) {
            db.collection("objectives").add(objective)
        } else {
            db.collection("objectives").document(objective.id).set(objective)
        }
    }

    fun getObjectivesForUser(userId: String): Flow<List<Objective>> {
        return db.collection("objectives")
            .where("userId", equalTo = userId)
            .snapshots()
            .map { query -> query.documents.map { it.data<Objective>() } }
    }

    // --- LOGICĂ UTILIZATOR (Profil & Status) ---

    suspend fun updateProfile(user: User) {
        db.collection("users").document(user.id).set(user)
    }

    suspend fun setAvailability(userId: String, available: Boolean, subject: String = "") {
        db.collection("users").document(userId).update(
            "isAvailable" to available,
            "studySubject" to subject
        )
    }

    // În AppRepository.kt
    suspend fun updateSessionToken(sessionId: String, token: String) {
        db.collection("sessions").document(sessionId).update("liveKitToken" to token)
    }

    // --- LOGICĂ MATCHMAKING (Real-time) ---

    // Această funcție returnează o listă care se updatează singură când cineva intră/iese de la studiu
    fun getAvailablePartners(subject: String, currentUserId: String): Flow<List<User>> {
        return db.collection("users")
            .where("isAvailable", equalTo = true)
            .where("studySubject", equalTo = subject)
            .snapshots()
            .map { query ->
                query.documents
                    .map { it.data<User>() }
                    .filter { it.id != currentUserId }
            }
    }

    // --- LOGICĂ SESIUNI DE STUDIU ---

    suspend fun createStudySession(session: StudySession): String {
        val id = if (session.id.isEmpty()) {
            db.collection("sessions").add(session).id
        } else {
            db.collection("sessions").document(session.id).set(session)
            session.id
        }
        return id
    }

    // Adaugă/Actualizează în AppRepository.kt
    suspend fun endSession(session: StudySession) {
        // 1. Închidem sesiunea în DB
        db.collection("sessions").document(session.id).update(
            "isActive" to false,
            "liveKitToken" to ""
        )

        // 2. Resetăm disponibilitatea tuturor participanților
        session.participantIds.forEach { userId ->
            db.collection("users").document(userId).update(
                "isAvailable" to false
            )
        }
    }


    fun getActiveSessions(): Flow<List<StudySession>> {
        return db.collection("sessions")
            .where("isActive", equalTo = true)
            .snapshots()
            .map { query ->
                query.documents.mapNotNull { doc ->
                    try {
                        doc.data<StudySession>()
                    } catch (e: Exception) {
                        println("Eroare conversie sesiune: ${e.message}")
                        null // Ignorăm documentele corupte/incomplet create
                    }
                }
            }
    }

    fun watchSessionToken(sessionId: String): Flow<String?> {
        return db.collection("sessions").document(sessionId)
            .snapshots()
            .map { it.data<StudySession>().liveKitToken }
    }

    // Salvare obiectiv nou
    suspend fun createObjective(objective: Objective) {
        db.collection("objectives").document(objective.id).set(objective)
    }

    // Ascultăm obiectivele unui user
    fun getUserObjectives(userId: String): Flow<List<Objective>> {
        return db.collection("objectives")
            .where("userId", equalTo = userId)
            .snapshots()
            .map { query -> query.documents.mapNotNull { it.data<Objective>() } }
    }

    fun getCompletedSessions(userId: String): Flow<List<StudySession>> {
        return db.collection("sessions")
            .where("participantIds", arrayContains = userId)
            .where("isActive", equalTo = false) // Doar cele terminate
            .snapshots()
            .map { query -> query.documents.mapNotNull { it.data<StudySession>() } }
    }
}