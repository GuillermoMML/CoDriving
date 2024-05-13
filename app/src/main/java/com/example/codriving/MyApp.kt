package com.example.codriving

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "my_channel"
    }

    override fun onCreate() {
        super.onCreate()
        /*
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val exception = task.exception
                Log.e("MyApp", "Error al obtener el token: $exception")
                return@addOnCompleteListener
            }
            val token = task.result
            println("El token se genero>$token")
        }
            .addOnFailureListener {
                Log.e("Error messaging token", "${it.message}")
            }*/
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notificaciones de CoDriving",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Este canal se usa para notificaiones de CoDriving"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}