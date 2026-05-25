package com.example.eventable.data

data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val companyName: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)