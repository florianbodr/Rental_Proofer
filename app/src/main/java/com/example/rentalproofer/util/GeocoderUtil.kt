package com.example.rentalproofer.util

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

suspend fun reverseGeocode(context: Context, lat: Double, lon: Double): String? =
    withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) return@withContext null
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val addr = addresses?.firstOrNull() ?: return@withContext null
            buildList {
                addr.thoroughfare?.let { add(it) }
                addr.locality?.let { add(it) }
                addr.adminArea?.let { add(it) }
            }.ifEmpty { null }?.joinToString(", ")
        } catch (_: Exception) {
            null
        }
    }
