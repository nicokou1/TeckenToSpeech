package com.example.app.connection

import kotlinx.coroutines.runBlocking

/**
 * En generisk buffert som automatiskt kan hämta och lagra data från en datakälla.
 * @param T datatypen som ska sparas i bufferten (t.ex. Letter)
 * @param fetchFunction en funktion som hämtar data (t.ex. fetchLetter)
 * @author Farzaneh
 * @since 2025-04-29
 */
class Buffer<T>(
    private val fetchFunction: suspend () -> List<T> // Funktion som hämtar data, t.ex. fetchLetter
) {

    // Intern lista som håller de hämtade objekten
    private val items = mutableListOf<T>()

    // Hämtar och lagrar data när bufferten skapas
    init {
        fetchAndStore()
    }

    /**
     * Hämtar data och sparar den i bufferten.
     */
    private fun fetchAndStore() = runBlocking {
        val newItems = fetchFunction()
        items.addAll(newItems)
    }

    /**
     * Returnerar alla sparade objekt i bufferten.
     */
    fun getAll(): List<T> = items.toList()

    /**
     * Rensar bufferten.
     */
    fun clear() {
        items.clear()
    }
}
