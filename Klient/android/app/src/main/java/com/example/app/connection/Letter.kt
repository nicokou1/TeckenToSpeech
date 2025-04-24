package com.example.app.connection

import kotlinx.serialization.Serializable

/**
 * Data class representing a letter object, to be used when deserializing from JSON.
 * @author Mimoza Behrami
 * @since 2025-04-14
 */

// Changelog:
// 2025-04-16 Mimoza Behrami - Ändrat från testkod till kod som passar för bokstäver
// 2025-04-17 Mimoza Behrami - Lagt till JavaDoc

@Serializable
data class Letter(
    val id: Int,
    val title: String,
    val body: String
)
