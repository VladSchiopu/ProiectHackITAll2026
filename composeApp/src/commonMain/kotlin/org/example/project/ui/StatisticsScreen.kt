package org.example.project.ui

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

@Composable
fun StatisticsScreen(objectives: List<Objective>, completed: List<StudySession>, active: List<StudySession>, currentUserId: String) {
    val allSessions = completed + active

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple)
            .padding(16.dp)
    ) {
        Text(
            text = "Progress Statistics",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (objectives.isEmpty()) {
            Text("No data available yet. Complete some sessions!", color = Color.Gray, fontStyle = FontStyle.Italic)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(objectives) { objective ->
                    val sessionsForObj = allSessions.filter { it.objectiveId == objective.id }

                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, NeonGreen, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = BlackAccent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            // Header-ul cardului
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = objective.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White,
                                    modifier = Modifier.weight(1f)
                                )
                                Text("#${objective.tag}", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(Modifier.height(12.dp))
                            Text("Total sessions: ${sessionsForObj.size}", color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))

                            // Lista mică de sub fiecare obiectiv
                            sessionsForObj.forEach { session ->
                                val status = if (session.isActive) "Active" else "Completed"
                                val partnerId = session.participantIds.find { it != currentUserId } ?: "Solo"

                                Text(
                                    text = "• $status with $partnerId",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}