package org.example.project

import kotlinx.browser.document

@JsModule("livekit-client")
@JsNonModule
external class Room() {
    fun connect(url: String, token: String): kotlin.js.Promise<Unit>
    val localParticipant: dynamic
}

actual fun connectToLiveKit(url: String, token: String) {
    val room = Room()
    room.connect(url, token).then {
        room.localParticipant.enableCameraAndMicrophone().then {
            val element = document.getElementById("local-video")
            if (element != null) {
                // Extragem publicațiile video
                val publications = room.localParticipant.videoTrackPublications

                // Transmitem elementul ca variabilă locală în blocul JS
                val attachTrack = { pub: dynamic ->
                    if (pub.track != null) {
                        pub.track.attach(element)
                    }
                }

                // Iterăm folosind logica de JS
                js("publications.forEach(attachTrack)")
            }
        }
    }
}