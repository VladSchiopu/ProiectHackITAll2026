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

        // State pentru Input Nume
        var userNameInput by remember { mutableStateOf("") }
        var currentUserId by remember { mutableStateOf("") }
        var isRegistered by remember { mutableStateOf(false) }

        val studyTag = "kotlin"

        // Ascultăm datele globale
        val availablePartners by repository.getAvailablePartners(studyTag, currentUserId).collectAsState(emptyList())
        val activeSessions by repository.getActiveSessions().collectAsState(emptyList())

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            if (!isRegistered) {
                // ECRAN ÎNREGISTRARE (Username)
                Text("Introduceți un nume pentru a începe", style = MaterialTheme.typography.headlineSmall)
                TextField(
                    value = userNameInput,
                    onValueChange = { userNameInput = it },
                    label = { Text("Username") }
                )
                Button(
                    onClick = {
                        if (userNameInput.isNotBlank()) {
                            currentUserId = "user_${userNameInput.lowercase()}"
                            isRegistered = true
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Salvează și Intră") }
            } else {
                // ECRAN MATCHMAKING
                Text("Salut, $userNameInput!", style = MaterialTheme.typography.headlineSmall)
                Text("ID: $currentUserId | Tag: $studyTag")

                Spacer(Modifier.height(20.dp))

                Button(onClick = {
                    scope.launch {
                        val userProfile = User(
                            id = currentUserId,
                            name = userNameInput,
                            studySubject = studyTag,
                            isAvailable = true
                        )
                        repository.updateProfile(userProfile)
                    }
                }) { Text("Caută Partener (Start Session)") }

                Spacer(Modifier.height(20.dp))

                Text("Parteneri online: ${availablePartners.size}")
                availablePartners.forEach { partner ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Partner: ${partner.name}", modifier = Modifier.weight(1f))
                            Button(onClick = {
                                scope.launch {
                                    val sessionId = "session_${currentUserId}_${partner.id}"
                                    val newSession = StudySession(
                                        id = sessionId, // Setează explicit ID-ul aici!
                                        creatorId = currentUserId,
                                        participantIds = listOf(currentUserId, partner.id),
                                        subject = studyTag,
                                        isActive = true
                                    )
                                    repository.createStudySession(newSession)
                                }
                            }) { Text("Match!") }
                        }
                    }
                }

                // Detectăm dacă suntem într-o sesiune activă
                val mySession = activeSessions.find { it.participantIds.contains(currentUserId) }
                if (mySession != null) {
                    VideoCallScreen(mySession, repository)
                }
            }
        }
    }
}

@Composable
fun VideoCallScreen(session: StudySession, repository: AppRepository) {
    val scope = rememberCoroutineScope()
    // Colectăm starea din Flow-ul de Firebase
    val tokenState by repository.watchSessionToken(session.id).collectAsState("")

    // Capturăm valoarea într-o variabilă locală pentru a permite Smart Cast
    val currentToken = tokenState
    val serverUrl = "wss://vostru-proiect.livekit.cloud"

    Box(Modifier.background(Color.Black).fillMaxSize()) {
        // Folosim variabila locală curentă
        if (!currentToken.isNullOrEmpty()) {
            LaunchedEffect(currentToken) {
                // Acum compilatorul știe sigur că currentToken este String non-null
                connectToLiveKit(serverUrl, currentToken)
            }

            Text("Sunteți în direct!", color = Color.Green, modifier = Modifier.align(Alignment.Center))
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Button(
            onClick = { scope.launch { repository.endSession(session) } },
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp)
        ) { Text("Închide Apel") }
    }
}