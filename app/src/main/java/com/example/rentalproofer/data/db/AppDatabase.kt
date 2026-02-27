package com.example.rentalproofer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rentalproofer.data.model.RentalPhoto
import com.example.rentalproofer.data.model.RentalSession

@Database(
    entities = [RentalSession::class, RentalPhoto::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): RentalSessionDao
    abstract fun photoDao(): RentalPhotoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE rental_sessions ADD COLUMN beforeDate INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE rental_sessions ADD COLUMN afterDate INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE rental_sessions ADD COLUMN address TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE rental_sessions ADD COLUMN deletionPeriodDays INTEGER NOT NULL DEFAULT 180"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rental_proofer.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { INSTANCE = it }
            }
        }
    }
}
