package com.example.rentalproofer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rentalproofer.data.model.RentalSession
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalSessionDao {
    @Query("SELECT * FROM rental_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<RentalSession>>

    @Query("SELECT * FROM rental_sessions WHERE id = :id")
    fun getSessionById(id: Long): Flow<RentalSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RentalSession): Long

    @Update
    suspend fun updateSession(session: RentalSession)

    @Delete
    suspend fun deleteSession(session: RentalSession)
}
