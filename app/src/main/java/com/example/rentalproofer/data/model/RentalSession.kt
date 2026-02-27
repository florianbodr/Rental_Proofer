package com.example.rentalproofer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rental_sessions")
data class RentalSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String,
    val notes: String,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: Long = System.currentTimeMillis(),
    val beforeDate: Long? = null,
    val afterDate: Long? = null,
    val address: String? = null
)
