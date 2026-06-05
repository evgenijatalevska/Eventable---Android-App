package com.example.eventable.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventable.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventViewModel : ViewModel() {

    private var localRepository: LocalRepository? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 📌 UI сега зависи САМО од Room
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 🚀 Го тргнавме init блокот од тука бидејќи localRepository тука е сè уште null!

    fun initLocalRepo(context: Context) {
        // 1. Прво го иницијализираме репозиториумот за да НЕ биде null
        val db = AppDatabase.getDatabase(context)
        localRepository = LocalRepository(db.eventDao())

        // 2. Дури СЕГА, откако имаме сигурна база, ги стартуваме синхронизацијата и слушањето
        val uid = auth.currentUser?.uid
        if (uid != null) {
            observeEventsFromRoom(uid)
            syncFromFirestore(uid)
        }
    }

    // -----------------------------------------------------
    // 📌 ROOM = SOURCE OF TRUTH FOR UI
    // -----------------------------------------------------
    private fun observeEventsFromRoom(userId: String) {
        viewModelScope.launch {
            localRepository?.getEvents(userId)?.collectLatest { list ->
                _events.value = list.map { it.toEvent() }
            }
        }
    }

    // -----------------------------------------------------
    // 📌 FIRESTORE = SYNC LAYER ONLY
    // -----------------------------------------------------
    private fun syncFromFirestore(uid: String) {
        firestore.collection("events")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    _errorMessage.value = "Sync error: ${error.localizedMessage}"
                    return@addSnapshotListener
                }

                val eventList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                // Складирање во Room (ова автоматски го ажурира UI-от преку Flow)
                viewModelScope.launch {
                    localRepository?.saveAllEvents(eventList.map { it.toEntity() })
                }
            }
    }

    // -----------------------------------------------------
    // 📌 ADD EVENT (Сега работи 100% со твојот AddEventScreen)
    // -----------------------------------------------------
    fun addEvent(event: Event, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newEvent = event.copy(userId = uid)

                val docRef = firestore.collection("events")
                    .add(newEvent)
                    .await()

                val savedEvent = newEvent.copy(id = docRef.id)

                // Запишување во Room базата (UI веднаш се освежува)
                localRepository?.saveEvent(savedEvent.toEntity())

                onSuccess()

            } catch (e: Exception) {
                _errorMessage.value = "Error adding event: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // -----------------------------------------------------
    // 📌 UPDATE EVENT
    // -----------------------------------------------------
    fun updateEvent(event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("events")
                    .document(event.id)
                    .set(event)
                    .await()

                localRepository?.saveEvent(event.toEntity())
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error updating event: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // -----------------------------------------------------
    // 📌 DELETE EVENT
    // -----------------------------------------------------
    fun deleteEvent(eventId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("events")
                    .document(eventId)
                    .delete()
                    .await()

                localRepository?.deleteEvent(eventId)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting event: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // -----------------------------------------------------
    // 📌 OPTIONAL FLAG UPDATE
    // -----------------------------------------------------
    fun markAddedToCalendar(eventId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("events")
                    .document(eventId)
                    .update("addedToCalendar", true)
                    .await()
            } catch (_: Exception) {}
        }
    }
}