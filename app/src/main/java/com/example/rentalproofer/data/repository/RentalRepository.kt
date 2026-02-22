package com.example.rentalproofer.data.repository

import com.example.rentalproofer.data.db.RentalPhotoDao
import com.example.rentalproofer.data.db.RentalSessionDao
import com.example.rentalproofer.data.model.RentalPhoto
import com.example.rentalproofer.data.model.RentalSession
import java.io.File

class RentalRepository(
    private val sessionDao: RentalSessionDao,
    private val photoDao: RentalPhotoDao
) {
    fun getAllSessions() = sessionDao.getAllSessions()
    fun getSessionById(id: Long) = sessionDao.getSessionById(id)
    fun getPhotosForSession(sessionId: Long) = photoDao.getPhotosForSession(sessionId)

    suspend fun insertSession(session: RentalSession): Long = sessionDao.insertSession(session)
    suspend fun updateSession(session: RentalSession) = sessionDao.updateSession(session)
    suspend fun deleteSession(session: RentalSession) = sessionDao.deleteSession(session)

    suspend fun insertPhoto(photo: RentalPhoto): Long = photoDao.insertPhoto(photo)

    suspend fun deletePhoto(photo: RentalPhoto) {
        File(photo.filePath).delete()
        photoDao.deletePhoto(photo)
    }

    suspend fun getPhotoById(id: Long): RentalPhoto? = photoDao.getPhotoById(id)
}
