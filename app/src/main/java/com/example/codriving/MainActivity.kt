package com.example.codriving

import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.User
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.example.codriving.navigation.AppNavigation
import com.example.codriving.ui.theme.CoDrivingTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var userRepository: UserRepository
    lateinit var uploadCarRepository: UploadCarRepository


    /*
        private val gooogleAuthUiCLient by lazy {
            GoogleAuthUiClient(
                context = applicationContext,
                oneTapClient = Identity.getSignInClient(applicationContext)
            )
        }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.getInstance()
        /*  userRepository = UserRepository(firestoreIns)
          uploadCarRepository = UploadCarRepository(FirebaseFirestore.getInstance())
          if (::userRepository.isInitialized && ::uploadCarRepository.isInitialized) {
              lifecycleScope.launch {
                  val uid = auth.uid
                  if (uid != null) {
                      val firestore = FirebaseFirestore.getInstance()

                     // firestore.collection("")
                  }


          }*/

        setContent {
            CoDrivingTheme {
                // A surface container using the 'background' color from the theme
                Surface {
                    val navController = rememberNavController()
                    App(navController, auth)

                }
            }
        }
    }


    private fun showNotification(senderUser: User, car: Car) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(this, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Tienes notificaciones disponibles")
            .setContentText("\"${senderUser.fullName} esta interesado en tu coche ${car.model}")
            .setSmallIcon(R.drawable.notifications_active_24dp_fill0_wght400_grad0_opsz24)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)

    }


}

@Composable
fun App(navController: NavHostController, auth: FirebaseAuth) {

    AppNavigation(navController, auth, isLogged = {

    })
}




