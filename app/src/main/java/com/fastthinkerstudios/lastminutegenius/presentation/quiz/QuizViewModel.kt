package com.fastthinkerstudios.lastminutegenius.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private var currentVideoId: Int = 0

    fun loadQuiz(videoId: Int) {
        currentVideoId = videoId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val questions = quizRepository.getQuiz(videoId)
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        questions = questions,
                        currentQuestionIndex = 0,
                        quizResults = emptyList(),
                        isQuizCompleted = false,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectAnswer(answer: String) {
        _state.update { it.copy(selectedAnswer = answer) }
    }

    fun submitAnswer() {
        val currentState = _state.value
        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val isCorrect = currentState.selectedAnswer == currentQuestion.correctAnswer

        val newResult = QuizResult(
            quiz = currentQuestion,
            selectedAnswer = currentState.selectedAnswer,
            isCorrect = isCorrect
        )

        _state.update { 
            it.copy(
                showResult = true,
                quizResults = it.quizResults + newResult
            )
        }
    }

    fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.questions.size) {
            // Quiz tamamlandı
            _state.update { 
                it.copy(
                    isQuizCompleted = true,
                    showResult = false,
                    selectedAnswer = null
                )
            }
        } else {
            // Sonraki soruya geç
            _state.update { 
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null,
                    showResult = false
                )
            }
        }
    }

    fun restartQuiz() {
        _state.update { 
            it.copy(
                currentQuestionIndex = 0,
                selectedAnswer = null,
                showResult = false,
                quizResults = emptyList(),
                isQuizCompleted = false
            )
        }
    }

    fun generateNewQuiz() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Yeni quiz oluştur (cache'i bypass ederek)
                val questions = quizRepository.generateNewQuiz(currentVideoId)
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        questions = questions,
                        currentQuestionIndex = 0,
                        selectedAnswer = null,
                        showResult = false,
                        quizResults = emptyList(),
                        isQuizCompleted = false,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
