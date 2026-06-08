package com.example.eventable.ui

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventable.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onEditProfileClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val noEmailText = stringResource(id = R.string.no_email)
    val userEmail = currentUser?.email ?: noEmailText

    // Динамичко земање на вредностите директно од ViewModel
    val companyName by remember { authViewModel.companyNameState }
    val profileImageUri by remember { authViewModel.profileImageUriState }

    // Следење на состојбата на нотификациите за динамичен поднаслов
    var isNotificationsEnabled by remember {
        mutableStateOf(
            context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .getBoolean("notifications_enabled", true)
        )
    }

    // Секогаш кога корисникот ќе се врати на овој екран, ја освежуваме состојбата
    LaunchedEffect(Unit) {
        isNotificationsEnabled = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getBoolean("notifications_enabled", true)
    }

    // Следење на активниот јазик за динамичен поднаслов на копчето „Јазик"
    val macedonianLabel = stringResource(id = R.string.macedonian)
    val englishLabel = stringResource(id = R.string.english)
    fun currentLanguageLabel() =
        if (AppCompatDelegate.getApplicationLocales().get(0)?.language == "en") englishLabel else macedonianLabel

    var languageSubtitle by remember { mutableStateOf(currentLanguageLabel()) }

    // Секогаш кога корисникот ќе се врати на овој екран, го освежуваме избраниот јазик
    LaunchedEffect(Unit) {
        languageSubtitle = currentLanguageLabel()
    }

    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text(stringResource(id = R.string.delete_account)) },
            text = { Text(stringResource(id = R.string.delete_account_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        authViewModel.deleteAccount(
                            onSuccess = { onLogoutSuccess() },
                            onError = { /* isLoading resets in finally; surface error if needed */ }
                        )
                    }
                ) {
                    Text(stringResource(id = R.string.delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(stringResource(id = R.string.cancel), color = MaterialTheme.colorScheme.secondary)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =====================================================
            // 💳 КАРТИЧКА 1: ПРОФИЛНИ ПОДАТОЦИ
            // =====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profileImageUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = Uri.parse(profileImageUri),
                                contentDescription = stringResource(id = R.string.profile_picture),
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = stringResource(id = R.string.logo),
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = companyName.ifEmpty { stringResource(id = R.string.company_name_label) },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.edit_profile), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // =====================================================
            // 💳 КАРТИЧКА 2: ЈАЗИК И НОТИФИКАЦИИ
            // =====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Language,
                        title = stringResource(id = R.string.language),
                        subtitle = languageSubtitle,
                        onClick = onLanguageClick
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(id = R.string.notifications),
                        subtitle = if (isNotificationsEnabled) stringResource(id = R.string.enabled) else stringResource(id = R.string.disabled), // Динамички поднасловови
                        onClick = onNotificationsClick
                    )
                }
            }

            // =====================================================
            // 💳 КАРТИЧКА 3: СМЕТКА (ОДЈАВИ СЕ И ИЗБРИШИ)
            // =====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.ExitToApp,
                        title = stringResource(id = R.string.logout),
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = {
                            authViewModel.signOut()
                            onLogoutSuccess()
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.DeleteForever,
                        title = stringResource(id = R.string.delete_account),
                        titleColor = Color.Red,
                        iconTint = Color.Red,
                        onClick = { showDeleteAccountDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color.Unspecified,
    iconTint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    val resolvedIconTint = if (iconTint == Color.Unspecified) MaterialTheme.colorScheme.secondary else iconTint
    val resolvedTitleColor = if (titleColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else titleColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = resolvedIconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = resolvedTitleColor)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    // Ја читаме моментално активната локала за да го одразиме точниот избор при отворање
    var selectedLanguage by remember {
        val activeTag = AppCompatDelegate.getApplicationLocales().get(0)?.language
        mutableStateOf(if (activeTag == "en") "en" else "mk")
    }

    fun applyLanguage(languageTag: String) {
        selectedLanguage = languageTag
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.choose_language), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, stringResource(id = R.string.back), tint = MaterialTheme.colorScheme.secondary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    LanguageOptionRow(
                        label = stringResource(id = R.string.macedonian),
                        selected = selectedLanguage == "mk",
                        onClick = { applyLanguage("mk") }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                    LanguageOptionRow(
                        label = stringResource(id = R.string.english),
                        selected = selectedLanguage == "en",
                        onClick = { applyLanguage("en") }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

// =====================================================
// 🔔 ЦЕЛОСНО ФУНКЦИОНАЛЕН ЕКРАН ЗА НОТИФИКАЦИИ
// =====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }

    // Вчитување на зачуваната состојба (по дефолт е true)
    var isChecked by remember { mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.notifications), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(id = R.string.back), tint = MaterialTheme.colorScheme.secondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.notifications_subtitle),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.push_notifications),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = R.string.push_notifications_description),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Копче прекинувач (Switch) за палење/гасење на FCM нотификациите
                    Switch(
                        checked = isChecked,
                        onCheckedChange = { value ->
                            isChecked = value
                            // Трајно зачувување на изборот во SharedPreferences
                            sharedPreferences.edit().putBoolean("notifications_enabled", value).apply()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    }
}