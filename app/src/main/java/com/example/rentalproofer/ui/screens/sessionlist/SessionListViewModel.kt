package com.example.rentalproofer.ui.screens.sessionlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.RentalSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as RentalProoferApp).repository

    val sessions: StateFlow<List<RentalSession>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.deleteExpiredSessions() }
    }

    fun deleteSession(session: RentalSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
