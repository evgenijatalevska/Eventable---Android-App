package com.example.eventable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventable.data.Event
import com.example.eventable.ui.theme.*
import kotlinx.coroutines.launch

// Екрани во навигацијата
enum class AppScreen {
    HOME,
    EVENTS,
    OFFERS,
    CALENDAR,
    PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentAppScreen by remember { mutableStateOf(AppScreen.HOME) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                windowInsets = WindowInsets(0),
                drawerContainerColor = BackgroundWhite
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Eventable",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PastelGreenDark,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Text(
                    text = "Главно мени",
                    fontSize = 12.sp,
                    color = TextDark.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = TextDark.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))

                // Настани
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = PastelGreenDark
                        )
                    },
                    label = {
                        Text(
                            "Настани",
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                    },
                    selected = currentAppScreen == AppScreen.EVENTS,
                    onClick = {
                        currentAppScreen = AppScreen.EVENTS
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PastelGreenPrimary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Понуди
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = PastelGreenDark
                        )
                    },
                    label = {
                        Text(
                            "Понуди",
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                    },
                    selected = currentAppScreen == AppScreen.OFFERS,
                    onClick = {
                        currentAppScreen = AppScreen.OFFERS
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PastelGreenPrimary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Календар
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PastelGreenDark
                        )
                    },
                    label = {
                        Text(
                            "Календар",
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                    },
                    selected = currentAppScreen == AppScreen.CALENDAR,
                    onClick = {
                        currentAppScreen = AppScreen.CALENDAR
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PastelGreenPrimary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = TextDark.copy(alpha = 0.1f))

                // Одјава
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    },
                    label = {
                        Text(
                            "Одјави се",
                            fontWeight = FontWeight.Medium,
                            color = Color.Red
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.signOut()
                        onLogoutSuccess()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentAppScreen) {
                                AppScreen.HOME -> "Eventable"
                                AppScreen.EVENTS -> "Настани"
                                AppScreen.OFFERS -> "Понуди"
                                AppScreen.CALENDAR -> "Календар"
                                AppScreen.PROFILE -> "Профил"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            color = PastelGreenDark
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundWhite
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = BackgroundWhite,
                    tonalElevation = 8.dp
                ) {
                    // Лево — Мени
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Мени",
                                tint = if (drawerState.isOpen) PastelGreenDark
                                else TextDark.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text("Мени", fontSize = 11.sp) },
                        selected = drawerState.isOpen,
                        onClick = {
                            scope.launch {
                                if (drawerState.isOpen) drawerState.close()
                                else drawerState.open()
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PastelGreenDark,
                            selectedTextColor = PastelGreenDark,
                            indicatorColor = PastelGreenPrimary.copy(alpha = 0.15f)
                        )
                    )

                    // Средина — Home
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (currentAppScreen == AppScreen.HOME) PastelGreenDark
                                else TextDark.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        selected = currentAppScreen == AppScreen.HOME,
                        onClick = { currentAppScreen = AppScreen.HOME },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PastelGreenDark,
                            selectedTextColor = PastelGreenDark,
                            indicatorColor = PastelGreenPrimary.copy(alpha = 0.15f)
                        )
                    )

                    // Десно — Профил
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Профил",
                                tint = if (currentAppScreen == AppScreen.PROFILE) PastelGreenDark
                                else TextDark.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text("Профил", fontSize = 11.sp) },
                        selected = currentAppScreen == AppScreen.PROFILE,
                        onClick = { currentAppScreen = AppScreen.PROFILE },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PastelGreenDark,
                            selectedTextColor = PastelGreenDark,
                            indicatorColor = PastelGreenPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            },
            containerColor = BackgroundWhite
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentAppScreen) {
                    AppScreen.HOME -> HomeContent()
                    AppScreen.EVENTS -> EventsPlaceholder()
                    AppScreen.OFFERS -> OffersPlaceholder()
                    AppScreen.CALENDAR -> CalendarPlaceholder()
                    AppScreen.PROFILE -> ProfilePlaceholder(
                        viewModel = viewModel,
                        onLogoutSuccess = onLogoutSuccess
                    )
                }
            }
        }
    }
}

// ---- HOME CONTENT ----
@Composable
fun HomeContent() {
    val dummyEvents = listOf(
        Event("1", "7-ми Роденден на Марко", "Игротека 'Бајка'", "26 Мај, 2026", "18:00", PastelGreenPrimary.copy(alpha = 0.2f)),
        Event("2", "Крштевка и 1-ви Роденден", "Игротека 'Ѕвездички'", "30 Мај, 2026", "12:30", Color(0xFFE3F2FD)),
        Event("3", "Тинејџерска забава - Ема", "Лаунџ Бар Тренд", "05 Јуни, 2026", "20:00", Color(0xFFF3E5F5))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Здраво! 👋",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Еве ги твоите претстојни настани.",
                fontSize = 14.sp,
                color = TextDark.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            Text(
                text = "Претстојни Настани",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(dummyEvents) { event ->
            EventCard(event = event)
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ---- PLACEHOLDER ЕКРАНИ ----
@Composable
fun EventsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Настани — наскоро", color = TextDark.copy(alpha = 0.4f), fontSize = 16.sp)
    }
}

@Composable
fun OffersPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Понуди — наскоро", color = TextDark.copy(alpha = 0.4f), fontSize = 16.sp)
    }
}

@Composable
fun CalendarPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Календар — наскоро", color = TextDark.copy(alpha = 0.4f), fontSize = 16.sp)
    }
}

@Composable
fun ProfilePlaceholder(
    viewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Профил — наскоро", color = TextDark.copy(alpha = 0.4f), fontSize = 16.sp)
    }
}

// ---- EVENT CARD ----
@Composable
fun EventCard(event: Event) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = event.cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📍 ${event.location}",
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