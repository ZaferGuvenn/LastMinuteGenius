package com.fastthinkerstudios.lastminutegenius.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fastthinkerstudios.lastminutegenius.data.local.converter.StringListConverter
import com.fastthinkerstudios.lastminutegenius.presentation.quiz.Quiz

@Entity(tableName = "quiz_questions")
@TypeConverters(StringListConverter::class)
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
) {
    fun toDomain(): Quiz = Quiz(question, options, correctAnswer)
}
