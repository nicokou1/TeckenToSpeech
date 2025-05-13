package com.example.app

import android.util.Log
import com.example.app.connection.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.isActive

/**
 * ViewModel (controller) containing business logic for the UI.
 * Translation toggling, data fetching, and history management.
 * Separates logic from MainActivity to improve readability and maintainability.
 * @author Mimoza Behrami
 * @since 2025-05-06
 */

// Changelog:
// 2025-05-06 Mimoza Behrami - flyttat innehåll från MainActivity hit, för läsbarhet och ansvarsseparation.
// 2025-05-09 Mimoza Behrami - uppdaterat toggleTranslation() för att kontinuerligt hämta nya bokstäver.
// 2025-05-13 Mimoza Behrami - lagt till felhantering och återanslutningslogik för anslutningsproblem i toggleTranslation().

class MainViewModel : ViewModel() {

    // tillståndsvariabler
    var fetchedLetter by mutableStateOf<Letter?>(null)
        private set

    var isTranslating by mutableStateOf(false)
        private set

    var historyList = mutableStateListOf<Letter>()
        private set

    /*
    Job: När vi gör nätverksanropet (med fetchLetter())
    sparar vi det i en variabel (fetchJob). Då kan vi senare
    avbryta detta nätverksanrop om användaren trycker på "Paus".

    Avbryt anropet: När användaren trycker på "Paus",
    avbryts eventuellt pågående nätverksanrop genom fetchJob?.cancel().
    */
    // Vi kan skapa en Job för att hålla koll på vårt pågående nätverksanrop
    private var fetchJob: Job? = null

    /**
     * <kommentar>
     * @author Farzaneh Ibrahimi
     * @since 2025-05-06
     */
    fun toggleTranslation(snackbarCallback: suspend (String) -> Unit) {
        if (isTranslating) {
            fetchJob?.cancel()
            viewModelScope.launch {
                snackbarCallback("Anslutningen avbröts.")
            }
        } else {
            fetchJob = viewModelScope.launch {
                while (isActive) {
                    var success = false
                    var attempt = 0
                    val maxAttempts = 3

                    while (attempt < maxAttempts && !success && isActive) {
                        val timeoutMs = (attempt + 1) * 2000L // exponentiell backoff

                        try {
                            withTimeout(timeoutMs) {
                                val letter = fetchLetter()
                                if (letter.body.isNotBlank()) {
                                    val combined = (fetchedLetter?.body ?: "") + letter.body
                                    fetchedLetter = Letter(combined)
                                }
                                success = true
                            }
                        } catch (e: Exception) {
                            attempt++
                            snackbarCallback("Misslyckad anslutning (försök $attempt). Försöker igen...")
                            delay(500)
                        }
                    }

                    if (!success) {
                        snackbarCallback("Det gick inte att ansluta. Försök igen senare.")
                        isTranslating = false
                        return@launch
                    }
                    delay(1000)
                }
            }
        }
        isTranslating = !isTranslating
    }

    /**
     * Called when clicking the clear button.
     * Data transfers to history panel.
     * Clears the data from text field on screen.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun clearFetchedLetters() {
        fetchedLetter?.let { historyList.add(it) }
        fetchedLetter = null
    }
}