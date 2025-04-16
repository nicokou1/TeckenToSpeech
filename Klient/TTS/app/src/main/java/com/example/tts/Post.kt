package com.example.app

// 20225-04-14
// mimoza har lagt till:
// Kotlin-objektet som JSON ska omvandlas till

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int,
    val title: String,
    val body: String
)