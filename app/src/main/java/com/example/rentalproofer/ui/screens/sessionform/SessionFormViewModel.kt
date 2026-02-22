package com.example.rentalproofer.ui.screens.sessionform

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.RentalSession
import com.example.rentalproofer.util.getLastLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SessionFormState(
    val name: String = "",
    val company: String = "",
    val notes: String = "",
    val locationText: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFetchingLocation: Boolean = false
)

class SessionFormViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as RentalProoferApp).repository
    private val _state = MutableStateFlow(SessionFormState())
    val state: StateFlow<SessionFormState> = _state.asStateFlow()

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            repository.getSessionById(sessionId).first()?.let { session ->
                _state.value = SessionFormState(
                    name = session.name,
                    company = session.company,
                    notes = session.notes,
                    latitude = session.latitude,
                    longitude = session.longitude,
                    locationText = if (session.latitude != null && session.longitude != null)
                        "%.6f, %.6f".format(session.latitude, session.longitude) else ""
                )
            }
        }
    }

    fun updateName(name: String) { _state.value = _state.value.copy(name = name) }
    fun updateCompany(c: String) { _state.value = _state.value.copy(company = c) }
    fun updateNotes(n: String) { _state.value = _state.value.copy(notes = n) }

    fun updateLocationText(text: String) {
        val parts = text.split(",").map { it.trim() }
        val lat = parts.getOrNull(0)?.toDoubleOrNull()
        val lon = parts.getOrNull(1)?.toDoubleOrNull()
        _state.value = _state.value.copy(locationText = text, latitude = lat, longitude = lon)
    }

    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isFetchingLocation = true)
            try {
                val loc = getLastLocation(context)
                _state.value = if (loc != null) {
                    _state.value.copy(
                        isFetchingLocation = false,
                        latitude = loc.first,
                        longitude = loc.second,
                        locationText = "%.6f, %.6f".format(loc.first, loc.second)
                    )
                } else {
                    _state.value.copy(isFetchingLocation = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isFetchingLocation = false)
            }
        }
    }

    suspend fun saveSession(editingId: Long?): Long {
        val s = _state.value
        return if (editingId != null) {
            val session = RentalSession(
                id = editingId,
                name = s.name,
                company = s.company,
                notes = s.notes,
                latitude = s.latitude,
                longitude = s.longitude
            )
            repository.updateSession(session)
            editingId
        } else {
            repository.insertSession(
                RentalSession(
                    name = s.name,
                    company = s.company,
                    notes = s.notes,
                    latitude = s.latitude,
                    longitude = s.longitude
                )
            )
        }
    }
}
