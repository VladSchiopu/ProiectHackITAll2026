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
fun ObjectivesScreen(
    userId: String,
    objectives: List<Objective>,
    allSessions: List<StudySession>, // NOU: Ca să putem calcula statisticile
    repository: AppRepository,
    onObjectiveClick: (Objective) -> Unit // NOU: Funcția care te trimite la sesiune
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple)
            .padding(16.dp)
    ) {

        Text(
            text = "Your Objectives",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- CARDUL DE ADĂUGARE ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonGreen, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = BlackAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Add New Objective", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NeonGreen)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White, unfocusedContainerColor = White,
                        focusedBorderColor = NeonGreen, unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = BlackAccent, unfocusedTextColor = BlackAccent,
                        cursorColor = NeonGreen
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag (e.g. unity, math)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White, unfocusedContainerColor = White,
                        focusedBorderColor = NeonGreen, unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = BlackAccent, unfocusedTextColor = BlackAccent,
                        cursorColor = NeonGreen
                    )
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && tag.isNotBlank()) {
                            val id = "obj_${userId}_${(1..100000).random()}"
                            scope.launch {
                                repository.saveObjective(Objective(id, userId, title, tag.lowercase().trim()))
                                title = ""
                                tag = ""
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BlackAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Current Objectives",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // --- LISTA DE OBIECTIVE (INTERACTIVĂ) ---
        if (objectives.isEmpty()) {
            Text("You haven't set any objectives yet.", color = Color.LightGray, fontStyle = FontStyle.Italic)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(objectives) { obj ->
                    // Calculăm sesiunile pentru acest obiectiv specific
                    val sessionsCount = allSessions.count { it.objectiveId == obj.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onObjectiveClick(obj) }, // MAGIA SE ÎNTÂMPLĂ AICI!
                        colors = CardDefaults.cardColors(containerColor = BlackAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(obj.title, color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                                Spacer(Modifier.height(6.dp))

                                // STATISTICILE OBIECTIVULUI
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("$sessionsCount sessions", color = Color.LightGray, fontSize = 12.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Text("${obj.totalMinutesSpent} mins", color = Color.LightGray, fontSize = 12.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = DeepPurple,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                                ) {
                                    Text(
                                        text = "#${obj.tag}",
                                        color = NeonGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Text care sugerează acțiunea (funcționează ca un fake-button)
                                Text("START", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}