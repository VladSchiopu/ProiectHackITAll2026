package org.example.project

import org.example.project.repository.AppRepository

expect fun openVideoSession(url: String)

expect suspend fun fetchAndStoreToken(url: String, sessionId: String, repository: AppRepository)