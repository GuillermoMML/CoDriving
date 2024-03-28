package com.example.codriving.data.repository

import android.net.Uri
import com.google.android.gms.tasks.Task

interface FirebaseStorageRepository {
    fun uploadImage(imageUri: Uri): Task<Uri>
}
