package com.fastthinkerstudios.lastminutegenius.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fastthinkerstudios.lastminutegenius.data.local.entity.QuizEntity

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuizEntity>)

    @Query("SELECT * FROM quiz_questions WHERE videoId = :videoId")
    suspend fun getQuizForVideo(videoId: Int): List<QuizEntity>

    @Query("DELETE FROM quiz_questions WHERE videoId = :videoId")
    suspend fun deleteQuizForVideo(videoId: Int)
}
