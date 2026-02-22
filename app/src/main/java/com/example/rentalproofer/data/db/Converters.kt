package com.example.rentalproofer.data.db

import androidx.room.TypeConverter
import com.example.rentalproofer.data.model.PhotoType

object Converters {
    @TypeConverter
    fun fromPhotoType(type: PhotoType): String = type.name

    @TypeConverter
    fun toPhotoType(name: String): PhotoType = PhotoType.valueOf(name)
}
