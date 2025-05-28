package com.example.app.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.connection.Letter


/**
 * Composable that displays the fetched letter in a transparent text box in UI.
 * @param fetchedLetter The letter to be displayed. If null, nothing is shown.
 * @author Mimoza Behrami
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-16 Mimoza Behrami - Ändrat från testkod till kod som passar för bokstäver
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc
// 2025-04-28 Mimoza Behrami - Lagt till en textruta som hämtad data skrivs ut i
// 2025-05-09 Mimoza Behrami - Lagt till skrollningsfunktion, ifall textrutan blir överfylld så blir den skrollbar

@Composable
fun ShowLetterOnScreen(fetchedLetter: Letter?) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // en transparent container som fungerar som textruta
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
                .width(334.dp)
                .height(192.dp),
            color = Color.Transparent,
            shape = MaterialTheme.shapes.medium
        ) {
            fetchedLetter?.let { letter ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = letter.body,
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
}