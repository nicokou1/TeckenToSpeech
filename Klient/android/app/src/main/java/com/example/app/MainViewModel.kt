package com.example.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.app.connection.*

/**
 * Contains some of the business logic for the UI.
 * @author Mimoza Behrami
 * @since 2025-05-06
 */

// Changelog:
// 2025-05-06 Mimoza Behrami - flyttat innehåll från MainActivity hit, för läsbarhet och ansvarsseparation

class MainViewModel : ViewModel() {

    // tillståndsvariabler
    var fetchedLetters by mutableStateOf(emptyList<Letter>())
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
    fun toggleTranslation() {
        if (isTranslating) {
            fetchJob?.cancel()
        } else {
            fetchJob = viewModelScope.launch {
                val newLetters = fetchLetter()
                fetchedLetters = newLetters
            }
        }
        isTranslating = !isTranslating
    }


    /**
     * Logic for when clicking clear-button.
     * Data transfers to history.
     * Clears the data from screen text box.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun clearFetchedLetters() {
        historyList.addAll(fetchedLetters)
        fetchedLetters = emptyList()
    }
}