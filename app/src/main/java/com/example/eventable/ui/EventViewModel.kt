package com.example.eventable.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventable.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class EventViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadEvents()
    }

    fun loadEvents() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("events")
                    .whereEqualTo("userId", uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _errorMessage.value = "Грешка при вчитување: ${error.localizedMessage}"
                            return@addSnapshotListener
                        }
                        val eventList = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(Event::class.java)?.copy(id = doc.id)
                        } ?: emptyList()

                        // Сортирање: Најблиските настани временски да бидат најгоре
                        val dateTimeFormat =
                            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        _events.value = eventList.sortedWith { e1, e2 ->
                            try {
                                val d1 = dateTimeFormat.parse("${e1.date} ${e1.time}")
                                val d2 = dateTimeFormat.parse("${e2.date} ${e2.time}")
                                d1?.compareTo(d2) ?: 0
                            } catch (e: Exception) {
                                0
                            }
                        }
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Грешка: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addEvent(event: Event, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newEvent = event.copy(userId = uid)
                firestore.collection("events").add(newEvent).await()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Грешка при додавање: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("events")
                    .document(event.id)
                    .set(event)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Грешка при ажурирање: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(eventId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("events")
                    .document(eventId)
                    .delete()
                    .await()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Грешка при бришење: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun markAddedToCalendar(eventId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("events")
                    .document(eventId)
                    .update("addedToCalendar", true)
                    .await()
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}