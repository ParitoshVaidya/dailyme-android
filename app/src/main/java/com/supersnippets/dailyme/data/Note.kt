package com.supersnippets.dailyme.data

import java.io.Serializable

data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
