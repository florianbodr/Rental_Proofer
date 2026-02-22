package com.example.rentalproofer.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

@SuppressLint("MissingPermission")
suspend fun getLastLocation(context: Context): Pair<Double, Double>? =
    suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { location ->
                cont.resume(location?.let { Pair(it.latitude, it.longitude) })
            }
            .addOnFailureListener { cont.resumeWithException(it) }
            .addOnCanceledListener { cont.cancel() }
    }
