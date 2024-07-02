package com.example.codriving.data.repository

import android.net.Uri
import com.example.codriving.data.model.Notification
import com.google.android.gms.tasks.Task
import java.io.File

interface FirebaseStorageRepository {
    fun uploadImage(imageUri: Uri): Task<Uri>
    fun uploadPDF(pdfPath: String, notification: Notification): Task<Uri>
    suspend fun downloadPDF(
        pdfName: String,
        onSuccess: (File) -> Unit,
        onFailure: (Exception) -> Unit
    )
}
