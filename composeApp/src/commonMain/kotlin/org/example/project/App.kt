package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.repository.AppRepository
import org.example.project.models.StudySession
import org.example.project.models.User
import org.example.project.models.Objective
import org.example.project.theme.*
import org.example.project.ui.HomeScreen
import org.example.project.ui.LoginScreen
import org.example.project.ui.ObjectivesScreen
import org.example.project.ui.SessionPrepareScreen
import org.example.project.ui.MatchmakingScreen
import org.example.project.ui.StatisticsScreen

// Definirea ecranelor pentru navigare
enum class Screen { HOME, OBJECTIVES, SESSION_PREPARE, MATCHMAKING, STATISTICS }

@Composable
fun App() {
    MaterialTheme(
        typography = AppTypography() // <--- AI ADĂUGAT DOAR LINIA ASTA
    ) {
        val repository = remember { AppRepository() }
        val scope = rememberCoroutineScope()

        // State-uri pentru Navigare și User
        var currentScreen by remember { mutableStateOf(Screen.HOME) }
        var userNameInput by remember { mutableStateOf("") }
        var currentUserId by remember { mutableStateOf("") }
        var isRegistered by remember { mutableStateOf(false) }

        // Persistent Login pe Tab curent
        LaunchedEffect(Unit) {
            val savedName = kotlinx.browser.window.sessionStorage.getItem("tab_user_nickname")
            if (!savedName.isNullOrBlank()) {
                userNameInput = savedName
                currentUserId = "user_${savedName.lowercase().replace(" ", "")}"
                isRegistered = true
            }
        }

        // State-uri pentru Date
        val objectives by repository.getUserObjectives(currentUserId).collectAsState(emptyList())
        val completedSessions by repository.getCompletedSessions(currentUserId).collectAsState(emptyList())
        val activeSessions by repository.getActiveSessions().collectAsState(emptyList())

        var selectedObjective by remember { mutableStateOf<Objective?>(null) }

        // Căutăm sesiunea activă a utilizatorului curent
        val myActiveSession = activeSessions.find { it.participantIds.contains(currentUserId) }

        // LOGICA DE NEW TAB: Monitorizăm sesiunea activă
        LaunchedEffect(myActiveSession?.liveKitToken) {
            val session = myActiveSession
            val token = session?.liveKitToken

            if (session != null && !token.isNullOrEmpty()) {
                // Avem token -> deschidem în TAB NOU
                val baseUrl = "https://meet.livekit.io/custom?liveKitUrl=wss://sheep-064e38km.livekit.cloud&token="
                openVideoSession(baseUrl + token)

                // IMPORTANT: NU mai apelăm repository.endSession aici automat.
                // Lăsăm sesiunea ACTIVĂ în DB pentru ca alții să o poată vedea și să dea Match.
                scope.launch {
                    delay(1000)
                    currentScreen = Screen.HOME // Revenim la Home în tab-ul principal
                }
            } else if (session != null && token.isNullOrEmpty()) {
                // Suntem în sesiune dar nu avem token încă
                val tokenUrl = "http://localhost:3000/get-token?user=$currentUserId&room=${session.id}"
                fetchAndStoreToken(tokenUrl, session.id, repository)
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = DeepPurple) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isRegistered) {
                    LoginScreen { name ->
                        kotlinx.browser.window.sessionStorage.setItem("tab_user_nickname", name)
                        userNameInput = name
                        currentUserId = "user_${name.lowercase().replace(" ", "")}"
                        isRegistered = true
                    }
                } else {
                    // Header Navigare
                    // --- HEADER NAVIGARE STILIZAT ---
                    Box(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        contentAlignment = Alignment.Center // Aceasta forțează textul "StudyMirror" exact pe mijloc
                    ) {
                        // Butonul de Back pus ancorat la stânga
                        if (currentScreen != Screen.HOME) {
                            TextButton(
                                onClick = { currentScreen = Screen.HOME },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Text("Home", color = NeonGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Titlul Aplicației pe centru
                        Text(
                            text = "StudyMirror",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = White // Textul alb, așa cum ai cerut
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // AFIȘARE ECRANE
                    when (currentScreen) {
                        Screen.HOME -> HomeScreen(userNameInput, myActiveSession, repository) { currentScreen = it }
                        // AICI ESTE MODIFICAREA
                        Screen.OBJECTIVES -> ObjectivesScreen(
                            userId = currentUserId,
                            objectives = objectives,
                            allSessions = completedSessions + activeSessions, // Combinăm sesiunile ca în Statistics
                            repository = repository,
                            onObjectiveClick = { clickedObj ->
                                selectedObjective = clickedObj // Setăm obiectivul ales
                                currentScreen = Screen.MATCHMAKING // Sărim direct în ecranul de căutare partener!
                            }
                        )
                        Screen.SESSION_PREPARE -> SessionPrepareScreen(objectives) { obj ->
                            selectedObjective = obj
                            currentScreen = Screen.MATCHMAKING
                        }
                        Screen.MATCHMAKING -> {
                            selectedObjective?.let { obj ->
                                MatchmakingScreen(currentUserId, userNameInput, obj, repository)
                            } ?: run { currentScreen = Screen.HOME }
                        }
                        Screen.STATISTICS -> StatisticsScreen(objectives, completedSessions, activeSessions, currentUserId)
                    }
                }
            }
        }
    }
}

//@Composable
//fun HomeScreen(name: String, activeSession: StudySession?, repository: AppRepository, onNavigate: (Screen) -> Unit) {
//    val scope = rememberCoroutineScope()
//    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
//        Text("Salut, $name!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
//        Text("Ești gata de studiu?", color = Color.Gray)
//
//        // CARD SESIUNE ACTIVĂ: Apare doar dacă utilizatorul are un apel pornit
//        if (activeSession != null) {
//            Spacer(Modifier.height(20.dp))
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
//            ) {
//                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
//                    Column(Modifier.weight(1f)) {
//                        Text("Apel video în curs...", fontWeight = FontWeight.Bold)
//                        Text("Tag: ${activeSession.subject}", fontSize = 12.sp)
//                    }
//                    Button(
//                        onClick = { scope.launch { repository.endSession(activeSession) } },
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                    ) {
//                        Text("Finalizează", color = Color.White)
//                    }
//                }
//            }
//        }
//
//        Spacer(Modifier.height(30.dp))
//
//        Button(onClick = { onNavigate(Screen.OBJECTIVES) }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
//            Text("🎯 Obiectivele Mele", fontSize = 18.sp)
//        }
//        Spacer(Modifier.height(16.dp))
//        Button(onClick = { onNavigate(Screen.SESSION_PREPARE) }, modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))) {
//            Text("🎬 Start Sesiune Nouă", fontSize = 18.sp, color = Color.Black)
//        }
//        Spacer(Modifier.height(16.dp))
//        OutlinedButton(onClick = { onNavigate(Screen.STATISTICS) }, modifier = Modifier.fillMaxWidth().height(60.dp)) {
//            Text("📊 Statistici Progres", fontSize = 18.sp)
//        }
//    }
//}

//@Composable
//fun ObjectivesScreen(userId: String, objectives: List<Objective>, repository: AppRepository) {
//    val scope = rememberCoroutineScope()
//    var title by remember { mutableStateOf("") }
//    var tag by remember { mutableStateOf("") }
//
//    Column {
//        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
//            Column(Modifier.padding(16.dp)) {
//                Text("Adaugă Obiectiv", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Spacer(Modifier.height(8.dp))
//                TextField(value = title, onValueChange = { title = it }, label = { Text("Titlu") }, modifier = Modifier.fillMaxWidth())
//                Spacer(Modifier.height(8.dp))
//                TextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag (ex: unity)") }, modifier = Modifier.fillMaxWidth())
//                Button(onClick = {
//                    if (title.isNotBlank() && tag.isNotBlank()) {
//                        val id = "obj_${userId}_${(1..100000).random()}"
//                        scope.launch {
//                            repository.createObjective(Objective(id, userId, title, tag.lowercase().trim()))
//                            title = ""; tag = ""
//                        }
//                    }
//                }, modifier = Modifier.align(Alignment.End).padding(top = 12.dp)) { Text("Salvează") }
//            }
//        }
//        Spacer(Modifier.height(24.dp))
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(objectives) { obj ->
//                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
//                    Text("${obj.title} [#${obj.tag}]", Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
//}

//@Composable
//fun SessionPrepareScreen(objectives: List<Objective>, onSelected: (Objective) -> Unit) {
//    Column {
//        Text("Alege obiectivul de studiu:", fontSize = 22.sp, fontWeight = FontWeight.Bold)
//        Spacer(Modifier.height(16.dp))
//        if (objectives.isEmpty()) Text("⚠️ Nu ai obiective.", color = Color.Red)
//        LazyColumn {
//            items(objectives) { obj ->
//                OutlinedButton(onClick = { onSelected(obj) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
//                    Text(obj.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun MatchmakingScreen(userId: String, userName: String, objective: Objective, repository: AppRepository) {
//    val scope = rememberCoroutineScope()
//    val partners by repository.getAvailablePartners(objective.tag, userId).collectAsState(emptyList())
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))) {
//            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(objective.title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF6200EE))
//                Text("Căutăm parteneri pe #${objective.tag}")
//            }
//        }
//        Spacer(Modifier.height(20.dp))
//        Button(onClick = {
//            scope.launch {
//                repository.updateProfile(User(userId, userName, studySubject = objective.tag, isAvailable = true))
//                val sessionId = "room_$userId"
//                val tokenUrl = "http://localhost:3000/get-token?user=$userId&room=$sessionId"
//                repository.createStudySession(StudySession(
//                    id = sessionId, creatorId = userId, participantIds = listOf(userId),
//                    subject = objective.tag, objectiveId = objective.id, startTime = 0.0, isActive = true
//                ))
//                fetchAndStoreToken(tokenUrl, sessionId, repository)
//            }
//        }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("🚀 Start Solo / Devino Disponibil") }
//        Spacer(Modifier.height(32.dp))
//        Text("Utilizatori disponibili:", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
//        LazyColumn(Modifier.fillMaxSize()) {
//            items(partners) { partner ->
//                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
//                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
//                        Text(partner.name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
//                        Button(onClick = {
//                            scope.launch {
//                                val ids = listOf(userId, partner.id).sorted()
//                                val sessionId = "session_${ids[0]}_${ids[1]}"
//                                val tokenUrl = "http://localhost:3000/get-token?user=$userId&room=$sessionId"
//                                repository.createStudySession(StudySession(
//                                    id = sessionId, creatorId = userId, participantIds = listOf(userId, partner.id),
//                                    subject = objective.tag, objectiveId = objective.id, startTime = 0.0, isActive = true
//                                ))
//                                fetchAndStoreToken(tokenUrl, sessionId, repository)
//                            }
//                        }) { Text("Match!") }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun StatisticsScreen(objectives: List<Objective>, completed: List<StudySession>, active: List<StudySession>, currentUserId: String) {
//    val allSessions = completed + active
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text("📊 Statistici Progres", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//        Spacer(Modifier.height(16.dp))
//        LazyColumn {
//            items(objectives) { objective ->
//                val sessionsForObj = allSessions.filter { it.objectiveId == objective.id }
//                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))) {
//                    Column(Modifier.padding(16.dp)) {
//                        Text(objective.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
//                        Text("Total sesiuni: ${sessionsForObj.size}")
//                        sessionsForObj.forEach { session ->
//                            val status = if (session.isActive) "🟢 În curs" else "✅ Finalizat"
//                            val partnerId = session.participantIds.find { it != currentUserId } ?: "Solo"
//                            Text("• $status cu $partnerId", fontSize = 12.sp, color = Color.DarkGray)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun LoginScreen(onLogin: (String) -> Unit) {
//    var name by remember { mutableStateOf("") }
//    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
//        Text("StudyMirror", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color(0xFF6200EE))
//        Spacer(Modifier.height(30.dp))
//        TextField(value = name, onValueChange = { name = it }, label = { Text("Nume") }, modifier = Modifier.fillMaxWidth(0.8f))
//        Button(onClick = { if (name.isNotBlank()) onLogin(name) }, modifier = Modifier.padding(top = 20.dp)) { Text("Începe") }
//    }
//}