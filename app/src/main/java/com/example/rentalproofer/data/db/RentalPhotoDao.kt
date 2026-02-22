package com.example.rentalproofer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rentalproofer.data.model.RentalPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalPhotoDao {
    @Query("SELECT * FROM rental_photos WHERE sessionId = :sessionId ORDER BY capturedAt ASC")
    fun getPhotosForSession(sessionId: Long): Flow<List<RentalPhoto>>

    @Query("SELECT * FROM rental_photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): RentalPhoto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: RentalPhoto): Long

    @Delete
    suspend fun deletePhoto(photo: RentalPhoto)
}
