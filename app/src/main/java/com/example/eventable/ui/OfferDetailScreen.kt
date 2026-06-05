package com.example.eventable.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.data.Offer
import com.example.eventable.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDetailScreen(
    offer: Offer?,
    onUpdate: (String, String) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    if (offer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PastelGreenDark)
        }
        return
    }

    var isEditMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var title by remember(offer) { mutableStateOf(offer.title) }
    var content by remember(offer) { mutableStateOf(offer.content) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Избриши понуда") },
            text = { Text("Дали сте сигурни дека сакате да ја избришете оваа понуда?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Избриши", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Откажи", color = PastelGreenDark)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Уреди понуда" else offer.title,
                        fontWeight = FontWeight.Bold,
                        color = PastelGreenDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditMode) {
                            isEditMode = false
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = PastelGreenDark
                        )
                    }
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Уреди", tint = PastelGreenDark)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Избриши", tint = Color.Red)
                        }
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
            if (isEditMode) {
                // --- EDIT MODE ---
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Име на понуда") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelGreenDark,
                        focusedLabelColor = PastelGreenDark,
                        cursorColor = PastelGreenDark
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Детали за понудата...") },
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isEditMode = false },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PastelGreenDark)
                    ) {
                        Text("Откажи")
                    }
                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onUpdate(title, content)
                                isEditMode = false
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary),
                        enabled = title.isNotEmpty()
                    ) {
                        Text("Зачувај", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

            } else {
                // --- VIEW MODE ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    // Поправено: Сега тука долу динамички се испишува вистинскиот наслов на понудата
                    Text(text = "Детали: ", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (offer.content.isNotEmpty()) offer.content else "Нема внесено дополнителни податоци.",
                    fontSize = 16.sp,
                    color = TextDark.copy(alpha = 0.8f),
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}