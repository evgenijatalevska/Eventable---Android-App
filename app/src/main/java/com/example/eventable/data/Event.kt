package com.example.eventable.data

data class Event(
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
    val userId: String = ""
)