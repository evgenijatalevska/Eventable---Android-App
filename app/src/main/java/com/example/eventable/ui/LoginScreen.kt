package com.example.eventable.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    callbackManager: CallbackManager,
    onEmailLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onFacebookLoginClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading

    val facebookLoginError = stringResource(id = R.string.facebook_login_error)

    // Иницијализација на Firebase Analytics
    val analytics = remember { FirebaseAnalytics.getInstance(context) }

    val webClientId = "463391834557-n1ph178cukbfpt1l49frrilgv148oomu.apps.googleusercontent.com"
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken) { onLoginSuccess() }
                }
            } catch (e: ApiException) {
                viewModel.setErrorMessage(context.getString(R.string.google_error_prefixed, e.localizedMessage))
            }
        }
    }

    val facebookLauncher = rememberLauncherForActivityResult(
        contract = LoginManager.getInstance().createLogInActivityResultContract(callbackManager, null)
    ) { }

    DisposableEffect(Unit) {
        val loginManager = LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel.signInWithFacebook(result.accessToken.token) { onLoginSuccess() }
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                viewModel.setErrorMessage(error.localizedMessage ?: facebookLoginError)
            }
        })
        onDispose { loginManager.unregisterCallback(callbackManager) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Лого
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.eventable_logo_description),
            modifier = Modifier
                .height(120.dp)
                .padding(bottom = 8.dp)
        )

        // Наслов
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        // Мото
        Text(
            text = stringResource(id = R.string.login_tagline),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 32.dp)
        )

        // Е-маил поле
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email_address)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = dynamicTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Лозинка поле
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = dynamicTextFieldColors()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
        } else {
            // Е-маил копче
            Button(
                onClick = {
                    val bundle = Bundle().apply { putString(FirebaseAnalytics.Param.METHOD, "email") }
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    viewModel.signInWithEmail(email, password, onLoginSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                analytics.logEvent("click_navigate_to_register", null)
                onEmailLoginClick()
            }) {
                Text(
                    text = stringResource(id = R.string.no_account),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.or_continue_via),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Google копче
            Button(
                onClick = {
                    val bundle = Bundle().apply { putString(FirebaseAnalytics.Param.METHOD, "google") }
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    onGoogleLoginClick()
                    googleLauncher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = stringResource(id = R.string.google_label),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.google_label),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Facebook копче
            Button(
                onClick = {
                    val bundle = Bundle().apply { putString(FirebaseAnalytics.Param.METHOD, "facebook") }
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    onFacebookLoginClick()
                    facebookLauncher.launch(listOf("public_profile", "email"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = stringResource(id = R.string.facebook_label),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.facebook_label),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Гостин копче
            Button(
                onClick = {
                    val bundle = Bundle().apply { putString(FirebaseAnalytics.Param.METHOD, "guest") }
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    viewModel.signInAnonymously(onLoginSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.guest_label),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}