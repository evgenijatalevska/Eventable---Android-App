package com.example.eventable.data

import kotlinx.coroutines.flow.Flow

class LocalRepository(private val eventDao: EventDao) {

    fun getEvents(userId: String): Flow<List<EventEntity>> {
        return eventDao.getEventsByUser(userId)
    }

    suspend fun saveEvent(event: EventEntity) {
        eventDao.insertEvent(event)
    }

    suspend fun saveAllEvents(events: List<EventEntity>) {
        eventDao.insertAll(events)
    }

    suspend fun deleteEvent(eventId: String) {
        eventDao.deleteEventById(eventId)
    }

    suspend fun deleteAllEvents(userId: String) {
        eventDao.deleteAllByUser(userId)
    }
}