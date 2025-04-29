package com.example.app.connection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * This class includes functions for showing letters on the UI.
 * Includes a coroutine for running suspended functions in the background.
 * @author Mimoza Behrami
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-16 Mimoza Behrami - Ändrat från testkod till kod som passar för bokstäver
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc
// 2025-04-28 Mimoza Behrami - Lagt till en textruta som hämtad data skrivs ut i

class ConnectionComposable {

    @Composable
    fun ShowLetterOnScreen() {
        var fetchedLetter by remember {
            mutableStateOf<List<Letter>>(emptyList())
        }

        // startar en coroutine som hämtar datan
        LaunchedEffect(Unit) {
            fetchedLetter = fetchLetter()
        }

        // box som centrerar allt innehåll
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // en transparent behållare som fungerar som textruta
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 93.dp)
                    .width(334.dp)
                    .height(192.dp),
                color = Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(fetchedLetter) { letter ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(text = "ID: ${letter.id}")
                            Text(text = "Titel: ${letter.title}")
                            Text(text = "Innehåll: ${letter.body}")
                        }
                    }
                }
            }
        }
    }
}