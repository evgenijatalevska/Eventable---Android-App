package com.example.eventable.ui

import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventable.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onEditProfileClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: "Нема е-маил"

    // Динамичко земање на вредностите директно од ViewModel
    val companyName by remember { authViewModel.companyNameState }
    val profileImageUri by remember { authViewModel.profileImageUriState }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            PastelGreenPrimary.copy(alpha = 0.35f),
            BackgroundWhite,
            BackgroundWhite
        )
    )

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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Динамична профилна слика
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(PastelGreenPrimary.copy(alpha = 0.15f))
                            .border(2.dp, PastelGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profileImageUri.isNullOrEmpty()) {
                            AsyncImage(
                                model = Uri.parse(profileImageUri),
                                contentDescription = "Профилна слика",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Лого",
                                modifier = Modifier.size(44.dp),
                                tint = PastelGreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Динамично име на игротека/компанија
                    Text(
                        text = companyName.ifEmpty { "Име на игротека" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Уреди Профил", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // =====================================================
            // 💳 КАРТИЧКА 2: ЈАЗИК И НОТИФИКАЦИИ
            // =====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Language,
                        title = "Јазик",
                        subtitle = "Македонски",
                        onClick = onLanguageClick
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.Notifications,
                        title = "Нотификации",
                        subtitle = "Вклучени",
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.ExitToApp,
                        title = "Одјави се",
                        iconTint = PastelGreenDark,
                        onClick = {
                            authViewModel.signOut()
                            onLogoutSuccess()
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                    ProfileMenuRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Избриши Акаунт",
                        titleColor = Color.Red,
                        iconTint = Color.Red,
                        onClick = { /* TODO: Бришење во Firebase Auth */ }
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
    titleColor: Color = TextDark,
    iconTint: Color = PastelGreenDark,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избери Јазик", fontWeight = FontWeight.Bold, color = PastelGreenDark) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = PastelGreenDark) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { p -> Box(Modifier.padding(p).fillMaxSize()) { Text("Опции за промена на јазик (MK / EN).", Modifier.padding(16.dp)) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Нотификации", fontWeight = FontWeight.Bold, color = PastelGreenDark) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = PastelGreenDark) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { p -> Box(Modifier.padding(p).fillMaxSize()) { Text("Подесувања за Пуш-нотификации и потсетници.", Modifier.padding(16.dp)) } }
}