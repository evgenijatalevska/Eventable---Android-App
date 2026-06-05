package com.example.eventable.ui

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.data.Event
import com.example.eventable.ui.theme.*
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    EVENTS,
    OFFERS,
    CALENDAR,
    PROFILE
}

// 📌 Енум кој совршено одговара за менаџирање на твоите под-екрани
enum class ProfileSubScreen {
    MAIN,
    EDIT,
    LANGUAGE,
    NOTIFICATIONS
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

    // Состојба за под-екраните на профилот
    var currentProfileSubScreen by remember { mutableStateOf(ProfileSubScreen.MAIN) }

    // Состојби за Настани
    var selectedEventId by remember { mutableStateOf<String?>(null) }
    var showAddEvent by remember { mutableStateOf(false) }
    val eventViewModel: EventViewModel = viewModel()

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        eventViewModel.initLocalRepo(context)
    }
    // Состојби за Понуди
    var selectedOfferId by remember { mutableStateOf<String?>(null) }
    var showAddOffer by remember { mutableStateOf(false) }
    val offerViewModel: OfferViewModel = viewModel()

    val offers by offerViewModel.offers.collectAsStateWithLifecycle()
    val isOffersLoading by offerViewModel.isLoading.collectAsStateWithLifecycle()

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

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = PastelGreenDark) },
                    label = { Text("Настани", fontWeight = FontWeight.Medium, color = TextDark) },
                    selected = currentAppScreen == AppScreen.EVENTS,
                    onClick = {
                        currentAppScreen = AppScreen.EVENTS
                        currentProfileSubScreen = ProfileSubScreen.MAIN
                        selectedEventId = null
                        showAddEvent = false
                        selectedOfferId = null
                        showAddOffer = false
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PastelGreenPrimary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null, tint = PastelGreenDark) },
                    label = { Text("Понуди", fontWeight = FontWeight.Medium, color = TextDark) },
                    selected = currentAppScreen == AppScreen.OFFERS,
                    onClick = {
                        currentAppScreen = AppScreen.OFFERS
                        currentProfileSubScreen = ProfileSubScreen.MAIN
                        selectedEventId = null
                        showAddEvent = false
                        selectedOfferId = null
                        showAddOffer = false
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PastelGreenPrimary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PastelGreenDark) },
                    label = { Text("Календар", fontWeight = FontWeight.Medium, color = TextDark) },
                    selected = currentAppScreen == AppScreen.CALENDAR,
                    onClick = {
                        currentAppScreen = AppScreen.CALENDAR
                        currentProfileSubScreen = ProfileSubScreen.MAIN
                        selectedEventId = null
                        showAddEvent = false
                        selectedOfferId = null
                        showAddOffer = false
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
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                    label = { Text("Одјави се", fontWeight = FontWeight.Medium, color = Color.Red) },
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
                // ⚠️ КРИТИЧНО МЕСТO: Го прикажуваме заглавието САМО ако сме на главниот екран на профилот.
                // Ако се отвори некој од под-екраните, тие си имаат свој TopAppBar и овој тука се крие.
                if (currentAppScreen != AppScreen.PROFILE || currentProfileSubScreen == ProfileSubScreen.MAIN) {
                    TopAppBar(
                        title = {
                            Text(
                                text = when {
                                    showAddEvent -> "Нов Настан"
                                    showAddOffer -> "Нова Понуда"
                                    selectedEventId != null -> "Детали"
                                    selectedOfferId != null -> "Понуда"
                                    else -> ""
                                },
                                fontWeight = FontWeight.ExtraBold,
                                color = PastelGreenDark
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = BackgroundWhite,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Мени",
                                tint = if (drawerState.isOpen) PastelGreenDark else TextDark.copy(alpha = 0.5f)
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
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (currentAppScreen == AppScreen.HOME && !showAddEvent && selectedEventId == null && !showAddOffer && selectedOfferId == null)
                                    PastelGreenDark else TextDark.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        selected = currentAppScreen == AppScreen.HOME && !showAddEvent && selectedEventId == null && !showAddOffer && selectedOfferId == null,
                        onClick = {
                            currentAppScreen = AppScreen.HOME
                            currentProfileSubScreen = ProfileSubScreen.MAIN
                            selectedEventId = null
                            showAddEvent = false
                            selectedOfferId = null
                            showAddOffer = false
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PastelGreenDark,
                            selectedTextColor = PastelGreenDark,
                            indicatorColor = PastelGreenPrimary.copy(alpha = 0.15f)
                        )
                    )
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
                        onClick = {
                            currentAppScreen = AppScreen.PROFILE
                            currentProfileSubScreen = ProfileSubScreen.MAIN // Секогаш ресетирај на главниот приказ кога ќе кликнат на табот
                            selectedEventId = null
                            showAddEvent = false
                            selectedOfferId = null
                            showAddOffer = false
                        },
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
                when {
                    // --- НАВИГАЦИЈА ЗА НАСТАНИ ---
                    showAddEvent -> {
                        AddEventScreen(
                            eventViewModel = eventViewModel,
                            onBack = { showAddEvent = false }
                        )
                    }
                    selectedEventId != null -> {
                        EventDetailScreen(
                            eventId = selectedEventId!!,
                            eventViewModel = eventViewModel,
                            onBack = { selectedEventId = null }
                        )
                    }

                    // --- НАВИГАЦИЈА ЗА ПОНУДИ ---
                    showAddOffer -> {
                        AddOfferScreen(
                            onSave = { title, content ->
                                offerViewModel.addOffer(title, content) { showAddOffer = false }
                            },
                            onBack = { showAddOffer = false }
                        )
                    }
                    selectedOfferId != null -> {
                        val currentOffer = offers.find { it.id == selectedOfferId }
                        OfferDetailScreen(
                            offer = currentOffer,
                            onUpdate = { updatedTitle, updatedContent ->
                                offerViewModel.updateOffer(selectedOfferId!!, updatedTitle, updatedContent) { }
                            },
                            onDelete = {
                                offerViewModel.deleteOffer(selectedOfferId!!) { selectedOfferId = null }
                            },
                            onBack = { selectedOfferId = null }
                        )
                    }

                    // --- ГЛАВНИ ЕКРАНИ ---
                    else -> when (currentAppScreen) {
                        AppScreen.HOME -> HomeContent(eventViewModel = eventViewModel)
                        AppScreen.EVENTS -> EventsScreen(
                            eventViewModel = eventViewModel,
                            onEventClick = { id -> selectedEventId = id },
                            onAddEventClick = { showAddEvent = true },
                            onCalendarClick = { currentAppScreen = AppScreen.CALENDAR }
                        )
                        AppScreen.OFFERS -> OffersScreen(
                            offers = offers,
                            isLoading = isOffersLoading,
                            onOfferClick = { id -> selectedOfferId = id },
                            onAddOfferClick = { showAddOffer = true }
                        )
                        AppScreen.CALENDAR -> CalendarScreen(eventViewModel = eventViewModel)

                        // --- 👤 НАВИГАЦИЈА НИЗ ПРОФИЛОТ (Поврзано со ProfileScreen.kt) ---
                        AppScreen.PROFILE -> when (currentProfileSubScreen) {
                            ProfileSubScreen.MAIN -> {
                                ProfileScreen(
                                    authViewModel = viewModel,
                                    onEditProfileClick = { currentProfileSubScreen = ProfileSubScreen.EDIT },
                                    onLanguageClick = { currentProfileSubScreen = ProfileSubScreen.LANGUAGE },
                                    onNotificationsClick = { currentProfileSubScreen = ProfileSubScreen.NOTIFICATIONS },
                                    onLogoutSuccess = onLogoutSuccess
                                )
                            }
                            ProfileSubScreen.EDIT -> {
                                EditProfileScreen(
                                    authViewModel = viewModel, // Го проследуваме viewModel за да има пристап до податоците
                                    onBack = { currentProfileSubScreen = ProfileSubScreen.MAIN }
                                )
                            }
                            ProfileSubScreen.LANGUAGE -> LanguageScreen(onBack = { currentProfileSubScreen = ProfileSubScreen.MAIN })
                            ProfileSubScreen.NOTIFICATIONS -> NotificationsScreen(onBack = { currentProfileSubScreen = ProfileSubScreen.MAIN })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(eventViewModel: EventViewModel = viewModel()) {
    val events by eventViewModel.events.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Здраво! 👋", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(
                text = "Еве ги твоите претстојни настани.",
                fontSize = 14.sp,
                color = TextDark.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            Text(text = "Претстојни Настани", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextDark, modifier = Modifier.padding(bottom = 4.dp))
        }
        if (events.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Нема настани сè уште", color = TextDark.copy(alpha = 0.4f), fontSize = 15.sp)
                }
            }
        } else {
            items(events) { event ->
                HomeEventCard(event = event)
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun HomeEventCard(event: Event) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PastelGreenPrimary.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            if (event.location.isNotEmpty()) {
                Text(text = "📍 ${event.location}", fontSize = 14.sp, color = TextDark.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "📅 ${event.date}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PastelGreenDark)
                Text(text = "🕒 ${event.time}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PastelGreenDark)
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PastelGreenPrimary.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "📍 ${event.location}", fontSize = 14.sp, color = TextDark.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "📅 ${event.date}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PastelGreenDark)
                Text(text = "🕒 ${event.time}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PastelGreenDark)
            }
        }
    }
}