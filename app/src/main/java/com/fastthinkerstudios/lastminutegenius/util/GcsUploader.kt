package com.fastthinkerstudios.lastminutegenius.util

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.File

object GcsUploader {

    private const val GCS_BUCKET_NAME = "lastminutegenius-da04f.firebasestorage.app"

    suspend fun uploadFlacToGcs(file: File): String? {
        return try {
            val storage = Firebase.storage
            val timestamp = System.currentTimeMillis()
            val uniqueFileName = "${timestamp}_${file.name}"  // örnek: 1691348394000_extracted_audio.flac

            val ref = storage.reference.child("uploads/$uniqueFileName")
            val uri = Uri.fromFile(file)

            // Upload işlemini coroutine ile bekle
            ref.putFile(uri).await()

            // Doğru GCS URI'yi döndür
            "gs://$GCS_BUCKET_NAME/uploads/$uniqueFileName"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}