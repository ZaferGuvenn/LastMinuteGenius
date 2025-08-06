package com.fastthinkerstudios.lastminutegenius.presentation.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    videoId: Int,
    navController: NavHostController,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(videoId) {
        viewModel.loadQuiz(videoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (state.isQuizCompleted) "Quiz Sonuçları" else "Quiz",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
    when {
        state.isLoading -> {
                    LoadingScreen()
        }

        state.error != null -> {
                    ErrorScreen(
                        error = state.error!!,
                        onRetry = { viewModel.loadQuiz(videoId) }
                    )
                }

                state.isQuizCompleted -> {
                    QuizResultsScreen(
                        results = state.quizResults,
                        onRestartQuiz = { viewModel.restartQuiz() },
                        onBackToVideo = { navController.popBackStack() },
                        onGenerateNewQuiz = { viewModel.generateNewQuiz() }
                    )
        }

        state.questions.isNotEmpty() -> {
                    QuizQuestionScreen(
                        state = state,
                        onAnswerSelected = { viewModel.selectAnswer(it) },
                        onSubmitAnswer = { viewModel.submitAnswer() },
                        onNextQuestion = { viewModel.nextQuestion() }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 6.dp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "🧠 Quiz Hazırlanıyor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Yapay zeka sizin için özel sorular oluşturuyor...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "😔 Quiz Yüklenemedi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                FilledTonalButton(
                    onClick = onRetry,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tekrar Dene", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun QuizQuestionScreen(
    state: QuizState,
    onAnswerSelected: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    val currentQuestion = state.questions[state.currentQuestionIndex]
    val totalQuestions = state.questions.size
    val currentQuestionNumber = state.currentQuestionIndex + 1
    val scrollState = rememberScrollState()

    // SCROLLABLE COLUMN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Progress section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Soru $currentQuestionNumber",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$totalQuestions sorudan $currentQuestionNumber. soru",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    
                    // Circular progress
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                        progress = { currentQuestionNumber.toFloat() / totalQuestions.toFloat() },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp,
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                        )
                        Text(
                            text = "$currentQuestionNumber/$totalQuestions",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                progress = { currentQuestionNumber.toFloat() / totalQuestions.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Quiz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Soru",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options
        Text(
            text = "Seçenekler:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        currentQuestion.options.forEachIndexed { index, option ->
            OptionCard(
                text = option,
                optionNumber = ('A' + index).toString(),
                isSelected = state.selectedAnswer == option,
                isCorrect = if (state.showResult) option == currentQuestion.correctAnswer else null,
                isUserAnswer = if (state.showResult) option == state.selectedAnswer else false,
                onClick = { 
                    if (!state.showResult) {
                        onAnswerSelected(option)
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Result feedback
        if (state.showResult) {
            Spacer(modifier = Modifier.height(16.dp)) // Daha fazla üst boşluk
            ResultFeedbackCard(
                isCorrect = state.selectedAnswer == currentQuestion.correctAnswer,
                correctAnswer = currentQuestion.correctAnswer,
                userAnswer = state.selectedAnswer
            )
            Spacer(modifier = Modifier.height(8.dp)) // Alt boşluk da ekleyelim
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action button
        Button(
            onClick = if (state.showResult) onNextQuestion else onSubmitAnswer,
            enabled = if (state.showResult) true else state.selectedAnswer != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = when {
                    !state.showResult -> "✓ Cevabı Onayla"
                    state.currentQuestionIndex < state.questions.size - 1 -> "Sonraki Soru →"
                    else -> "🎯 Sonuçları Gör"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Bottom padding for scrolling
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun OptionCard(
    text: String,
    optionNumber: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isUserAnswer: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect == true -> Color(0xFF2E7D32).copy(alpha = 0.1f)
        isCorrect == false && isUserAnswer -> Color(0xFFD32F2F).copy(alpha = 0.1f)
        isSelected && isCorrect == null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isCorrect == true -> Color(0xFF2E7D32)
        isCorrect == false && isUserAnswer -> Color(0xFFD32F2F)
        isSelected && isCorrect == null -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

                    Card(
                        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected || isCorrect != null) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = if (isSelected) CardDefaults.cardElevation(defaultElevation = 3.dp) 
                   else CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option letter
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = borderColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = borderColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                lineHeight = 24.sp
            )

            // Result icon
            if (isCorrect != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Quiz,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ResultFeedbackCard(
    isCorrect: Boolean,
    correctAnswer: String,
    userAnswer: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) 
                Color(0xFF2E7D32).copy(alpha = 0.1f) else Color(0xFFD32F2F).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp), // Daha fazla padding
            verticalArrangement = Arrangement.Center, // Dikey ortalama
            horizontalAlignment = Alignment.CenterHorizontally // Yatay ortalama
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isCorrect) "🎉" else "😔",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isCorrect) "Harika! Doğru cevap!" else "Üzülme! Yanlış cevap",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            }
            
            if (!isCorrect) {
                Spacer(modifier = Modifier.height(16.dp)) // Daha fazla spacing
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❌ Senin cevabın: ${userAnswer ?: "Cevaplanmadı"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Doğru cevap: $correctAnswer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun QuizResultsScreen(
    results: List<QuizResult>,
    onRestartQuiz: () -> Unit,
    onBackToVideo: () -> Unit,
    onGenerateNewQuiz: () -> Unit
) {
    val correctAnswers = results.count { it.isCorrect }
    val totalQuestions = results.size
    val scorePercentage = (correctAnswers.toFloat() / totalQuestions.toFloat() * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Score card - TAM ORTADA VE MODERN
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        scorePercentage >= 80 -> Color(0xFF2E7D32).copy(alpha = 0.1f)
                        scorePercentage >= 60 -> Color(0xFFEF6C00).copy(alpha = 0.1f)
                        else -> Color(0xFFD32F2F).copy(alpha = 0.1f)
                    }
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Emoji ve başlık
                    Text(
                        text = when {
                            scorePercentage >= 90 -> "🏆"
                            scorePercentage >= 80 -> "🎉"
                            scorePercentage >= 60 -> "👏"
                            else -> "💪"
                        },
                        style = MaterialTheme.typography.displayLarge
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Quiz Tamamlandı!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Score display - BÜYÜK VE GÖSTERIŞLI
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$correctAnswers",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                            fontWeight = FontWeight.Black,
                            color = when {
                                scorePercentage >= 80 -> Color(0xFF2E7D32)
                                scorePercentage >= 60 -> Color(0xFFEF6C00)
                                else -> Color(0xFFD32F2F)
                            }
                        )
                        Text(
                            text = " / $totalQuestions",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "%$scorePercentage",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Motivational message
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when {
                                scorePercentage >= 90 -> "Mükemmel! Sen bir dahi olabilirsin! 🌟"
                                scorePercentage >= 80 -> "Harika performans! Tebrikler! 🎯"
                                scorePercentage >= 60 -> "İyi iş çıkardın! Biraz daha pratik yapabilirsin! 📚"
                                else -> "Önemli değil! Tekrar denersen daha iyi olacaksın! 🚀"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
        
        item {
            // Results header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 Detaylı Sonuçlar",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$correctAnswers doğru • ${totalQuestions - correctAnswers} yanlış",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Results list
        items(results.withIndex().toList()) { (index, result) ->
            ResultCard(
                questionNumber = index + 1,
                result = result
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action buttons - MODERN VE BÜYÜK
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top row: Restart and New Quiz
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRestartQuiz,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "🔄 Tekrar Çöz", 
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = onGenerateNewQuiz,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "✨ Yeni Test", 
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                // Bottom row: Back to video
                OutlinedButton(
                    onClick = onBackToVideo,
                    modifier = Modifier
                            .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        "🎬 Videoya Dön", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    questionNumber: Int,
    result: QuizResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isCorrect) 
                Color(0xFF2E7D32).copy(alpha = 0.08f) else Color(0xFFD32F2F).copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            2.dp, 
            if (result.isCorrect) Color(0xFF2E7D32) else Color(0xFFD32F2F)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (result.isCorrect) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = questionNumber.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Soru $questionNumber",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (result.isCorrect) "✅" else "❌",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = result.quiz.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!result.isCorrect) {
                Text(
                    text = "❌ Senin cevabın: ${result.selectedAnswer ?: "Cevaplanmadı"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "✅ Doğru cevap: ${result.quiz.correctAnswer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "✅ Doğru cevap: ${result.quiz.correctAnswer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
