package com.example.rentalproofer.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.DeletionPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsState(
    val company: String = "",
    val address: String = "",
    val deletionPeriod: DeletionPeriod = DeletionPeriod.SIX_MONTHS
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = (application as RentalProoferApp).settingsManager
    private val _state = MutableStateFlow(
        SettingsState(
            company = settingsManager.defaultCompany,
            address = settingsManager.defaultAddress,
            deletionPeriod = settingsManager.defaultDeletionPeriod
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun updateCompany(value: String) {
        _state.value = _state.value.copy(company = value)
        settingsManager.defaultCompany = value
    }

    fun updateAddress(value: String) {
        _state.value = _state.value.copy(address = value)
        settingsManager.defaultAddress = value
    }

    fun updateDeletionPeriod(value: DeletionPeriod) {
        _state.value = _state.value.copy(deletionPeriod = value)
        settingsManager.defaultDeletionPeriod = value
    }
}
