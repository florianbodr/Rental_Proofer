package com.example.rentalproofer.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rental_photos",
    foreignKeys = [
        ForeignKey(
            entity = RentalSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class RentalPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val filePath: String,
    val type: PhotoType,
    val capturedAt: Long = System.currentTimeMillis()
)
