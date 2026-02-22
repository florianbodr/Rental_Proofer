package com.example.rentalproofer

import android.app.Application
import com.example.rentalproofer.data.db.AppDatabase
import com.example.rentalproofer.data.repository.RentalRepository

class RentalProoferApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy {
        RentalRepository(database.sessionDao(), database.photoDao())
    }
}
