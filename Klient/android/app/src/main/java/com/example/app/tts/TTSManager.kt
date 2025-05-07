package com.example.app.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * <kommentar>
 * @author Mimoza Behrami
 * @since 2025-05-06
 */

// Changelog:

class TTSManager(private val context: Context) : TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isInitialized = false

    /**
     * <kommentar>
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun init() {
        tts = TextToSpeech(context, this)
    }

    /**
     * <kommentar>
     * @param <kommentar>
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
     * <kommentar>
     * @param <kommentar>
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
     * <kommentar>
     * @author Mimoza Behrami
     * @since 2025-05-06
     */
    fun stop() {
        if (isInitialized) {
            tts.stop()
        }
    }

    /**
     * <kommentar>
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