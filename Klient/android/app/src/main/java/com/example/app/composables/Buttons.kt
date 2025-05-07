package com.example.app.composables

import android.speech.tts.TextToSpeech
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
import com.example.app.connection.Letter

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
// 2025-05-06 Mimoza Behrami - Ändrat onClick i SpeakerIconButton och ClearIconButton


/**
 * En knapp som växlar mellan "Översätt" och "Paus" beroende på tillståndet för översättning.
 * När knappen klickas, anropas `onClick`-funktionen som skickas in som parameter.
 *
 * @param isTranslating Tillstånd som bestämmer om knappen ska visa "Paus" eller "Översätt".
 * @param onClick Funktion som anropas när knappen klickas. Här definieras vad som händer vid klick.
 * @param modifier Modifier som kan användas för att anpassa knappens utseende eller placering.
 * @author Farzaneh Ibrahimi
 */
@Composable
fun BottomCenterRoundedButton(
    isTranslating: Boolean, // Tillståndet som styr knappens text ("Paus" eller "Översätt")
    onClick: () -> Unit,    // Funktion som anropas vid knapptryck
    modifier: Modifier = Modifier // Modifier som kan användas för att anpassa knappens storlek och position
) {
    Button(
        onClick = onClick, // När knappen klickas, anropas `onClick`-funktionen
        shape = CircleShape, // Sätter knappens form till en cirkel
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF1AABE), // Bakgrundsfärg för knappen
            contentColor = Color.White // Textfärg på knappen
        ),
        modifier = modifier.size(120.dp) // Sätter knappens storlek till 120dp
    ) {
        // Innehållet i knappen, en text som växlar mellan "Översätt" och "Paus" beroende på tillståndet
        Text(
            text = if (isTranslating) "Paus" else "Översätt", // Visar "Paus" om isTranslating är true, annars "Översätt"
            fontSize = 16.sp // Sätter textens storlek till 16sp
        )
    }
}

/**
 * Clickable button with the symbol of a speaker.
 * Toggles state between sound on or sound off.
 * @author Farzaneh Ibrahimi
 * @since ?
 */
@Composable
fun SpeakerIconButton(
    isVolumeOn: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 16.dp)
            .size(80.dp)
            .background(Color(0xFFF1AABE), shape = CircleShape)
    ) {
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
 * For deleting letters on screen.
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