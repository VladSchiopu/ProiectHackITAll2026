package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize // Asigură-te că acest import există

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Definirea opțiunilor
    val webOptions = FirebaseOptions(
        apiKey = "AIzaSyCApoYy1I5OOOBUAY8WOHkJ-_nW-RDemF0",
        applicationId = "1:755749979674:web:c597bf02767acf741b73a3",
        projectId = "proiecthack2026",
        storageBucket = "proiecthack2026.firebasestorage.app",
        gcmSenderId = "755749979674"
    )

    try {
        // Încearcă această formă de apel:
        Firebase.initialize(options = webOptions)
        println("Firebase initialized successfully")
    } catch (e: Exception) {
        println("Error during initialization: ${e.message}")
    }

    ComposeViewport {
        App()
    }
}