package com.example.app.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * This class contains all buttons to be shown and used on UI.
 * @author Farzaneh Ibrahimi & Mimoza Behrami
 * @since ?
 */

// Changelog:
// 2025-04-24 Mimoza Behrami - Tagit bort redundant mainActivity-metod. Anropar knapparna från MainAcitivity-klassen istället.
// 2025-04-24 Mimoza Behrami - Lagt till JavaDoc.
// 2025-04-25 Mimoza Behrami - Lagt till tillståndsväxling för BottomCenterRoundedButton och SpeakerIconButton.
// 2025-04-25 Mimoza Behrami & Farzaneh Ibrahimi - Ändrat färger på knappar för att passa till bakgrundsbilden.

/**
 * Clickable button that plays or pauses the printing of letters.
 * @author Farzaneh Ibrahimi
 * @since ?
 */
@Composable
fun BottomCenterRoundedButton(modifier: Modifier = Modifier) {
    var isTranslating by remember { mutableStateOf(false) }
    Button(
        onClick = { isTranslating = !isTranslating },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF1AABE),
            contentColor = Color.White
        ),
        modifier = modifier.size(120.dp)
    ) {
        Text(
            text = if (isTranslating) "Paus" else "Översätt",
            fontSize = 16.sp
        )
    }
}

/**
 * Clickable button with the symbol of a speaker.
 * Starts the TTS-translation of letters.
 * @author Farzaneh Ibrahimi
 * @since ?
 */
@Composable
fun SpeakerIconButton() {
    var isVolumeOn by remember { mutableStateOf(true) }

    IconButton(
        onClick = { isVolumeOn = !isVolumeOn }, //växla tillstånd för varje klick
        modifier = Modifier
            .padding(start = 16.dp)
            .size(80.dp)
            .background(Color(0xFFF1AABE), shape = CircleShape)
    ) {
        @Suppress("DEPRECATION")
        val icon = if (isVolumeOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff
        Icon(
            imageVector = icon,
            contentDescription = if (isVolumeOn) "Ljud på" else "Ljud av",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Clickable button with the symbol of a trash can.
 * Deletes the received letters.
 * @author Farzaneh Ibrahimi
 * @since ?
 */
@Composable
fun ClearIconButton(onClear : () -> Unit) {
    IconButton(
        onClick = { onClear() },
        modifier = Modifier
            .padding(start = 10.dp)
            .offset(x = (-16).dp)
            .size(80.dp)
            .background(Color(0xFFF1AABE), shape = CircleShape)
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Radera",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}