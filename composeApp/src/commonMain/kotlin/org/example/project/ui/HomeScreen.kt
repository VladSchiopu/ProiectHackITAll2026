import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.project.theme.purple
import org.example.project.theme.green
import org.example.project.theme.white
import org.example.project.theme.black

import org.example.project.ui.components.MainButton
import org.example.project.ui.components.SecondaryButton
import org.example.project.ui.components.AccentButton


@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = purple
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- HEADER / LOGO ---
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Placeholder pentru Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(green, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "FOCUS FLOW", // Înlocuiește cu numele aplicației tale
                    color = white,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // --- SECȚIUNEA 1: NEW SESSION ---
            Text(
                text = "Try starting a new session now!",
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            MainButton(
                text = "New Session",
                onClick = { /* TODO: Navighează spre Matchmaking */ }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- SECȚIUNEA 2: PROGRAM & OBJECTIVE ---
            Text(
                text = "Or program a session for later\nor set an objective!",
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Row pentru butoanele side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryButton(
                    text = "Program\nSession",
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO */ }
                )
                SecondaryButton(
                    text = "Set\nObjective",
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- SECȚIUNEA 3: STATISTICS ---
            Text(
                text = "Or see your progress so far!",
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            AccentButton(
                text = "See Statistics",
                onClick = { /* TODO: Navighează spre Stats */ }
            )
        }
    }
}