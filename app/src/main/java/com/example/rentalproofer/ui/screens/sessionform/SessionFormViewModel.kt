package com.example.rentalproofer.ui.screens.sessionform

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.DeletionPeriod
import com.example.rentalproofer.data.model.RentalSession
import com.example.rentalproofer.util.getLastLocation
import com.example.rentalproofer.util.reverseGeocode
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
    val address: String? = null,
    val beforeDate: Long? = null,
    val afterDate: Long? = null,
    val isFetchingLocation: Boolean = false,
    val deletionPeriod: DeletionPeriod = DeletionPeriod.SIX_MONTHS
)

class SessionFormViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as RentalProoferApp
    private val repository = app.repository
    private val settingsManager = app.settingsManager
    private val _state = MutableStateFlow(SessionFormState())
    val state: StateFlow<SessionFormState> = _state.asStateFlow()

    fun loadDefaults() {
        _state.value = SessionFormState(
            company = settingsManager.defaultCompany,
            deletionPeriod = settingsManager.defaultDeletionPeriod
        )
    }

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            repository.getSessionById(sessionId).first()?.let { session ->
                val locationDisplay = when {
                    session.address != null -> session.address
                    session.latitude != null && session.longitude != null ->
                        "%.6f, %.6f".format(session.latitude, session.longitude)
                    else -> ""
                }
                _state.value = SessionFormState(
                    name = session.name,
                    company = session.company,
                    notes = session.notes,
                    latitude = session.latitude,
                    longitude = session.longitude,
                    address = session.address,
                    beforeDate = session.beforeDate,
                    afterDate = session.afterDate,
                    locationText = locationDisplay,
                    deletionPeriod = DeletionPeriod.fromDays(session.deletionPeriodDays)
                )
            }
        }
    }

    fun updateName(name: String) { _state.value = _state.value.copy(name = name) }
    fun updateCompany(c: String) { _state.value = _state.value.copy(company = c) }
    fun updateNotes(n: String) { _state.value = _state.value.copy(notes = n) }
    fun updateDeletionPeriod(p: DeletionPeriod) { _state.value = _state.value.copy(deletionPeriod = p) }

    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isFetchingLocation = true)
            try {
                val loc = getLastLocation(context)
                if (loc != null) {
                    val addr = reverseGeocode(context, loc.first, loc.second)
                    _state.value = _state.value.copy(
                        isFetchingLocation = false,
                        latitude = loc.first,
                        longitude = loc.second,
                        address = addr,
                        locationText = addr ?: "%.6f, %.6f".format(loc.first, loc.second)
                    )
                } else {
                    _state.value = _state.value.copy(isFetchingLocation = false)
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
                longitude = s.longitude,
                address = s.address,
                beforeDate = s.beforeDate,
                afterDate = s.afterDate,
                deletionPeriodDays = s.deletionPeriod.days
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
                    longitude = s.longitude,
                    address = s.address,
                    beforeDate = s.beforeDate,
                    afterDate = s.afterDate,
                    deletionPeriodDays = s.deletionPeriod.days
                )
            )
        }
    }
}
