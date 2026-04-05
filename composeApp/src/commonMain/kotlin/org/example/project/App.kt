package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        var userNameInput by remember { mutableStateOf("") }
        var currentUserId by remember { mutableStateOf("") }
        var isRegistered by remember { mutableStateOf(false) }

        val studyTag = "kotlin"

        val availablePartners by repository.getAvailablePartners(studyTag, currentUserId).collectAsState(emptyList())
        val activeSessions by repository.getActiveSessions().collectAsState(emptyList())

        // Identificăm dacă suntem într-o sesiune (fie creată de noi, fie de un partener)
        val mySession = activeSessions.find { it.participantIds.contains(currentUserId) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mySession != null) {
                // ECRAN APEL VIDEO
                VideoCallScreen(mySession, repository, currentUserId)
            } else if (!isRegistered) {
                // ECRAN ÎNREGISTRARE
                Text("Introduceți un nume pentru a începe", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = userNameInput,
                    onValueChange = { userNameInput = it },
                    label = { Text("Username") }
                )
                Button(
                    onClick = {
                        if (userNameInput.isNotBlank()) {
                            currentUserId = "user_${userNameInput.lowercase().replace(" ", "")}"
                            isRegistered = true
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Salvează și Intră") }
            } else {
                // ECRAN MATCHMAKING
                Text("Salut, $userNameInput!", style = MaterialTheme.typography.headlineSmall)

                Spacer(Modifier.height(20.dp))

                // BUTONUL MODIFICAT: Te face disponibil ȘI te bagă în sesiune preview
                Button(onClick = {
                    scope.launch {
                        try {
                            // 1. Te facem vizibil pentru alții
                            val userProfile = User(
                                id = currentUserId,
                                name = userNameInput,
                                studySubject = studyTag,
                                isAvailable = true
                            )
                            repository.updateProfile(userProfile)

                            // 2. Creăm o sesiune "individuală" pentru a deschide meet-ul imediat
                            // Folosim un ID de room fix sau bazat pe ID-ul tău pentru demo
                            val sessionId = "room_$currentUserId"
                            val tokenUrl = "http://localhost:3000/get-token?user=$currentUserId&room=$sessionId"

                            val previewSession = StudySession(
                                id = sessionId,
                                creatorId = currentUserId,
                                participantIds = listOf(currentUserId),
                                subject = studyTag,
                                isActive = true,
                                liveKitToken = ""
                            )
                            repository.createStudySession(previewSession)

                            // 3. Cerem token-ul (Asta va declanșa deschiderea Meet în VideoCallScreen)
                            fetchAndStoreToken(tokenUrl, sessionId, repository)

                        } catch (e: Exception) {
                            println("Eroare activare: ${e.message}")
                        }
                    }
                }) { Text("Devino Disponibil și Intră pe Meet") }

                Spacer(Modifier.height(24.dp))

                Text("Parteneri online: ${availablePartners.size}", style = MaterialTheme.typography.titleMedium)

                availablePartners.forEach { partner ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Partner: ${partner.name}", modifier = Modifier.weight(1f))

                            Button(onClick = {
                                scope.launch {
                                    try {
                                        // ID predictibil bazat pe ambii useri
                                        val ids = listOf(currentUserId, partner.id).sorted()
                                        val sessionId = "session_${ids[0]}_${ids[1]}"

                                        val tokenUrl = "http://localhost:3000/get-token?user=$currentUserId&room=$sessionId"

                                        val newSession = StudySession(
                                            id = sessionId,
                                            creatorId = currentUserId,
                                            participantIds = listOf(currentUserId, partner.id),
                                            subject = studyTag,
                                            isActive = true,
                                            liveKitToken = ""
                                        )
                                        repository.createStudySession(newSession)

                                        // Cerem token-ul pentru camera comună
                                        fetchAndStoreToken(tokenUrl, sessionId, repository)

                                    } catch (e: Exception) {
                                        println("Eroare Match: ${e.message}")
                                    }
                                }
                            }) { Text("Match!") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCallScreen(session: StudySession, repository: AppRepository, currentUserId: String) {
    val scope = rememberCoroutineScope()
    val tokenState by repository.watchSessionToken(session.id).collectAsState("")
    val currentToken = tokenState

    // URL-ul tău fix de LiveKit Cloud
    val baseUrl = "https://meet.livekit.io/custom?liveKitUrl=wss://sheep-064e38km.livekit.cloud&token="

    LaunchedEffect(currentToken) {
        val token = currentToken
        if (token != null && token.isNotEmpty()) {
            // Dacă există deja un token în DB (scris de cineva), îl folosim
            openVideoSession(baseUrl + token)
        } else {
            // Dacă am intrat în ecran dar token-ul e gol, înseamnă că suntem
            // partenerul care a fost ales. Ne luăm propriul token pentru acea cameră.
            val tokenUrl = "http://localhost:3000/get-token?user=$currentUserId&room=${session.id}"
            fetchAndStoreToken(tokenUrl, session.id, repository)
        }
    }

    Box(
        Modifier
            .background(Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.White)
            Spacer(Modifier.height(16.dp))
            Text("Conexiune LiveKit în curs...", color = Color.White)
            Text("Sesiune: ${session.id}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

            val token = currentToken
            if (token != null && token.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Button(onClick = { openVideoSession(baseUrl + token) }) {
                    Text("Reîncearcă deschiderea Meet")
                }
            }
        }

        // Buton pentru a ieși din sesiune și a curăța starea
        Button(
            onClick = {
                scope.launch {
                    repository.endSession(session)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Închide Sesiunea", color = Color.White)
        }
    }
}