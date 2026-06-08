package com.example.eventable.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.R
import com.example.eventable.data.Event
import com.example.eventable.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    eventViewModel: EventViewModel = viewModel(),
    offerViewModel: OfferViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val errorEnterEventName = stringResource(id = R.string.error_enter_event_name)
    val errorSelectDate = stringResource(id = R.string.error_select_date)
    val errorSelectTime = stringResource(id = R.string.error_select_time)

    val offers by offerViewModel.offers.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var selectedOffer by remember { mutableStateOf("") }
    var adultsCount by remember { mutableStateOf("") }
    var childrenCount by remember { mutableStateOf("") }
    var food by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var offerExpanded by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = "%02d.%02d.%04d".format(dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            time = "%02d:%02d".format(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.new_event),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (errorMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)), // Semantic error — stays red in both themes
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMsg!!,
                        color = Color.Red, // Semantic error — stays red in both themes
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            EventTextField(
                value = title,
                onValueChange = { title = it },
                label = stringResource(id = R.string.event_name)
            )

            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text(stringResource(id = R.string.date_required)) },
                placeholder = { Text(stringResource(id = R.string.date_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = stringResource(id = R.string.choose_date),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = dynamicTextFieldColors()
            )

            OutlinedTextField(
                value = time,
                onValueChange = { },
                label = { Text(stringResource(id = R.string.time_required)) },
                placeholder = { Text(stringResource(id = R.string.time_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = stringResource(id = R.string.choose_time),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = dynamicTextFieldColors()
            )

            ExposedDropdownMenuBox(
                expanded = offerExpanded,
                onExpandedChange = { offerExpanded = !offerExpanded }
            ) {
                OutlinedTextField(
                    value = selectedOffer.ifEmpty { stringResource(id = R.string.offer_label) },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.offer_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = offerExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dynamicTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = offerExpanded,
                    onDismissRequest = { offerExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.without_offer), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                        onClick = {
                            selectedOffer = ""
                            offerExpanded = false
                        }
                    )
                    offers.forEach { offerItem ->
                        DropdownMenuItem(
                            text = { Text(offerItem.title) },
                            onClick = {
                                selectedOffer = offerItem.title
                                offerExpanded = false
                            }
                        )
                    }
                }
            }

            EventTextField(
                value = adultsCount,
                onValueChange = { adultsCount = it },
                label = stringResource(id = R.string.adults_count),
                keyboardType = KeyboardType.Number
            )

            EventTextField(
                value = childrenCount,
                onValueChange = { childrenCount = it },
                label = stringResource(id = R.string.children_count),
                keyboardType = KeyboardType.Number
            )

            OutlinedTextField(
                value = food,
                onValueChange = { food = it },
                label = { Text(stringResource(id = R.string.food)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                minLines = 3,
                colors = dynamicTextFieldColors()
            )

            OutlinedTextField(
                value = additionalInfo,
                onValueChange = { additionalInfo = it },
                label = { Text(stringResource(id = R.string.additional_info)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                minLines = 3,
                colors = dynamicTextFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (title.isEmpty()) {
                        errorMsg = errorEnterEventName
                        return@Button
                    }
                    if (date.isEmpty()) {
                        errorMsg = errorSelectDate
                        return@Button
                    }
                    if (time.isEmpty()) {
                        errorMsg = errorSelectTime
                        return@Button
                    }

                    // Креирање на настанот со addedToCalendar = true
                    val event = Event(
                        title = title,
                        date = date,
                        time = time,
                        offer = selectedOffer,
                        adultsCount = adultsCount.toIntOrNull() ?: 0,
                        childrenCount = childrenCount.toIntOrNull() ?: 0,
                        food = food,
                        notes = additionalInfo,
                        addedToCalendar = true // Измената е тука!
                    )

                    eventViewModel.addEvent(event) {
                        addEventToGoogleCalendar(context, event)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(id = R.string.save_event),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EventTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines,
        colors = dynamicTextFieldColors()
    )
}

@Composable
fun dynamicTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)
