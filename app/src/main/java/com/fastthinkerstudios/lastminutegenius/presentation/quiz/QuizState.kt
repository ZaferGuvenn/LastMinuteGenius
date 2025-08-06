package com.fastthinkerstudios.lastminutegenius.presentation.quiz

data class QuizState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val questions: List<Quiz> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val showResult: Boolean = false,
    val quizResults: List<QuizResult> = emptyList(),
    val isQuizCompleted: Boolean = false
)
