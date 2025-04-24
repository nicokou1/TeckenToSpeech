package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//2025-04-14
//mimoza har lagt till följande importer:
import com.example.app.connection.*

import com.example.app.buttons.*

/**
 * MainActivity is the primary entry point of the application.
 * It is responsible for initializing the UI and setting up necessary components during app startup
 * Also serves as a gateway to the business logic.
 * @author Mimoza Behrami & Farzaneh Ibrahim
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc

class MainActivity : ComponentActivity() {

    var connection: ConnectionComposable = ConnectionComposable()

    //onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //composables som ska visas på UI
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Visa logiken från ConnectionComposable
                        connection.ShowLetterOnScreen()

                        // Visa knapparna längst ner
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ClearIconButton()
                            BottomCenterRoundedButton()
                            SpeakerIconButton()
                        }
                    }
                }
            }
        }
    }
}