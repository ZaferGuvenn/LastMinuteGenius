package com.fastthinkerstudios.lastminutegenius.domain.repository

import com.fastthinkerstudios.lastminutegenius.presentation.quiz.Quiz

interface QuizRepository {
    suspend fun getQuiz(videoId: Int): List<Quiz>
    suspend fun generateNewQuiz(videoId: Int): List<Quiz>
}