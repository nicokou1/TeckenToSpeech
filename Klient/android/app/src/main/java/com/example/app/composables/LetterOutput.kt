package com.example.app.composables

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
import com.example.app.connection.Letter


/**
 * This class includes functions for showing letters on the UI.
 * @author Mimoza Behrami
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-16 Mimoza Behrami - Ändrat från testkod till kod som passar för bokstäver
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc
// 2025-04-28 Mimoza Behrami - Lagt till en textruta som hämtad data skrivs ut i

class LetterOutput {

    @Composable
    fun ShowLetterOnScreen(fetchedLetter: List<Letter>) {
        // Box som centrerar allt innehåll
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // En transparent behållare som fungerar som textruta
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 93.dp)
                    .width(334.dp)
                    .height(192.dp),
                color = Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ) {

                // visa alla objekt i det inskickade argumentet
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    //datan som skrivs ut
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