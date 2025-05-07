package com.example.app.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Manager class for handling Text-To-Speech (TTS) functionality.
 * Encapsulates initialization, speaking, stopping, and shutdown logic.
 * @author Mimoza Behrami
 * @since 2025-05-06
 */

// Changelog:

class TTSManager(private val context: Context) : TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isInitialized = false

    /**
     * Initialize the TTS engine with a given context.
     * Must be called before using speak(), stop(), or shutdown().
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun init() {
        tts = TextToSpeech(context, this)
    }

    /**
     * Callback when the TTS finish initializing.
     * Sets language to Swedish and updates initialization state.
     * @param status Initialization status passed from TTS. Status SUCCESS if initialization succeeded.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("sv", "SE"))
            isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /**
     * Speaks the given text character by character with a delay between each character.
     * @param text the text to be spoken.
     * @param pauseMillis how long the delay should be between characters, in milliseconds.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun speak(text: String, pauseMillis: Long = 500) {
        if (!isInitialized) return

        Thread {
            for (char in text) {
                tts.speak(char.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                Thread.sleep(pauseMillis)
            }
        }.start()
    }

    /**
     * Stops the ongoing speech.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun stop() {
        if (isInitialized) {
            tts.stop()
        }
    }

    /**
     * Shuts down the TTS engine and releases its resources.
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun shutdown() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}