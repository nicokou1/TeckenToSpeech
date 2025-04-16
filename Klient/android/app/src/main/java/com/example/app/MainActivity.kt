package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

//2025-04-14
//mimoza har lagt till följande importer:
import com.example.app.connection.*

class MainActivity : ComponentActivity() {

    var connection : ConnectionComposable = ConnectionComposable()

    //onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //composables som ska visas på UI
        setContent {
            connection.PostListScreen()
        }
    }
}