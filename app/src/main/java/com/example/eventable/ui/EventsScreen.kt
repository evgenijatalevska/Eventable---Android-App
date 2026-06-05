package com.example.eventable.ui

import android.os.Bundle // ДОДАДЕНО ЗА ANALYTICS
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // ДОДАДЕНО ЗА ANALYTICS
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.R
import com.example.eventable.data.Event
import com.example.eventable.ui.theme.*
import com.google.firebase.analytics.FirebaseAnalytics // ДОДАДЕНО ЗА ANALYTICS

@Composable
fun EventsScreen(
    eventViewModel: EventViewModel = viewModel(),
    onEventClick: (String) -> Unit,
    onAddEventClick: () -> Unit,
    onCalendarClick: () -> Unit = {}
) {
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    val isLoading by eventViewModel.isLoading.collectAsStateWithLifecycle()

    // ДОДАДЕНО ЗА ANALYTICS
    val context = LocalContext.current
    val analytics = remember { FirebaseAnalytics.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- ХЕДЕР СЕКЦИЈА СО НАСЛОВ, КАЛЕНДАР И ПРЕКЛОПЕНА КАРТИЧКА ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
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
                                            PastelGreenDark.copy(alpha = 0.6f),
                                            PastelGreenPrimary.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )

                        // ПОПРАВКА: Спуштен наслов „Настани“ (променето од top = 20.dp во 40.dp)
                        Text(
                            text = "Настани",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 20.dp, top = 40.dp)
                        )

                        // Копче за Календар — Подигнато погоре за подобар преглед
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 75.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    // ДОДАДЕНО ЗА ANALYTICS
                                    analytics.logEvent("open_calendar_click", null)
                                    onCalendarClick()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.25f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Отвори Календар",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Првата картичка се позиционира ПОЛА на слика, ПОЛА на бело
                    if (events.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(top = 180.dp)
                        ) {
                            EventCardWhite(
                                event = events.first(),
                                onClick = {
                                    // ДОДАДЕНО ЗА ANALYTICS
                                    val bundle = Bundle().apply { putString("event_id", events.first().id) }
                                    analytics.logEvent("view_event_detail", bundle)
                                    onEventClick(events.first().id)
                                }
                            )
                        }
                    }
                }
            }

            // --- ОСТАНАТИ ЕЛЕМЕНТИ ОД ЛИСТАТА ---
            if (events.isNotEmpty()) {
                items(events.drop(1)) { event ->
                    EventCardWhite(
                        event = event,
                        onClick = {
                            // ДОДАДЕНО ЗА ANALYTICS
                            val bundle = Bundle().apply { putString("event_id", event.id) }
                            analytics.logEvent("view_event_detail", bundle)
                            onEventClick(event.id)
                        }
                    )
                }
            } else if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PastelGreenDark)
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нема настани сè уште",
                            fontSize = 16.sp,
                            color = TextDark.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Притисни + за да додадеш настан",
                            fontSize = 13.sp,
                            color = TextDark.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Пловечко копче за додавање настан
        FloatingActionButton(
            onClick = {
                // ДОДАДЕНО ЗА ANALYTICS
                analytics.logEvent("add_event_fab_click", null)
                onAddEventClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PastelGreenPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Додај настан")
        }
    }
}

@Composable
fun EventCardWhite(
    event: Event,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = event.title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Поголем датум и час со икони
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Датум
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = PastelGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.date,
                        fontSize = 15.sp,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(TextDark.copy(alpha = 0.15f))
                )

                // Час
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = PastelGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.time,
                        fontSize = 15.sp,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (event.offer.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Понуда: ${event.offer}",
                    fontSize = 13.sp,
                    color = TextDark.copy(alpha = 0.5f)
                )
            }
        }
    }
}