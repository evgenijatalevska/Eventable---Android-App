package com.example.eventable.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.data.Offer
import com.example.eventable.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOfferScreen(
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Нова Понуда", fontWeight = FontWeight.Bold, color = PastelGreenDark) },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Име на понудата
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Име на понуда (напр. Понуда 1)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PastelGreenDark,
                    focusedLabelColor = PastelGreenDark,
                    cursorColor = PastelGreenDark
                )
            )

            // Текст во стил на Notepad (Слободно внесување)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Внесете ги деталите за понудата тука...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PastelGreenDark,
                    focusedLabelColor = PastelGreenDark,
                    cursorColor = PastelGreenDark
                )
            )

            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        onSave(title, content)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary),
                enabled = title.isNotEmpty()
            ) {
                Text("Зачувај Понуда", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}