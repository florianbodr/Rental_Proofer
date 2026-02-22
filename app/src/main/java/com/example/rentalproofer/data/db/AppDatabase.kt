package com.example.rentalproofer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rentalproofer.data.model.RentalPhoto
import com.example.rentalproofer.data.model.RentalSession

@Database(
    entities = [RentalSession::class, RentalPhoto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): RentalSessionDao
    abstract fun photoDao(): RentalPhotoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rental_proofer.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
