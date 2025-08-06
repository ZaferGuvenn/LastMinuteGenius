package com.fastthinkerstudios.lastminutegenius.domain.repository

import java.io.File

interface SummaryRepository {

    suspend fun uploadAudioUriForSummary(
        gcsUri: String,
        languageCode: String,
        frames: List<File>?
    ): String
}