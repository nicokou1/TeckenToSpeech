package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app.buttons.*
import com.example.app.connection.LetterOutput
import com.example.app.connection.Letter
import com.example.app.connection.fetchLetter

/**
 * MainActivity is the primary entry point of the application.
 * It is responsible for initializing the UI and setting up necessary components during app startup
 * Also serves as a gateway to the business logic.
 * @author Mimoza Behrami & Farzaneh Ibrahimi
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc
// 2025-04-24 Mimoza Behrami - Lagt till knapparna från Buttons i onCreate()
// 2025-04-28 Farzaneh Ibrahimi - Lagt till bakgrundsbild

class MainActivity : ComponentActivity() {

    var connection: LetterOutput = LetterOutput()

    //onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // innehållet i UI
        setContent {

            var fetchedLetter by remember {
                mutableStateOf<List<Letter>>(emptyList())
            }

            // startar en coroutine som hämtar datan
            LaunchedEffect(Unit) {
                fetchedLetter = fetchLetter()
            }

            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // visar logiken från ConnectionComposable
                        connection.ShowLetterOnScreen(fetchedLetter)

                        // visar knapparna
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ClearIconButton(onClear = { fetchedLetter = emptyList() })
                            BottomCenterRoundedButton()
                            SpeakerIconButton()
                        }
                    }
                }
            }
        }
    }
}