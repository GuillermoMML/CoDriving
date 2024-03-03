package com.example.codriving.LoginPage.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.codriving.R
import com.example.codriving.SignUp.ui.SignInViewModel
import com.example.codriving.navigation.AppScreens
import kotlinx.coroutines.launch

@Composable
fun SignScreen(
    viewModel: SignInViewModel,
    navController: NavHostController
) {
    val email: String by viewModel.email.observeAsState("")
    val password: String by viewModel.password.observeAsState("")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(false)
    val errorMessage: String by viewModel.errorMessage.observeAsState("")
    val showError = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    if (showError.value) {
        AlertDialog(
            onDismissRequest = {
                showError.value = false
            },
            title = { Text(text = "Error") },
            text = { errorMessage?.let { Text(text = it) } },
            confirmButton = {
                Button(onClick = {
                    showError.value = false
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(0.5F),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ecofamily), // Usa tu recurso de imagen aquí
                contentDescription = "App",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            )
        {
            SignInFieldEmail(
                email = email,
                onTextFieldChanged = { viewModel.onSignChange(it, password) })
            Spacer(modifier = Modifier.height(16.dp))
            SignInFieldPassword(
                password = password,
                onTextFieldChanged = { viewModel.onSignChange(email, it) })

            SignInButton(
                loginEnable,
            ) {
                coroutineScope.launch {
                    viewModel.signIn(email = email, password = password) { success ->
                    if (success) {
                        navController.navigate(AppScreens.LoginScreen.route)
                    } else {
                        showError.value = true
                    }
                }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            }

        }

    }
}

@Composable
fun MostrarVentanaError(errorMessage: String) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Error") },
        text = { Text(text = errorMessage) },
        confirmButton = {
            Button(onClick = { /* Cerrar la ventana de error */ }) {
                Text("Aceptar")
            }
        }
    )
}


@Composable
fun SignInFieldPassword(password: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        modifier = Modifier.fillMaxWidth(),
        value = password,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Password"
            )
        },
        singleLine = true,
        label = { Text(text = "Password") },
        placeholder = { Text(text = "Enter your password") },
        onValueChange = {
            onTextFieldChanged(it)
        },
    )

}

@Composable
fun SignInFieldEmail(email: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = email,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "UserIcon"
            )
        },
        singleLine = true,
        label = { Text(text = "Username") },
        placeholder = { Text(text = "Enter your username") },
        onValueChange = {
            onTextFieldChanged(it)
        },
    )

}

@Composable
fun SignInButton(
    loginEnable: Boolean,
    onSignIn: () -> Unit // Un único parámetro de callback
) {

    Button(
        onClick = {
            onSignIn() // Llamar al callback
        },
        modifier = Modifier.padding(15.dp),
        enabled = loginEnable
    ) {
        Text(text = "Crear cuenta")
    }
}


