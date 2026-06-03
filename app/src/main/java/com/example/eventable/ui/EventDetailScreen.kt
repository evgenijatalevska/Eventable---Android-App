package com.example.eventable.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    eventViewModel: EventViewModel = viewModel(),
    onBack: () -> Unit
) {
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    val event = events.find { it.id == eventId }

    var isEditMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var title by remember(event) { mutableStateOf(event?.title ?: "") }
    var location by remember(event) { mutableStateOf(event?.location ?: "") }
    var date by remember(event) { mutableStateOf(event?.date ?: "") }
    var time by remember(event) { mutableStateOf(event?.time ?: "") }
    var offer by remember(event) { mutableStateOf(event?.offer ?: "") }
    var adultsCount by remember(event) { mutableStateOf(event?.adultsCount?.toString() ?: "") }
    var childrenCount by remember(event) { mutableStateOf(event?.childrenCount?.toString() ?: "") }
    var food by remember(event) { mutableStateOf(event?.food ?: "") }
    var notes by remember(event) { mutableStateOf(event?.notes ?: "") }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PastelGreenDark)
        }
        return
    }

    // Дијалог за потврда на бришење
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Избриши настан") },
            text = { Text("Дали сте сигурни дека сакате да го избришете овој настан?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventViewModel.deleteEvent(eventId) { onBack() }
                        showDeleteDialog = false
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
                        text = if (isEditMode) "Уреди Настан" else event.title,
                        fontWeight = FontWeight.Bold,
                        color = PastelGreenDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = PastelGreenDark
                        )
                    }
                },
                actions = {
                    if (!isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Уреди",
                                tint = PastelGreenDark
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Избриши",
                                tint = Color.Red
                            )
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEditMode) {
                // Edit Mode
                EventTextField(value = title, onValueChange = { title = it }, label = "Ime на настан *")
                EventTextField(value = location, onValueChange = { location = it }, label = "Локација")
                EventTextField(value = date, onValueChange = { date = it }, label = "Датум")
                EventTextField(value = time, onValueChange = { time = it }, label = "Време")
                EventTextField(value = offer, onValueChange = { offer = it }, label = "Понуда")
                EventTextField(
                    value = adultsCount,
                    onValueChange = { adultsCount = it },
                    label = "Број на возрасни",
                    keyboardType = KeyboardType.Number
                )
                EventTextField(
                    value = childrenCount,
                    onValueChange = { childrenCount = it },
                    label = "Број на деца",
                    keyboardType = KeyboardType.Number
                )
                EventTextField(value = food, onValueChange = { food = it }, label = "Храна")
                EventTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Белешки",
                    singleLine = false,
                    minLines = 3
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
                            val updated = event.copy(
                                title = title,
                                location = location,
                                date = date,
                                time = time,
                                offer = offer,
                                adultsCount = adultsCount.toIntOrNull() ?: 0,
                                childrenCount = childrenCount.toIntOrNull() ?: 0,
                                food = food,
                                notes = notes
                            )
                            eventViewModel.updateEvent(updated) { isEditMode = false }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary)
                    ) {
                        Text("Зачувај", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

            } else {
                // View Mode
                DetailRow(label = "📅 Датум", value = event.date)
                DetailRow(label = "🕒 Време", value = event.time)
                DetailRow(label = "📍 Локација", value = event.location)
                DetailRow(label = "🎁 Понуда", value = event.offer)
                DetailRow(label = "👨 Возрасни", value = event.adultsCount.toString())
                DetailRow(label = "👧 Деца", value = event.childrenCount.toString())
                DetailRow(label = "🍕 Храна", value = event.food)
                if (event.notes.isNotEmpty()) {
                    DetailRow(label = "📝 Белешки", value = event.notes)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    if (value.isEmpty() || value == "0") return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PastelGreenPrimary.copy(alpha = 0.06f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }
    }
}