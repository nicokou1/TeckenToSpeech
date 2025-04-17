package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

//2025-04-14
//mimoza har lagt till följande importer:
import com.example.app.connection.*

/**
 * Main class
 * @author Mimoza Behrami & Farzaneh Ibrahim
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc

class MainActivity : ComponentActivity() {

    var connection : ConnectionComposable = ConnectionComposable()

    //onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //composables som ska visas på UI
        setContent {
            connection.ShowLetterOnScreen()
        }
    }
}