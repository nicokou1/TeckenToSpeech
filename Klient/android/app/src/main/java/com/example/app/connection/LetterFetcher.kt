package com.example.app.connection

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * This class creates a HTTP client that sends GET-requests to a server.
 * @author Mimoza Behrami
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Ändrat adress från testAPI till server och testat anslutningen
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc

/**
 * Suspend functions are used for time consuming operations, such as network communication.
 * It must be called from within a coroutine, which is a lightweight alternative to a thread (java.Thread).
 * .body() is used for deserializing the server response and converting to the return type.
 * @return a list containing Letter-objects
 * @author Mimoza Behrami
 * @since 2024-04-14
 */
suspend fun fetchLetter(): List<Letter> {

    //skapar HTTP-klient, med CIO-motor som hanterar nätverkstrafiken (se build.gradle)
    val client = HttpClient(CIO) {

        // klientdriven innehållsförhandling
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) //ignorera eventuella okända fält, förhindrar krasch
        }
    }
    return client.get("http://10.2.4.158:8000/app").body()
}