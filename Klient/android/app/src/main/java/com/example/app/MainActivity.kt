package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

//2025-04-14
//mimoza har lagt till följande importer:
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*

// NEJ NEJ

class MainActivity : ComponentActivity() {

    //onCreate är alltid det första som körs då appen öppnas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //composables som ska visas på UI
        setContent {
            PostListScreen()
        }

    }
}

@Composable
fun PostListScreen() {
    var posts by remember { mutableStateOf< List<Post> >(emptyList()) }

    // startar en coroutine som fetchar de första 5 elementen
    LaunchedEffect(Unit) {
        posts = fetchPosts().take(5)
    }

    // items = generisk lista som elementen placeras i
    // för varje element, skriv ut title i UI
    LazyColumn {
        items(posts) { post ->
            Column {
                Text(text = "ID: ${post.id}")
                Text(text = "Bokstav: ${post.letter}")
            }
        }
    }
}



/*
@Composable
public fun TestColumn() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Hej1",
            color = Color.Magenta,
            fontSize = 50.sp
        )
        Text(
            "Hej2",
            color = Color.Red,
            fontSize = 30.sp
        )
    }
}

@Composable
public fun TestRow () {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Hej3",
            color = Color.Green,
            fontSize = 43.sp
        )
        Text("Hej4",
            color = Color.Blue,
            fontSize = 32.sp
        )
    }
}

@Composable
@Preview
public fun TestButton () {
    Column (

    ){
        //variable for counting button clicks
        var counter = remember {
            mutableStateOf(0)
        }

        Button(
            {counter.value += 1} ) {
            Text(
                "This is a test button. " +
                        "Clicked ${counter.value} times"
            )
        }
    }
}
*/