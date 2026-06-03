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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.data.Event
import com.example.eventable.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    eventViewModel: EventViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var selectedOffer by remember { mutableStateOf("") }
    var adultsCount by remember { mutableStateOf("") }
    var childrenCount by remember { mutableStateOf("") }
    var food by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

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
                        "Нов Настан",
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
            if (errorMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMsg!!,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            EventTextField(
                value = title,
                onValueChange = { title = it },
                label = "Име на настан *"
            )

            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Датум *") },
                placeholder = { Text("дд.мм.гггг") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Избери датум",
                            tint = PastelGreenDark
                        )
                    }
                },
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = time,
                onValueChange = { },
                label = { Text("Време *") },
                placeholder = { Text("чч:мм") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = "Избери време",
                            tint = PastelGreenDark
                        )
                    }
                },
                colors = textFieldColors()
            )

            EventTextField(
                value = selectedOffer,
                onValueChange = { selectedOffer = it },
                label = "Понуда"
            )

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

            OutlinedTextField(
                value = food,
                onValueChange = { food = it },
                label = { Text("Храна") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                minLines = 3,
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = additionalInfo,
                onValueChange = { additionalInfo = it },
                label = { Text("Дополнителни податоци") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                minLines = 3,
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (title.isEmpty()) {
                        errorMsg = "Внесете име на настан."
                        return@Button
                    }
                    if (date.isEmpty()) {
                        errorMsg = "Изберете датум."
                        return@Button
                    }
                    if (time.isEmpty()) {
                        errorMsg = "Изберете време."
                        return@Button
                    }
                    val event = Event(
                        title = title,
                        date = date,
                        time = time,
                        offer = selectedOffer,
                        adultsCount = adultsCount.toIntOrNull() ?: 0,
                        childrenCount = childrenCount.toIntOrNull() ?: 0,
                        food = food,
                        notes = additionalInfo
                    )
                    eventViewModel.addEvent(event) { onBack() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelGreenPrimary)
            ) {
                Text(
                    text = "Зачувај Настан",
                    color = Color.White,
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
        colors = textFieldColors()
    )
}