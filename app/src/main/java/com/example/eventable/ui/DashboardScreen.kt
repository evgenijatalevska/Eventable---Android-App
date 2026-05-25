package com.example.eventable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.data.Event
import com.example.eventable.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    // Лажни (Mock) податоци за почеток, додека не поврземе Firestore база
    val dummyEvents = listOf(
        Event("1", "7-ми Роденден на Марко", "Игротека 'Бајка'", "26 Мај, 2026", "18:00", PastelGreenPrimary.copy(alpha = 0.2f)),
        Event("2", "Крштевка и 1-ви Роденден", "Игротека 'Ѕвездички'", "30 Мај, 2026", "12:30", Color(0xFFE3F2FD)), // Светло сина
        Event("3", "Тинејџерска забава - Ема", "Лаунџ Бар Тренд", "05 Јуни, 2026", "20:00", Color(0xFFF3E5F5)) // Светло виолетова
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Eventable", fontWeight = FontWeight.ExtraBold, color = PastelGreenDark)
                },
                actions = {
                    // Икона за одјава во горниот десен агол
                    IconButton(onClick = {
                        viewModel.signOut()
                        onLogoutSuccess()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Одјави се",
                            tint = Color.Red
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
                .padding(horizontal = 20.dp)
        ) {
            // Поздравен дел
            Text(
                text = "Здраво! 👋",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Еве ги твоите претстојни настани и игротеки.",
                fontSize = 14.sp,
                color = TextDark.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Наслов за листата
            Text(
                text = "Претстојни Настани",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Листа со настани (Слично на RecyclerView, но во Compose)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dummyEvents) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = event.cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📍 Локација: ${event.location}",
                fontSize = 14.sp,
                color = TextDark.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📅 ${event.date}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PastelGreenDark
                )
                Text(
                    text = "🕒 ${event.time}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PastelGreenDark
                )
            }
        }
    }
}