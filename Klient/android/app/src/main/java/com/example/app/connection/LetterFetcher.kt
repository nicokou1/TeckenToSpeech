package com.example.app.connection

// 2025-04-14
// mimoza har lagt till:
// Testar att hämta data från ett test-API

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*

// suspend-funktioner används för tidskrävande uppgifter, som nätverksanslutning
// måste köras i en coroutine (lättare alternativ till java.Thread)
// returnerar datatypen Letter

suspend fun fetchLetter(): List<Letter> {

    //skapar HTTP-klient med en CIO-motor
    val client = HttpClient(CIO) {

        //klientdriven innehållsförhandling (HTTP Header "accept: application/json")
        install(ContentNegotiation) {

            // ignorerar fält i JSON som inte finns som instansvariabler i Letter-klassen            json(Json { ignoreUnknownKeys = true })
        }
    }

    // klienten anropar GET och får ett HTTP Response-objekt som innehåller rådata
    // .body() omvandlar datan till en bokstav
    return client.get("http://10.2.0.95:8000/app").body()
}