package com.example.eventable.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val offer: String = "",
    val adultsCount: Int = 0,
    val childrenCount: Int = 0,
    val food: String = "",
    val notes: String = "",
    val userId: String = "",
    val addedToCalendar: Boolean = false
)