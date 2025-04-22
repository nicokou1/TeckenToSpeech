package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

//2025-04-14
//mimoza har lagt till följande importer:
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.example.app.anslutning.Post
import com.example.app.anslutning.fetchPosts


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