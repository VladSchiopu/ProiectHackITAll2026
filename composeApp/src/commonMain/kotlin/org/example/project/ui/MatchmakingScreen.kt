package org.example.project.ui

import org.example.project.models.User
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.models.Objective
import org.example.project.models.StudySession
import org.example.project.repository.AppRepository
import org.example.project.theme.*
import org.example.project.fetchAndStoreToken

@Composable
fun MatchmakingScreen(userId: String, userName: String, objective: Objective, repository: AppRepository) {
    val scope = rememberCoroutineScope()
    val partners by repository.getAvailablePartners(objective.tag, userId).collectAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- CARDUL OBIECTIVULUI CURENT ---
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, NeonGreen, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = BlackAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(objective.title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = White)
                Spacer(Modifier.height(8.dp))
                Text("Looking for partners on #${objective.tag}", color = NeonGreen, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(32.dp))

        // --- BUTONUL DE START SOLO ---
        Button(
            onClick = {
                scope.launch {
                    repository.updateProfile(User(userId, userName, studySubject = objective.tag, isAvailable = true))
                    val sessionId = "room_$userId"
                    val tokenUrl = "http://localhost:3000/get-token?user=$userId&room=$sessionId"
                    repository.createStudySession(StudySession(
                        id = sessionId, creatorId = userId, participantIds = listOf(userId),
                        subject = objective.tag, objectiveId = objective.id, startTime = 0.0, isActive = true
                    ))
                    // Aici chemi funcția ta care ia tokenul din Firebase Functions (AppRepository)
                    fetchAndStoreToken(tokenUrl, sessionId, repository)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BlackAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Start Solo / Become Available", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(40.dp))

        // --- LISTA DE PARTENERI ---
        Text(
            text = "Available Users:",
            fontWeight = FontWeight.Bold,
            color = White,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
        )

        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(partners) { partner ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlackAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(partner.name, Modifier.weight(1f), color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        Button(
                            onClick = {
                                scope.launch {
                                    val ids = listOf(userId, partner.id).sorted()
                                    val sessionId = "session_${ids[0]}_${ids[1]}"
                                    val tokenUrl = "http://localhost:3000/get-token?user=$userId&room=$sessionId"

                                    repository.createStudySession(StudySession(
                                        id = sessionId, creatorId = userId, participantIds = listOf(userId, partner.id),
                                        subject = objective.tag, objectiveId = objective.id, startTime = 0.0, isActive = true
                                    ))

                                    // Din nou, linia magică pentru generarea biletului LiveKit
                                    fetchAndStoreToken(tokenUrl, sessionId, repository)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BlackAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Match!", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}