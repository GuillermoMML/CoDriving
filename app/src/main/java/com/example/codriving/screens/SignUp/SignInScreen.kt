package com.example.codriving.screens.SignUp

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.codriving.R
import com.example.codriving.navigation.AppScreens
import kotlinx.coroutines.launch

@Composable
fun SignScreen(
    navController: NavHostController
) {
    val viewModel: SignInViewModel = hiltViewModel() // Injected in the composable

    val email: String by viewModel.email.observeAsState("")
    val password: String by viewModel.password.observeAsState("")
    val name: String by viewModel.fullname.observeAsState("")
    val phone: String by viewModel.phone.observeAsState("")
    val loading: Boolean by viewModel.loading.observeAsState(false)
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
            text = {  Text(text = errorMessage)  },
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
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "UserIcon"
                    )
                },
                singleLine = true,
                label = { Text(text = "Username") },
                placeholder = { Text(text = "Enter your username") },
                onValueChange = {
                    viewModel.onSignChange(email, password,it, phone)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            SignInFieldEmail(
                email = email,
                onTextFieldChanged = { viewModel.onSignChange(it, password, name, phone) })

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phone,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "PhoneIcon"
                    )
                },
                singleLine = true,
                label = { Text(text = "Number (no obligatory)") },
                placeholder = { Text(text = "Enter your username") },
                onValueChange = {
                    viewModel.onSignChange(email, password,name,it)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            SignInFieldPassword(
                password = password,
                onTextFieldChanged = { viewModel.onSignChange(email, it,name,phone) })

            SignInButton(
                loginEnable,
                loading,
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
                if(loading){
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                }
            }

        }

    }
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
                imageVector = Icons.Default.Email,
                contentDescription = "UserIcon"
            )
        },
        singleLine = true,
        label = { Text(text = "Email") },
        placeholder = { Text(text = "Enter your email") },
        onValueChange = {
            onTextFieldChanged(it)
        },
    )

}

@Composable
fun SignInButton(
    loginEnable: Boolean,
    loading: Boolean,
    onSignIn: () -> Unit // Un único parámetro de callback
) {

    Button(
        onClick = {
            onSignIn() // Llamar al callback
        },
        modifier = Modifier.padding(15.dp),
        enabled = loginEnable && !loading
    ) {
        Text(text = "Crear cuenta")
    }
}


