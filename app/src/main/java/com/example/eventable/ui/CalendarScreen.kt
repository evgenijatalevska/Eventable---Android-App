package com.example.eventable.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.R
import com.example.eventable.data.Event
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    eventViewModel: EventViewModel = viewModel(),
    onEventClick: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val today = Calendar.getInstance()
    var currentYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val monthNames = listOf(
        stringResource(id = R.string.month_january),
        stringResource(id = R.string.month_february),
        stringResource(id = R.string.month_march),
        stringResource(id = R.string.month_april),
        stringResource(id = R.string.month_may),
        stringResource(id = R.string.month_june),
        stringResource(id = R.string.month_july),
        stringResource(id = R.string.month_august),
        stringResource(id = R.string.month_september),
        stringResource(id = R.string.month_october),
        stringResource(id = R.string.month_november),
        stringResource(id = R.string.month_december)
    )

    val selectedDateStr = "%02d.%02d.%04d".format(selectedDay, currentMonth + 1, currentYear)
    val eventsForSelectedDay = events.filter { it.date == selectedDateStr }

    val daysWithEvents = events.mapNotNull { event ->
        val parts = event.date.split(".")
        if (parts.size == 3) {
            val day = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()?.minus(1)
            val year = parts[2].toIntOrNull()
            if (month == currentMonth && year == currentYear) day else null
        } else null
    }.toSet()

    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val totalCells = firstDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // Header слика
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.header_height_calendar))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.events_header),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )
                    Text(
                        text = stringResource(id = R.string.calendar),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 20.dp, top = 60.dp)
                    )
                }
            }

            // Календар картичка над сликата
            item {
                Box(modifier = Modifier.offset(y = (-50).dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    if (currentMonth == 0) {
                                        currentMonth = 11
                                        currentYear--
                                    } else currentMonth--
                                }) {
                                    Icon(
                                        Icons.Default.ArrowBackIosNew,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "${monthNames[currentMonth]} $currentYear",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = {
                                    if (currentMonth == 11) {
                                        currentMonth = 0
                                        currentYear++
                                    } else currentMonth++
                                }) {
                                    Icon(
                                        Icons.Default.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(
                                    stringResource(id = R.string.weekday_mon),
                                    stringResource(id = R.string.weekday_tue),
                                    stringResource(id = R.string.weekday_wed),
                                    stringResource(id = R.string.weekday_thu),
                                    stringResource(id = R.string.weekday_fri),
                                    stringResource(id = R.string.weekday_sat),
                                    stringResource(id = R.string.weekday_sun)
                                ).forEach { day ->
                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            for (row in 0 until rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (col in 0 until 7) {
                                        val cellIndex = row * 7 + col
                                        val dayNum = cellIndex - firstDayOfWeek + 1

                                        if (dayNum < 1 || dayNum > daysInMonth) {
                                            Box(modifier = Modifier.weight(1f).height(40.dp))
                                        } else {
                                            val isToday = dayNum == today.get(Calendar.DAY_OF_MONTH) &&
                                                    currentMonth == today.get(Calendar.MONTH) &&
                                                    currentYear == today.get(Calendar.YEAR)
                                            val isSelected = dayNum == selectedDay
                                            val hasEvent = dayNum in daysWithEvents

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .padding(2.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            isSelected -> MaterialTheme.colorScheme.primary
                                                            isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                            else -> Color.Transparent
                                                        }
                                                    )
                                                    .clickable { selectedDay = dayNum },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = dayNum.toString(),
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                        color = when {
                                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                                            isToday -> MaterialTheme.colorScheme.secondary
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                    if (hasEvent) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                                    else MaterialTheme.colorScheme.secondary
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height((-42).dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (eventsForSelectedDay.isEmpty())
                            stringResource(id = R.string.no_events_for_date, selectedDateStr)
                        else
                            stringResource(id = R.string.events_for_date, selectedDateStr),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (eventsForSelectedDay.isEmpty())
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            items(eventsForSelectedDay) { event ->
                CalendarEventCard(
                    event = event,
                    onEventClick = { onEventClick(event.id) },
                    onAddToGoogleCalendar = { addEventToGoogleCalendar(context, event) },
                    onMarkAdded = { eventViewModel.markAddedToCalendar(event.id) },
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.screen_horizontal_padding), vertical = 4.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB копче
        FloatingActionButton(
            onClick = onAddEventClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_event))
        }
    }
}

@Composable
fun CalendarEventCard(
    event: Event,
    onEventClick: () -> Unit = {},
    onAddToGoogleCalendar: () -> Unit,
    onMarkAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onEventClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.time,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )
            if (event.offer.isNotEmpty()) {
                Text(
                    text = event.offer,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (event.addedToCalendar) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.already_added_google_calendar),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                TextButton(
                    onClick = {
                        onAddToGoogleCalendar()
                        onMarkAdded()
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.add_to_google_calendar),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun addEventToGoogleCalendar(context: Context, event: Event) {
    try {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val startDate = dateFormat.parse("${event.date} ${event.time}")
        val startMillis = startDate?.time ?: System.currentTimeMillis()
        val endMillis = startMillis + 2 * 60 * 60 * 1000

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                buildString {
                    if (event.offer.isNotEmpty()) append(context.getString(R.string.offer_prefixed, event.offer) + "\n")
                    if (event.food.isNotEmpty()) append(context.getString(R.string.food_prefixed, event.food) + "\n")
                    if (event.notes.isNotEmpty()) append(context.getString(R.string.notes_prefixed, event.notes))
                }
            )
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.HAS_ALARM, 1)
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        val uri = Uri.parse("https://calendar.google.com")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}