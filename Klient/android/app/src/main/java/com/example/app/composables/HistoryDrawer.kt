package com.example.app.composables

import com.example.app.connection.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*  // importera Material3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable for the history drawer.
 * Displays a list of previously received letters and a close button for the drawer.
 * @param historyList list of letters to be put in history.
 * @param onClose lambda function triggered when the close button is pressed.
 * @author Mimoza Behrami
 * @since 2025-04-30
 */

@Composable
fun HistoryDrawerContent(historyList: List<Letter>, onClose: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(
            text = "Historik",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(historyList) { letter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Innehåll: ${letter.body}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF1AABE),
                contentColor = Color.White
            ),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.End)
                .height(48.dp)
        ) {
            Text("Stäng", fontSize = 16.sp)
        }
    }
}