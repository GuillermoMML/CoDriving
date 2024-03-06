package com.example.codriving.LoginPage.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.codriving.R
import com.example.codriving.navigation.AppScreens
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel,
    ) {

    val email: String by viewModel.email.observeAsState("")
    val password: String by viewModel.password.observeAsState("")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(false)
    val errorMessage = remember { mutableStateOf("") }
    val clicked by viewModel.clicked.collectAsState()

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
                    .clip(CircleShape) // Clip con forma de círculo
            )        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            )
        {

            EmailField(
                email = email,
                onTextFieldChanged = { viewModel.onLoginChange(it, password) })

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                password = password,
                onTextFieldChanged = { viewModel.onLoginChange(email, it) })

            LoginButton(loginEnable) {
                viewModel.signInWithEmailAndPassword(
                    email = email,
                    password = password,
                    showErrorMessage = { errorMessage.value = it },
                    HomePage = {
                        // Navegar a la página de inicio cuando el inicio de sesión sea exitoso
                        navController.navigate(AppScreens.HomeScreen.route)
                    }
                )
            }

            if (errorMessage.value.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { errorMessage.value = "" }, // Limpiar el mensaje de error al cerrar el diálogo
                    title = { Text(text = "Error") },
                    text = { Text(text = errorMessage.value) },
                    confirmButton = {
                        Button(
                            onClick = { errorMessage.value = "" }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )

            }


        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center

            ) {
                Surface(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.isClicked()
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 12.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 12.dp
                            )
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearOutSlowInEasing
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "googleSignUp",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = "Sign Up with Google")

                        if (clicked) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

            }
            TextButton(onClick = {
                navController.navigate(AppScreens.SignInScreen.route)
            }) {
                Text(text = stringResource(id = R.string.notAccountYet),)
            }
        }

    }
}

@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
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
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
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
fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = Modifier
            .padding(15.dp),
        enabled = loginEnable
    )
    {

        Text(text = "Iniciar Sesión")
    }
}