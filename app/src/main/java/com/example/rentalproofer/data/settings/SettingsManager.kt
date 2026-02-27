package com.example.rentalproofer.data.settings

import android.content.Context
import com.example.rentalproofer.data.model.DeletionPeriod

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("rental_proofer_settings", Context.MODE_PRIVATE)

    var defaultCompany: String
        get() = prefs.getString(KEY_COMPANY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_COMPANY, value).apply()

    var defaultAddress: String
        get() = prefs.getString(KEY_ADDRESS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ADDRESS, value).apply()

    var defaultDeletionPeriod: DeletionPeriod
        get() = DeletionPeriod.fromDays(prefs.getInt(KEY_DELETION_DAYS, DeletionPeriod.SIX_MONTHS.days))
        set(value) = prefs.edit().putInt(KEY_DELETION_DAYS, value.days).apply()

    companion object {
        private const val KEY_COMPANY = "default_company"
        private const val KEY_ADDRESS = "default_address"
        private const val KEY_DELETION_DAYS = "default_deletion_days"
    }
}
