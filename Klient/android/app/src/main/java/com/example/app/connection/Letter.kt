package com.example.app.connection

// 20225-04-14
// mimoza har lagt till:
// Kotlin-objektet som JSON ska omvandlas till

import kotlinx.serialization.Serializable

@Serializable
data class Letter(
    val id: Long,
    val letter: String,
)