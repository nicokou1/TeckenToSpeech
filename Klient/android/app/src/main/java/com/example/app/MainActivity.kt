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
import com.example.app.composables.LetterOutput
import androidx.compose.material.ModalDrawer
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.app.connection.Letter
import com.example.app.tts.TTSManager


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
// 2025-05-06 Mimoza Behrami - Lägger till logik för onDestroy() och toggleSpeaker()

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TTSManager
    var output: LetterOutput = LetterOutput()

    //onCreate är alltid det första som körs då appen öppnas
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
            val fetchedLetter = viewModel.fetchedLetters
            val isTranslating = viewModel.isTranslating
            val historyList = viewModel.historyList

            //skapar instans av sidopanelen för historik
            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    HistoryDrawerContent(historyList = historyList, onClose = {
                        scope.launch { drawerState.close() }
                    })
                }
            ) {
                //innehållets layout
                MaterialTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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

                            // visar logik från ConnectionComposable
                            output.ShowLetterOnScreen(fetchedLetter)

                            // ställer in (Row) och visar knappar from Buttons
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClearIconButton(onClear = {
                                    viewModel.clearFetchedLetters()
                                    scope.launch { drawerState.open() }
                                })

                                BottomCenterRoundedButton(
                                    isTranslating = isTranslating,
                                    onClick = { viewModel.toggleTranslation() }
                                )

                                SpeakerIconButton(
                                    isVolumeOn = isVolumeOn,
                                    onClick = {
                                        isVolumeOn = toggleSpeaker(isVolumeOn, fetchedLetter)
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
     * <kommentar>
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }

    /**
     * <kommentar>
     * @param <kommentar>
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    private fun toggleSpeaker(isSpeakerOn: Boolean, letters: List<Letter>): Boolean {
        return if (!isSpeakerOn) {
            for (letter in letters) {
                ttsManager.speak(letter.body)
            }
            true
        } else {
            ttsManager.stop()
            false
        }
    }
}