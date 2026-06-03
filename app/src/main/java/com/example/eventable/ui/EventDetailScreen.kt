package com.example.eventable.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
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
    offerViewModel: OfferViewModel = viewModel(), // Додаден OfferViewModel за уредување
    onBack: () -> Unit
) {
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    val event = events.find { it.id == eventId }

    // Земи ги понудите за Dropdown менито
    val offers by offerViewModel.offers.collectAsStateWithLifecycle()

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

    // Контрола за отворање на паѓачкото мени во Edit режим
    var offerExpanded by remember { mutableStateOf(false) }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PastelGreenDark)
        }
        return
    }

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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditMode) {
                // --- EDIT MODE ---
                EventTextField(value = title, onValueChange = { title = it }, label = "Име на настан *")
                EventTextField(value = location, onValueChange = { location = it }, label = "Локација")
                EventTextField(value = date, onValueChange = { date = it }, label = "Датум")
                EventTextField(value = time, onValueChange = { time = it }, label = "Време")

                // --- ИЗБОР НА ПОНУДА ПРЕКУ DROP DOWN ВО EDIT РЕЖИМ ---
                ExposedDropdownMenuBox(
                    expanded = offerExpanded,
                    onExpandedChange = { offerExpanded = !offerExpanded }
                ) {
                    OutlinedTextField(
                        value = offer.ifEmpty { "Понуда " },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Понуда") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = offerExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = offerExpanded,
                        onDismissRequest = { offerExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Без понуда", color = Color.Gray) },
                            onClick = {
                                offer = ""
                                offerExpanded = false
                            }
                        )
                        offers.forEach { offerItem ->
                            DropdownMenuItem(
                                text = { Text(offerItem.title) },
                                onClick = {
                                    offer = offerItem.title
                                    offerExpanded = false
                                }
                            )
                        }
                    }
                }

                EventTextField(value = adultsCount, onValueChange = { adultsCount = it }, label = "Број на возрасни", keyboardType = KeyboardType.Number)
                EventTextField(value = childrenCount, onValueChange = { childrenCount = it }, label = "Број на деца", keyboardType = KeyboardType.Number)
                EventTextField(value = food, onValueChange = { food = it }, label = "Храна")
                EventTextField(value = notes, onValueChange = { notes = it }, label = "Белешки", singleLine = false, minLines = 3)

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
                // --- VIEW MODE ---

                DetailRowClean(
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PastelGreenDark, modifier = Modifier.size(20.dp)) },
                    text = event.date
                )

                DetailRowClean(
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = PastelGreenDark, modifier = Modifier.size(20.dp)) },
                    text = event.time
                )

                if (event.offer.isNotEmpty()) {
                    DetailRowClean(
                        icon = { Text("🎁", fontSize = 18.sp) },
                        text = "Понуда: ${event.offer}"
                    )
                }

                if (event.adultsCount > 0) {
                    DetailRowClean(
                        icon = { Text("👨", fontSize = 18.sp) },
                        text = "Број на возрасни: ${event.adultsCount}"
                    )
                }

                if (event.childrenCount > 0) {
                    DetailRowClean(
                        icon = { Text("👧", fontSize = 18.sp) },
                        text = "Број на деца: ${event.childrenCount}"
                    )
                }

                if (event.food.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🍕", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Храна:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Text(
                            text = event.food,
                            fontSize = 15.sp,
                            color = TextDark.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }
                }

                if (event.notes.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📝", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Белешки:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Text(
                            text = event.notes,
                            fontSize = 15.sp,
                            color = TextDark.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowClean(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )
    }
}