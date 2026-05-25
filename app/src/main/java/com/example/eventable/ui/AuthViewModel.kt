package com.example.eventable.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    // Флаг за да не скокне на Dashboard при регистрација
    private var isRegistering = false

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && !isRegistering) {
                _isUserLoggedIn.value = true
            }
        }
    }

    fun setErrorMessage(message: String) {
        errorMessage.value = message
    }

    // 1. Најава со Е-маил и Лозинка
    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.value = "Ве молиме пополнете ги сите полиња."
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _isUserLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = when {
                    e.message?.contains("password") == true -> "Погрешна лозинка. Обидете се повторно."
                    e.message?.contains("no user") == true -> "Не постои корисник со овој е-маил."
                    e.message?.contains("email") == true -> "Е-маил адресата е невалидна."
                    e.message?.contains("network") == true -> "Проблем со интернет конекција."
                    else -> "Грешка при најава: ${e.localizedMessage}"
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    // 2. Регистрација со зачувување во Firestore
    fun signUpWithEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        companyName: String,
        onSuccess: () -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
            errorMessage.value = "Ве молиме пополнете ги задолжителните полиња."
            return
        }
        viewModelScope.launch {
            isRegistering = true
            isLoading.value = true
            errorMessage.value = null
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: return@launch

                val user = hashMapOf(
                    "uid" to uid,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "companyName" to companyName,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(uid).set(user).await()

                auth.signOut()
                _isUserLoggedIn.value = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = when {
                    e.message?.contains("password") == true -> "Лозинката мора да има најмалку 6 карактери."
                    e.message?.contains("already") == true -> "Веќе постои корисник со овој е-маил."
                    e.message?.contains("email") == true -> "Е-маил адресата е невалидна."
                    e.message?.contains("network") == true -> "Проблем со интернет конекција."
                    else -> "Грешка при регистрација: ${e.localizedMessage}"
                }
            } finally {
                isRegistering = false
                isLoading.value = false
            }
        }
    }

    // 3. Анонимна најава
    fun signInAnonymously(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                auth.signInAnonymously().await()
                _isUserLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = "Грешка: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // 4. Најава со Google
    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _isUserLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = "Google најавата неуспешна: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // 5. Најава со Facebook
    fun signInWithFacebook(token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val credential = FacebookAuthProvider.getCredential(token)
                auth.signInWithCredential(credential).await()
                _isUserLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = "Facebook најавата неуспешна: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Одјава
    fun signOut() {
        auth.signOut()
        _isUserLoggedIn.value = false
    }
}