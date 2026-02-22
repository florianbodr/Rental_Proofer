package com.example.rentalproofer.ui.screens.photoviewer

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.RentalPhoto
import com.example.rentalproofer.util.FileUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PhotoViewerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as RentalProoferApp).repository
    private val _photo = MutableStateFlow<RentalPhoto?>(null)
    val photo: StateFlow<RentalPhoto?> = _photo.asStateFlow()

    fun loadPhoto(photoId: Long) {
        viewModelScope.launch {
            _photo.value = repository.getPhotoById(photoId)
        }
    }

    fun sharePhoto(context: Context) {
        val p = _photo.value ?: return
        val file = File(p.filePath)
        if (!file.exists()) return
        val uri = FileUtil.getShareUri(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Photo"))
    }

    fun deletePhoto(onDeleted: () -> Unit) {
        val p = _photo.value ?: return
        viewModelScope.launch {
            repository.deletePhoto(p)
            onDeleted()
        }
    }
}
