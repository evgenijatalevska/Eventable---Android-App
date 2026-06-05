package com.example.eventable.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventable.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: "Нема е-маил"

    // Ги земаме точните состојби од твојот AuthViewModel
    var firstName by remember { mutableStateOf(authViewModel.firstNameState.value) }
    var lastName by remember { mutableStateOf(authViewModel.lastNameState.value) }
    var companyName by remember { mutableStateOf(authViewModel.companyNameState.value) }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(authViewModel.profileImageUriState.value?.let { Uri.parse(it) })
    }

    // 📸 Launcher за галерија
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Уреди Профил", fontWeight = FontWeight.Bold, color = PastelGreenDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = PastelGreenDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // =====================================================
            // 📸 СЕКЦИЈА ЗА ПРОФИЛНА СЛИКА
            // =====================================================
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clickable {
                        galleryLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PastelGreenPrimary.copy(alpha = 0.15f))
                        .border(2.dp, PastelGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Профилна слика",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Профилна слика",
                            modifier = Modifier.size(50.dp),
                            tint = PastelGreenDark
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PastelGreenPrimary)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Промени слика",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =====================================================
            // 🔒 ПОЛЕ ЗА Е-МАИЛ (ЗАКЛУЧЕНО)
            // =====================================================
            OutlinedTextField(
                value = userEmail,
                onValueChange = {},
                label = { Text("Е-маил адреса (Заклучено)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Gray,
                    disabledBorderColor = Color.LightGray.copy(alpha = 0.6f),
                    disabledLabelColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )

            // =====================================================
            // ✍️ ПОЛИЊА ЗА ВНЕСУВАЊЕ
            // =====================================================
            OutlinedTextField(
                value = firstName,
                onValueChange = { text -> firstName = text },
                label = { Text("Име") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PastelGreenPrimary, focusedLabelColor = PastelGreenDark)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { text -> lastName = text },
                label = { Text("Презиме") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PastelGreenPrimary, focusedLabelColor = PastelGreenDark)
            )

            OutlinedTextField(
                value = companyName,
                onValueChange = { text -> companyName = text },
                label = { Text("Име на игротека") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PastelGreenPrimary, focusedLabelColor = PastelGreenDark)
            )

            Spacer(modifier = Modifier.weight(1f))

            // =====================================================
            // 💾 КОПЧЕ ЗА ЗАЧУВУВАЊЕ
            // =====================================================
            Button(
                onClick = {
                    authViewModel.updateUserProfile(
                        firstName = firstName,
                        lastName = lastName,
                        companyName = companyName,
                        imageUri = selectedImageUri?.toString(),
                        onComplete = {
                            onBack()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary)
            ) {
                Text("Зачувај Промени", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}