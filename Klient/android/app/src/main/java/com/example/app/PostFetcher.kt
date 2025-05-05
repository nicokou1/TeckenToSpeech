package com.example.app

// 2025-04-14
// mimoza har lagt till:
// Testar att h?mta data fr?n ett test-API

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// suspend-funktioner anv?nds f?r tidskr?vande uppgifter, som n?tverksanslutning
// m?ste k?ras i en coroutine (l?ttare alternativ till java.Thread)
// returnerar datatypen List<Post>

suspend fun fetchPosts(): List<Post> {
    // skapar HTTP-klient med en CIO-motor
    val client =
        HttpClient(CIO) {
            // klientdriven inneh?llsf?rhandling (HTTP Header "accept: application/json")
            install(ContentNegotiation) {
                // ignorerar f?lt i JSON som inte finns som instansvariabler i Post
                json(Json { ignoreUnknownKeys = true })
            }
        }

    // klienten anropar GET och f?r ett HTTP Response-objekt som inneh?ller r?data
    // .body() omvandlar datan till List<Post>
    return client.get("http://10.2.0.95:8000/app").body()
}
