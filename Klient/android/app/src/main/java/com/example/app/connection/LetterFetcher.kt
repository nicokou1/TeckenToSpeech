package com.example.app.connection

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Client that sends GET-requests to server
 * @author Mimoza Behrami
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Ändrat URL-adress från testAPI till server
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc


//-------------------------------------------------------------------------------
// suspend-funktioner används för tidskrävande uppgifter, som nätverksanslutning.
// måste köras i en coroutine (lättare alternativ till java.Thread).
// returnerar en lista av datatypen Letter.
//-------------------------------------------------------------------------------
suspend fun fetchLetter(): List<Letter> {

    //skapar HTTP-klient med en CIO-motor
    val client = HttpClient(CIO) {

        //klientdriven innehållsförhandling (HTTP Header "accept: application/json")
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) //ignorera eventuella okända fält, förhindrar krasch
        }
    }

    // klienten anropar GET och får ett HTTP Response-objekt som innehåller rådata.
    // .body() omvandlar datan till en bokstav.
    return client.get("http://10.2.0.95:8000/app").body()
}