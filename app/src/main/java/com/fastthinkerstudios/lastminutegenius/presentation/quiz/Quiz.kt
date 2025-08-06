package com.fastthinkerstudios.lastminutegenius.presentation.quiz

data class Quiz(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

data class QuizResult(
    val quiz: Quiz,
    val selectedAnswer: String?,
    val isCorrect: Boolean
)