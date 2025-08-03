package com.fastthinkerstudios.lastminutegenius.domain.repository

import java.io.File

interface SummaryRepository {

    suspend fun uploadAudioForSummary(
        audioFile: File,
        languageCodeStr: String,
        frames: List<File>?
    ): String
}