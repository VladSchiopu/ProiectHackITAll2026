// Calea: org.example.project.ui.HomeScreen.kt
package org.example.project.ui

// ... importuri ...
// --- IMPORTURILE DE BAZĂ PENTRU COMPOSE ---
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// --- IMPORTURILE DIN PROIECTUL TĂU ---
import org.example.project.Screen
import org.example.project.repository.AppRepository
import org.example.project.models.StudySession
import org.example.project.theme.* // Aduce culorile (DeepPurple, NeonGreen, etc.)
import org.example.project.components.* // Aduce butoanele (MainButton, etc.)

// --- ECRANUL HOME REFĂCUT ---
@Composable
fun HomeScreen(
    name: String,
    activeSession: StudySession?,
    repository: AppRepository,
    onNavigate: (Screen) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Am adăugat scroll în caz că ecranul e prea mic pentru toate butoanele
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple) // Aplicăm fundalul mov aici
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- HEADER / LOGO ---
        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = "STUDDY BUDDY",
//            color = White,
//            fontSize = 28.sp,
//            fontWeight = FontWeight.ExtraBold
//        )
        Text(
            text = "Welcome back, $name!",
            color = NeonGreen,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- CARD SESIUNE ACTIVĂ (Păstrăm logica, dar o facem să arate bine în tema dark) ---
        if (activeSession != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, NeonGreen, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BlackAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("🟢 Live Session in progress...", color = NeonGreen, fontWeight = FontWeight.Bold)
                        Text("Tag: #${activeSession.subject}", color = White, fontSize = 14.sp)
                    }
                    Button(
                        onClick = { scope.launch { repository.endSession(activeSession) } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("End", color = White)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        // --- SECȚIUNEA 1: NEW SESSION ---
        Text(
            text = "Start a new session now!",
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        MainButton(
            text = "New Session",
            onClick = { onNavigate(Screen.SESSION_PREPARE) } // Navighează unde ai setat tu
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- SECȚIUNEA 2: PROGRAM & OBJECTIVE ---
        Text(
            text = "Program a session for later\nor set an objective!",
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecondaryButton(
                text = "Program\nSession",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Fă un Screen nou pentru programare mai târziu */ }
            )
            SecondaryButton(
                text = "Set\nObjective",
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(Screen.OBJECTIVES) } // Navighează la obiective
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- SECȚIUNEA 3: STATISTICS ---
        Text(
            text = "See your progress so far!",
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(
            text = "See Progress",
            onClick = { onNavigate(Screen.STATISTICS) }
        )
    }
}