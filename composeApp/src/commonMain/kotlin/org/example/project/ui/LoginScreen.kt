package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.* // Aduce DeepPurple, NeonGreen, White, BlackAccent

@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple) // Fundalul principal mov
            .padding(24.dp)
    ) {
        // --- TITLUL ---
        Text(
            text = "StudyMirror",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = White
        )

        // --- SUBTITLUL VERDE ---
        Text(
            text = "Enter your nickname to start focusing",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = NeonGreen
        )

        Spacer(Modifier.height(48.dp))

        // --- TEXT BOX-UL ALB ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nickname", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White, // Fundal alb
                unfocusedContainerColor = White,
                focusedBorderColor = NeonGreen, // Margine verde când scrii
                unfocusedBorderColor = Color.Transparent, // Fără margine când nu scrii
                focusedTextColor = BlackAccent, // Scris negru să se vadă pe alb
                unfocusedTextColor = BlackAccent,
                cursorColor = NeonGreen // Cursorul care clipește e verde!
            )
        )

        Spacer(Modifier.height(24.dp))

        // --- BUTONUL DE START ---
        Button(
            onClick = { if (name.isNotBlank()) onLogin(name) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen, // Buton verde
                contentColor = BlackAccent  // Text negru
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Start Focusing", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}