package com.example.codriving

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.codriving.navigation.AppNavigation
import com.example.codriving.screens.LoginPage.domain.GoogleAuthUiClient
import com.example.codriving.screens.LoginPage.ui.LoginViewModel
import com.example.codriving.ui.theme.CoDrivingTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var auth: FirebaseAuth
    private val viewModel by viewModels<LoginViewModel>()


    private val gooogleAuthUiCLient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoDrivingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController,auth)
                }
            }
        }
    }
}
@Composable
fun App(navController: NavHostController, auth: FirebaseAuth) {

    AppNavigation(navController,auth)
}



