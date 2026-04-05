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
fun SessionPrepareScreen(objectives: List<Objective>, onSelected: (Objective) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple)
            .padding(16.dp)
    ) {
        Text(
            text = "Choose Your Objective",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (objectives.isEmpty()) {
            Text("You don't have any objectives yet. Go back and create one!", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(objectives) { obj ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelected(obj) }, // Click direct pe card!
                        colors = CardDefaults.cardColors(containerColor = BlackAccent),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen) // Chenar verde fin
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = obj.title,
                                color = White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text("SELECT", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}