package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app.composables.*
import com.example.app.connection.Buffer
import com.example.app.composables.LetterOutput
import com.example.app.connection.Letter
import com.example.app.connection.fetchLetter
import androidx.compose.material.ModalDrawer
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job // 🆕 Import för Job, så vi kan hantera nätverksanrop

/**
 * MainActivity is the primary entry point of the application.
 * It is responsible for initializing the UI and setting up necessary components during app startup.
 * Also serves as a gateway to the business logic.
 * @author Mimoza Behrami & Farzaneh Ibrahimi
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc
// 2025-04-24 Mimoza Behrami - Lagt till knapparna från Buttons i onCreate()
// 2025-04-28 Farzaneh Ibrahimi - Lagt till bakgrundsbild
// 2025-04-30 Mimoza Behrami - Lagt till en drawer (sidopanel) att spara historiken i

class MainActivity : ComponentActivity() {

    var output: LetterOutput = LetterOutput()

    //onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // innehållet i UI
        setContent {

            var letterBuffer: Buffer<Letter>? = null
            var fetchedLetter by remember { mutableStateOf(emptyList<Letter>()) } // 🆕 Starta tomt
            val historyList = remember { mutableStateListOf<Letter>() }
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var isTranslating by remember { mutableStateOf(false) }


            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    HistoryDrawerContent(historyList = historyList, onClose = {
                        scope.launch { drawerState.close() }
                    })
                }
            ) {
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
                            output.ShowLetterOnScreen(fetchedLetter)

                            // visar knapparna
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClearIconButton(onClear = {
                                    historyList.addAll(fetchedLetter)
                                    fetchedLetter = emptyList()
                                    scope.launch { drawerState.open() }
                                })


                                /*
                                Job: När vi gör nätverksanropet (med fetchLetter())
                                sparar vi det i en variabel (fetchJob). Då kan vi senare
                                avbryta detta nätverksanrop om användaren trycker på "Paus".

                                Avbryt anropet: När användaren trycker på "Paus",
                                 avbryts eventuellt pågående nätverksanrop genom fetchJob?.cancel().
                                 */

                                // Vi kan skapa en Job för att hålla koll på vårt pågående nätverksanrop
                                var fetchJob: Job? = null

                                BottomCenterRoundedButton(
                                    isTranslating = isTranslating,
                                    onClick = {
                                        if (isTranslating) {
                                            //  Om knappen visar "Paus" så kan vi stoppa den pågående hämtningsprocessen
                                            fetchJob?.cancel()  // Avbryt pågående jobb om vi pausat
                                        } else {
                                            // skapa nuffern här
                                            if (letterBuffer == null) {
                                                letterBuffer = Buffer { fetchLetter() }
                                            }
                                            //  Om knappen visar "Översätt", hämta bokstäver från servern
                                            fetchJob = scope.launch {
                                                val newLetters = fetchLetter()  // Hämtar bokstäver
                                                fetchedLetter = newLetters  // Uppdaterar skärmen med nya bokstäver
                                            }
                                        }
                                        isTranslating = !isTranslating  // Växla mellan "Översätt" och "Paus"
                                    }
                                )

                                SpeakerIconButton()
                            }
                        }
                    }
                }
            }
        }
    }
}