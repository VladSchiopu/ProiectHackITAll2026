package org.example.project

@JsModule("livekit-client")
@JsNonModule
external object LiveKit {
    // Aici definim funcția de conectare din SDK-ul JS
    fun connect(url: String, token: String): dynamic
}