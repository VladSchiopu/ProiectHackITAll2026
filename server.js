const express = require('express');
const cors = require('cors');
const { AccessToken } = require('livekit-server-sdk');
require('dotenv').config();

const app = express();

// Important pentru Hackathon: Permite browserului să acceseze serverul local
app.use(cors());
app.use(express.json());

// Datele tale de configurare LiveKit
const apiKey = "APIA8weksJBJueo";
const apiSecret = "QsTpFkL2DkTQkq5oXU5Ljj6CHvGykeNXBVa6JCMoKGX";
const serverUrl = "wss://sheep-064e38km.livekit.cloud";

app.get('/get-token', async (req, res) => {
    // Luăm numele camerei și al utilizatorului din URL sau punem valori default
    const roomName = req.query.room || 'match-room-1';
    const participantName = req.query.user || 'User-' + Math.floor(Math.random() * 100);

    try {
        // 1. Creăm Token-ul (Biletul de acces)
        const at = new AccessToken(apiKey, apiSecret, {
            identity: participantName,
        });

        // 2. Setăm permisiunile (Grants)
        at.addGrant({ 
            roomJoin: true, 
            room: roomName, 
            canPublish: true, 
            canSubscribe: true 
        });

        // 3. Generăm șirul JWT și îl trimitem înapoi
        const token = await at.toJwt();
        
        console.log(`✅ Token generat pentru ${participantName} în camera ${roomName}`);
        
        res.send({
            token: token,
            serverUrl: serverUrl
        });
    } catch (error) {
        console.error("❌ Eroare la generare token:", error);
        res.status(500).send({ error: "Nu s-a putut genera token-ul" });
    }
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`🚀 Serverul de Token-uri rulează la http://localhost:${PORT}`);
    console.log(`👉 Testează în browser: http://localhost:3000/get-token?user=Mara`);
});
