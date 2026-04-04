package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.example.project.repository.AppRepository
import org.jetbrains.compose.resources.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Starea originală
        var showContent by remember { mutableStateOf(false) }

        // Stările noi pentru Firebase
        val repository = remember { AppRepository() }
        val coroutineScope = rememberCoroutineScope()
        var firebaseStatus by remember { mutableStateOf("Stare Firebase: Așteaptă...") }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Butonul tău original
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }

            // Secțiunea originală cu imaginea și textul
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- NOU: Secțiunea de test Firebase ---
            Text(firebaseStatus)

            Button(onClick = {
                firebaseStatus = "Trimit date către Firebase..."
                coroutineScope.launch {
                    try {
                        repository.testFirebaseConnection()
                        firebaseStatus = "SUCCES! Obiectiv salvat în DB."
                    } catch (e: Exception) {
                        firebaseStatus = "EROARE: ${e.message}"
                    }
                }
            }) {
                Text("Testează Firebase")
            }
        }
    }
}