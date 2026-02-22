package com.example.rentalproofer.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.rentalproofer.data.model.PhotoType
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileUtil {
    fun createPhotoFile(context: Context, sessionId: Long, type: PhotoType): File {
        val dir = File(context.getExternalFilesDir(null), "session_$sessionId/${type.name.lowercase()}")
        dir.mkdirs()
        return File(dir, "photo_${System.currentTimeMillis()}.jpg")
    }

    fun getShareUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun createZipFile(context: Context, files: List<File>): File {
        val zipFile = File(context.cacheDir, "rental_photos_${System.currentTimeMillis()}.zip")
        ZipOutputStream(zipFile.outputStream().buffered()).use { zos ->
            files.forEach { file ->
                if (file.exists()) {
                    zos.putNextEntry(ZipEntry(file.name))
                    FileInputStream(file).use { fis -> fis.copyTo(zos) }
                    zos.closeEntry()
                }
            }
        }
        return zipFile
    }
}
