package com.example.codriving.data.repository

import android.net.Uri
import android.util.Log
import com.example.codriving.data.model.Notification
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : FirebaseStorageRepository {

    override fun uploadImage(imageUri: Uri): Task<Uri> {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")

        return imagesRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imagesRef.downloadUrl
            }
    }

    override fun uploadPDF(pdfPath: String, notification: Notification): Task<Uri> {
        val storageRef = storage.reference
        val pdfRef = storageRef.child("rentingPDF/${notification.idNotification}.pdf")
        val pdfUri = Uri.fromFile(File(pdfPath))
        return pdfRef.putFile(pdfUri)
            .addOnFailureListener { exception ->
                Log.e("FirebaseUpload", "Failed to upload PDF: ${exception.message}")

            }
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                pdfRef.downloadUrl
            }
            .addOnSuccessListener {

                // Show a toast message on success
                Log.e("Sucessfull", "PDF uploaded")

            }

    }


}
