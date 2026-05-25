package com.example.eventable

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventable.ui.AuthViewModel
import com.example.eventable.ui.DashboardScreen
import com.example.eventable.ui.LoginScreen
import com.example.eventable.ui.RegisterScreen
import com.example.eventable.ui.theme.EventableTheme
import com.facebook.CallbackManager

enum class Screen {
    LOGIN,
    EMAIL_AUTH,
    DASHBOARD
}

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        setContent {
            EventableTheme {
                val isLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

                var currentScreen by remember {
                    mutableStateOf(
                        if (authViewModel.isUserLoggedIn.value) Screen.DASHBOARD
                        else Screen.LOGIN
                    )
                }

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) currentScreen = Screen.DASHBOARD
                    else if (currentScreen == Screen.DASHBOARD) currentScreen = Screen.LOGIN
                }

                when (currentScreen) {
                    Screen.LOGIN -> {
                        LoginScreen(
                            viewModel = authViewModel,
                            callbackManager = callbackManager,
                            onEmailLoginClick = { currentScreen = Screen.EMAIL_AUTH },
                            onGoogleLoginClick = { },
                            onFacebookLoginClick = { },
                            onLoginSuccess = { currentScreen = Screen.DASHBOARD }
                        )
                    }
                    Screen.EMAIL_AUTH -> {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onBackClick = { currentScreen = Screen.LOGIN },
                            onAuthSuccess = { currentScreen = Screen.DASHBOARD }
                        )
                    }
                    Screen.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = authViewModel,
                            onLogoutSuccess = { currentScreen = Screen.LOGIN }
                        )
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }
}