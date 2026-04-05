package org.example.project

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.example.project.repository.AppRepository // Importă repository-ul tău

// 1. Implementarea pentru deschiderea tab-ului nou
actual fun openVideoSession(url: String) {
    //window.location.href = url
    kotlinx.browser.window.open(url, "_blank")
}

// 2. Implementarea pentru cererea HTTP către serverul tău Node.js
actual suspend fun fetchAndStoreToken(url: String, sessionId: String, repository: AppRepository) {
    try {
        // Folosim fetch-ul nativ din browser (window.fetch)
        val response = window.fetch(url).await()

        if (!response.ok) {
            println("Eroare server Node: ${response.statusText}")
            return
        }

        val data = response.json().await().asDynamic()
        val token = data.token.toString()

        // Scriem token-ul primit înapoi în Firebase folosind funcția creată anterior
        repository.updateSessionToken(sessionId, token)

        println("✅ Token stocat cu succes pentru sesiunea: $sessionId")
    } catch (e: Exception) {
        println("❌ Eroare la fetch token din JS: ${e.message}")
    }
}

// 3. Definițiile externe pentru LiveKit (păstrate pentru referință sau uz viitor)
@JsModule("livekit-client")
@JsNonModule
external class Room() {
    fun connect(url: String, token: String): kotlin.js.Promise<Unit>
    val localParticipant: dynamic
}