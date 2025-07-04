package com.example.mathq.models

// This data class defines the structure for a single question in your game.
data class Question(
    val question: String,
    val options: List<String>,
    val answer: String
)
