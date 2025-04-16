package com.example.app.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * 2025-04-16
 * Detta är en coroutine som visar de mottagna värdena från servern till GUI
 * @author Mimoza Behrami
 */

class ConnectionComposable {

    @Composable
    fun PostLetterOnScreen() {
        var fetchedLetter by remember { mutableStateOf< List<Letter> >(emptyList()) }

        // startar en coroutine som kör fetchLetter()
        LaunchedEffect(Unit) {
            fetchedLetter = fetchLetter()
        }

        // items = generisk lista som elementen placeras i
        // för varje element, skriv ut title i UI
        LazyColumn {
            items(fetchedLetter) { fetchedLetter ->
                Column {
                    Text(text = "ID: ${fetchedLetter.id}")
                    Text(text = "Bokstav: ${fetchedLetter.letter}")
                }
            }
        }
    }
}