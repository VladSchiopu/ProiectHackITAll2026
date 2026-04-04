package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // IMPORT NOU
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.repository.AppRepository
import org.example.project.models.StudySession
import org.example.project.models.User

@Composable
fun App() {
    MaterialTheme {
        val repository = remember { AppRepository() }
        val scope = rememberCoroutineScope()

        // Generăm un ID unic per tab pentru testare
        val currentUserId = remember { "user_${(1..100).random()}" }
        val studyTag = "kotlin"

        val availablePartners by repository.getAvailablePartners(studyTag, currentUserId).collectAsState(emptyList())
        val activeSessions by repository.getActiveSessions().collectAsState(emptyList())

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("User ID: $currentUserId", style = MaterialTheme.typography.headlineSmall)
            Text("Tag studiu: $studyTag")

            Spacer(Modifier.height(20.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        // REPARARE: Creăm/Update-ăm profilul complet pentru a evita eroarea NOT_FOUND
                        val userProfile = User(
                            id = currentUserId,
                            name = "Student $currentUserId",
                            studySubject = studyTag,
                            isAvailable = true // Îl facem disponibil direct aici
                        )
                        repository.updateProfile(userProfile)

                        // Opțional, forțăm și disponibilitatea dacă documentul exista deja
                        repository.setAvailability(currentUserId, true, studyTag)
                    } catch (e: Exception) {
                        println("Eroare la activare: ${e.message}")
                    }
                }
            }) {
                Text("Caută Partener (Start Session)")
            }

            Spacer(Modifier.height(20.dp))

            // Vizualizare parteneri în timp real
            Text("Parteneri online pentru $studyTag: ${availablePartners.size}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            availablePartners.forEach { partner ->
                // DEBUG: Verifică dacă ID-ul nu e gol
                val partnerId = if (partner.id.isEmpty()) "ID_NECUNOSCUT" else partner.id

                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Partener: $partnerId") // Folosește variabila de debug

                        Button(onClick = {
                            if (partner.id.isNotEmpty() && currentUserId.isNotEmpty()) {
                                scope.launch {
                                    val newSession = StudySession(
                                        id = "session_${currentUserId}_${partner.id}", // Setează manual un ID pentru sesiune
                                        creatorId = currentUserId,
                                        participantIds = listOf(currentUserId, partner.id),
                                        subject = studyTag,
                                        liveKitRoomName = "room_${currentUserId}_${partner.id}",
                                        startTime = 0.0,
                                        isActive = true
                                    )
                                    // Folosim document().set() în loc de add() pentru a fi siguri de ID
                                    repository.createStudySession(newSession)
                                }
                            } else {
                                println("Eroare: Unul dintre ID-uri este gol!")
                            }
                        }) {
                            Text("Match!")
                        }
                    }
                }
            }

            // AUTO-JOIN: Dacă apare o sesiune în care suntem incluși, afișăm ecranul de apel
            // Detectăm dacă suntem parte dintr-o sesiune
            val mySession = activeSessions.find { it.participantIds.contains(currentUserId) }

            if (mySession != null && mySession.id.isNotEmpty()) {
                VideoCallScreen(mySession, repository)
            }
        }
    }
}

// FUNCȚIA MUTATĂ ÎN AFARĂ
@Composable
fun VideoCallScreen(session: StudySession, repository: AppRepository) {
    val scope = rememberCoroutineScope() // Avem nevoie de scope pentru apeluri Firebase
    val token by repository.watchSessionToken(session.id).collectAsState("")

    Column(Modifier.background(Color.Black).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // ... restul UI-ului tău ...

        Button(onClick = {
            scope.launch {
                try {
                    // Trimitem obiectul 'session' primit ca parametru în VideoCallScreen
                    repository.endSession(session)
                } catch (e: Exception) {
                    println("Eroare la închidere: ${e.message}")
                }
            }
        }) {
            Text("Închide Apel", color = Color.White)
        }
    }
}