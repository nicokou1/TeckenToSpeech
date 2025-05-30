package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.app.composables.*
import androidx.compose.material.ModalDrawer
import androidx.compose.material.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.app.tts.TTSManager
import androidx.compose.runtime.LaunchedEffect

/**
 * MainActivity is the primary entry point of the application.
 * It is responsible for initializing the UI and setting up necessary components during app startup.
 * Serves as a gateway to the business logic.
 * Contains lifecycle functions for TTS.
 * @author Mimoza Behrami & Farzaneh Ibrahimi
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc
// 2025-04-24 Mimoza Behrami - Lagt till knapparna från Buttons i onCreate()
// 2025-04-30 Mimoza Behrami - Lagt till en sidopanel att spara historiken i
// 2025-05-06 Mimoza Behrami - Flyttat allt som inte är grafik ("view") till MainViewModel.
// 2025-05-06 Mimoza Behrami - Instansierar TTSManager samt lagt till funktioner som ttsManager använder.
// 2025-05-09 Mimoza Behrami - Lagt till "hamburgerknapp" och "snackbar" för historikpanelen.
// 2025-05-12 Mimoza Behrami - Lagt till LaunchedEffect i setContent som ttsManager använder.
// 2025-05-13 Mimoza Behrami - Ändrat onClick i BottomCenterButton för att hantera felmeddelanden.

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TTSManager

    // onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ttsManager = TTSManager(this)
        ttsManager.init()

        // innehållet i UI
        setContent {

            val viewModel: MainViewModel = viewModel()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var isVolumeOn by remember { mutableStateOf(false) }
            val fetchedLetter = viewModel.fetchedLetter
            val isTranslating = viewModel.isTranslating
            val historyList = viewModel.historyList
            val snackbarHostState = remember { SnackbarHostState() }
            var lastSpokenLength by remember { mutableStateOf(0) }

            // följer med i bokstavsflödet, undviker upprepning av föregående bokstäver
            LaunchedEffect(fetchedLetter, isVolumeOn) {
                if (isVolumeOn && fetchedLetter != null && fetchedLetter.body.length > lastSpokenLength) {
                    val newText = fetchedLetter.body.substring(lastSpokenLength)
                    ttsManager.speak(newText)
                    lastSpokenLength = fetchedLetter.body.length
                }
            }

            // skapar instans av sidopanelen för historik
            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    HistoryDrawerContent(historyList = historyList, onClose = {
                        scope.launch { drawerState.close() }
                    })
                }
            ) {
                // innehållets layout
                MaterialTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {

                            // ställer in bakgrundsbild
                            Image(
                                painter = painterResource(id = R.drawable.img),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            HistoryButton {
                                scope.launch { drawerState.open() }
                            }

                            // visar den hämtade bokstaven i textrutan
                            ShowLetterOnScreen(fetchedLetter)

                            // ställer in och visar de tre nedersta knapparna
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                ClearIconButton(onClear = {
                                    if (fetchedLetter != null) {
                                        viewModel.clearFetchedLetters()
                                        isVolumeOn = false
                                        lastSpokenLength = 0
                                        scope.launch { drawerState.open() }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Det finns inget att radera.")
                                        }
                                    }
                                })

                                BottomCenterButton(
                                    isTranslating = isTranslating,
                                    onClick = {
                                        viewModel.toggleTranslation { message ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(message)
                                            }
                                        }
                                    }
                                )

                                SpeakerIconButton(
                                    isVolumeOn = isVolumeOn,
                                    onClick = {
                                        if (!isVolumeOn && fetchedLetter != null) {
                                            lastSpokenLength = 0
                                            isVolumeOn = true
                                        } else {
                                            isVolumeOn = false
                                            ttsManager.stop()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the activity is being destroyed by the system.
     * Shuts down the TTS engine to release resources.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }
}