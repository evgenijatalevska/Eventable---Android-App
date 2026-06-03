package com.example.eventable.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventable.data.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OfferViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadOffers()
    }

    fun loadOffers() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("offers")
                    .whereEqualTo("userId", uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _errorMessage.value = "Грешка при вчитување: ${error.localizedMessage}"
                            return@addSnapshotListener
                        }
                        val offerList = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(Offer::class.java)?.copy(id = doc.id)
                        } ?: emptyList()

                        _offers.value = offerList
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Грешка: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addOffer(title: String, content: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newOffer = Offer(userId = uid, title = title, content = content)
                firestore.collection("offers").add(newOffer).await()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Грешка при додавање: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateOffer(offerId: String, title: String, content: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedOffer = Offer(id = offerId, userId = uid, title = title, content = content)
                firestore.collection("offers")
                    .document(offerId)
                    .set(updatedOffer)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Грешка при ажурирање: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteOffer(offerId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("offers")
                    .document(offerId)
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
}