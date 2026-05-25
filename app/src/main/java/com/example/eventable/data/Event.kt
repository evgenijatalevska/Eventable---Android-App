package com.example.eventable.data

import androidx.compose.ui.graphics.Color

data class Event(
    val id: String,
    val title: String,
    val location: String,
    val date: String,
    val time: String,
    val cardColor: Color
)