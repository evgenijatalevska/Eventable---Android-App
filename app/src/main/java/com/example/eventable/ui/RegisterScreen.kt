package com.example.eventable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit = {},
    onAuthSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var registrationSuccess by remember { mutableStateOf(false) }

    // ПОПРАВЕНО: by наместо .value
    val isLoading by viewModel.isLoading
    val error by viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (registrationSuccess) "Регистрацијата е успешна!"
            else if (isLoginMode) "Добредојдовте назад"
            else "Креирај профил",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PastelGreenDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (registrationSuccess) "Вашиот профил е креиран."
            else if (isLoginMode) "Внесете ги вашите податоци за најава"
            else "Пополнете ги податоците за вашиот профил",
            fontSize = 14.sp,
            color = TextDark.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        if (error != null && !registrationSuccess) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (registrationSuccess) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PastelGreenPrimary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✅",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Добредојдовте, $firstName!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PastelGreenDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Вашиот профил е успешно креиран. Најавете се со вашата е-маил адреса и лозинка.",
                        fontSize = 13.sp,
                        color = TextDark.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = {
                    registrationSuccess = false
                    isLoginMode = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary)
            ) {
                Text(
                    text = "Оди на Најава",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        } else {
            if (!isLoginMode) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Име *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Презиме") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Име на игротека / компанија") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Е-маил адреса *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Лозинка *") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = PastelGreenDark)
            } else {
                Button(
                    onClick = {
                        if (isLoginMode) {
                            viewModel.signInWithEmail(email, password, onAuthSuccess)
                        } else {
                            viewModel.signUpWithEmail(
                                email, password,
                                firstName, lastName, companyName,
                                onSuccess = {
                                    registrationSuccess = true
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary)
                ) {
                    Text(
                        text = if (isLoginMode) "Најави се" else "Регистрирај се",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { isLoginMode = !isLoginMode }) {
                    Text(
                        text = if (isLoginMode) "Немаш профил? Регистрирај се тука"
                        else "Веќе имаш профил? Најави се",
                        color = PastelGreenDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(onClick = onBackClick) {
                    Text(
                        text = "Назад кон почетна",
                        color = TextDark.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = PastelGreenPrimary,
    unfocusedIndicatorColor = TextDark.copy(alpha = 0.2f),
    focusedLabelColor = PastelGreenPrimary,
    unfocusedLabelColor = TextDark.copy(alpha = 0.5f)
)