package com.example.app.buttons

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.filled.Delete

class Buttons : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ClearIconButton()
                            BottomCenterRoundedButton()
                            SpeakerIconButton()
                        }
                    }
                }
            }
        }
    }
}

    @Composable
    fun BottomCenterRoundedButton(modifier: Modifier = Modifier) {
        Button(
            onClick = { /* Lägg in din knapplogik här */ },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF80B7),
                contentColor = Color.White
            ),
            modifier = modifier.size(120.dp)
        ) {
            Text(
                text = "Översätt",
                fontSize = 16.sp
            )
        }
    }

    @Composable
    fun SpeakerIconButton() {
        IconButton(
            onClick = { /* ljud logiken */ },
            modifier = Modifier
                .padding(start = 16.dp)
                .size(80.dp)
                .background(Color(0xFFFF80B7), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.VolumeUp,
                contentDescription = "Högtalare",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

        }
    }

    @Composable
    fun ClearIconButton() {
        IconButton(
            onClick = { /* rensa logiken */ },
            modifier = Modifier
                .padding(start = 10.dp)
                .offset(x = (-16).dp)
                .size(80.dp)
                .background(Color(0xFFFF80B7), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "papperskorge",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BottomCenterRoundedButtonPreview() {
        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ClearIconButton()
                    BottomCenterRoundedButton()
                    SpeakerIconButton()

                }
            }
        }
    }

}