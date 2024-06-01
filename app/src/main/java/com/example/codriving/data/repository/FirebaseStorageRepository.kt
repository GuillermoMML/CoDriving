package com.example.codriving.data.repository

import android.net.Uri
import com.example.codriving.data.model.Notification
import com.google.android.gms.tasks.Task

interface FirebaseStorageRepository {
    fun uploadImage(imageUri: Uri): Task<Uri>
    fun uploadPDF(pdfPath: String, notification: Notification): Task<Uri>


}
