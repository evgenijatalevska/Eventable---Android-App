package com.example.eventable.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventable.R
import com.example.eventable.data.Event
import com.example.eventable.data.Offer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

private fun parseDateForSorting(dateStr: String): Long {
    val formats = listOf("dd.MM.yyyy", "yyyy-MM-dd", "d.M.yyyy", "dd/MM/yyyy")
    for (fmt in formats) {
        try {
            SimpleDateFormat(fmt, Locale.getDefault()).parse(dateStr)?.time?.let { return it }
        } catch (e: Exception) {
            // try next format
        }
    }
    return Long.MAX_VALUE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // На пошироки екрани (таблети, екстендирана ширина) ја прикажуваме навигацијата
    // како странична NavigationRail наместо долна NavigationBar.
    val windowSizeClass = LocalWindowSizeClass.current
    val useNavigationRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.COMPACT

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
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                Text(
                    text = stringResource(id = R.string.nav_menu),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                    label = { Text(stringResource(id = R.string.events), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) },
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
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                    label = { Text(stringResource(id = R.string.offers), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) },
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
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                    label = { Text(stringResource(id = R.string.calendar), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) },
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
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                // ⚠️ КРИТИЧНО МЕСТО: Го прикажуваме заглавието САМО ако сме на главниот екран на профилот.
                // Ако се отвори некој од под-екраните, тие си имаат свој TopAppBar и овој тука се крие.
                if (currentAppScreen != AppScreen.PROFILE || currentProfileSubScreen == ProfileSubScreen.MAIN) {
                    TopAppBar(
                        title = {
                            when {
                                showAddEvent -> Text(
                                    stringResource(id = R.string.new_event),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                showAddOffer -> Text(
                                    stringResource(id = R.string.new_offer),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                selectedEventId != null -> Text(
                                    stringResource(id = R.string.details),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                selectedOfferId != null -> Text(
                                    stringResource(id = R.string.offer_label),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                currentAppScreen == AppScreen.HOME -> Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.app_name),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                else -> { /* EVENTS, OFFERS, CALENDAR screens have their own visual headers */ }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                    )
                }
            },
            bottomBar = {
                if (!useNavigationRail) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.nav_menu),
                                tint = if (drawerState.isOpen) MaterialTheme.colorScheme.secondary
                                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text(stringResource(id = R.string.nav_menu), fontSize = 11.sp) },
                        selected = drawerState.isOpen,
                        onClick = {
                            scope.launch {
                                if (drawerState.isOpen) drawerState.close() else drawerState.open()
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = stringResource(id = R.string.home),
                                tint = if (currentAppScreen == AppScreen.HOME && !showAddEvent && selectedEventId == null && !showAddOffer && selectedOfferId == null)
                                    MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text(stringResource(id = R.string.home), fontSize = 11.sp) },
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
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = stringResource(id = R.string.profile),
                                tint = if (currentAppScreen == AppScreen.PROFILE) MaterialTheme.colorScheme.secondary
                                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        label = { Text(stringResource(id = R.string.profile), fontSize = 11.sp) },
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
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    )
                }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = stringResource(id = R.string.nav_menu),
                                    tint = if (drawerState.isOpen) MaterialTheme.colorScheme.secondary
                                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            },
                            label = { Text(stringResource(id = R.string.nav_menu), fontSize = 11.sp) },
                            selected = drawerState.isOpen,
                            onClick = {
                                scope.launch {
                                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = stringResource(id = R.string.home),
                                    tint = if (currentAppScreen == AppScreen.HOME && !showAddEvent && selectedEventId == null && !showAddOffer && selectedOfferId == null)
                                        MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            },
                            label = { Text(stringResource(id = R.string.home), fontSize = 11.sp) },
                            selected = currentAppScreen == AppScreen.HOME && !showAddEvent && selectedEventId == null && !showAddOffer && selectedOfferId == null,
                            onClick = {
                                currentAppScreen = AppScreen.HOME
                                currentProfileSubScreen = ProfileSubScreen.MAIN
                                selectedEventId = null
                                showAddEvent = false
                                selectedOfferId = null
                                showAddOffer = false
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = stringResource(id = R.string.profile),
                                    tint = if (currentAppScreen == AppScreen.PROFILE) MaterialTheme.colorScheme.secondary
                                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            },
                            label = { Text(stringResource(id = R.string.profile), fontSize = 11.sp) },
                            selected = currentAppScreen == AppScreen.PROFILE,
                            onClick = {
                                currentAppScreen = AppScreen.PROFILE
                                currentProfileSubScreen = ProfileSubScreen.MAIN
                                selectedEventId = null
                                showAddEvent = false
                                selectedOfferId = null
                                showAddOffer = false
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
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
                        AppScreen.HOME -> HomeContent(
                            authViewModel = viewModel,
                            eventViewModel = eventViewModel,
                            offers = offers,
                            onEventClick = { id -> selectedEventId = id },
                            onAddEventClick = { showAddEvent = true },
                            onCalendarClick = { currentAppScreen = AppScreen.CALENDAR },
                            onOfferClick = { id -> selectedOfferId = id },
                            onAddOfferClick = { showAddOffer = true }
                        )
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
                        AppScreen.CALENDAR -> CalendarScreen(
                            eventViewModel = eventViewModel,
                            onEventClick = { id -> selectedEventId = id },
                            onAddEventClick = { showAddEvent = true }
                        )

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
                                    authViewModel = viewModel,
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
}

// =============================================================================
// HOME CONTENT — Welcome header + 2×2 upcoming-events grid
// =============================================================================

@Composable
fun HomeContent(
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel = viewModel(),
    offers: List<Offer>,
    onEventClick: (String) -> Unit,
    onAddEventClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onOfferClick: (String) -> Unit,
    onAddOfferClick: () -> Unit
) {
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    val firstName by remember { authViewModel.firstNameState }

    val todayMs = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val upcomingEvents = remember(events) {
        events
            .filter { parseDateForSorting(it.date).let { ms -> ms >= todayMs || ms == Long.MAX_VALUE } }
            .sortedBy { parseDateForSorting(it.date) }
            .take(3)
    }

    // Grid slots: up to 3 event cards followed by the add-button card (null sentinel)
    val slots = buildList<Event?> {
        addAll(upcomingEvents)
        add(null)
    }
    val rows = slots.chunked(2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensionResource(id = R.dimen.home_horizontal_padding))
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Welcome section
        Text(
            text = stringResource(id = R.string.welcome_greeting),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = firstName.ifEmpty { stringResource(id = R.string.default_user) },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(id = R.string.upcoming_events),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2×2 grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { item ->
                        if (item != null) {
                            EventGridCard(
                                event = item,
                                onClick = { onEventClick(item.id) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            AddEventGridCard(
                                onClick = onAddEventClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    // Pad with an invisible box when the last row has only 1 item
                    if (row.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCalendarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.calendar), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(id = R.string.offers),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal row sized so exactly 3 cards fit within the screen width before scrolling
        BoxWithConstraints {
            val cardSpacing = 12.dp
            val cardWidth = (maxWidth - cardSpacing * 2) / 3

            LazyRow(horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
                items(offers) { offer ->
                    OfferGridCard(
                        offer = offer,
                        onClick = { onOfferClick(offer.id) },
                        modifier = Modifier.width(cardWidth)
                    )
                }
                item {
                    AddOfferGridCard(
                        onClick = onAddOfferClick,
                        modifier = Modifier.width(cardWidth)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// =============================================================================
// EVENT GRID CARD — clickable square card showing title, date, time
// =============================================================================

@Composable
fun EventGridCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.date,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (event.time.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.time,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// ADD EVENT GRID CARD — 4th slot; styled like a primary action card
// =============================================================================

@Composable
fun AddEventGridCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_event),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.add_event),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =============================================================================
// OFFER GRID CARD — clickable square card showing only the offer title
// =============================================================================

@Composable
fun OfferGridCard(
    offer: Offer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = offer.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =============================================================================
// ADD OFFER GRID CARD — last slot; styled like a primary action card
// =============================================================================

@Composable
fun AddOfferGridCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_offer),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.add_offer),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
