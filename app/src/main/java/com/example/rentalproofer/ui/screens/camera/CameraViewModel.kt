package com.example.rentalproofer.ui.screens.camera

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.PhotoType
import com.example.rentalproofer.data.model.RentalPhoto
import com.example.rentalproofer.util.FileUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as RentalProoferApp).repository
    private val _photoType = MutableStateFlow(PhotoType.BEFORE)
    val photoType: StateFlow<PhotoType> = _photoType.asStateFlow()

    fun setPhotoType(type: PhotoType) { _photoType.value = type }

    fun togglePhotoType() {
        _photoType.value = if (_photoType.value == PhotoType.BEFORE) PhotoType.AFTER else PhotoType.BEFORE
    }

    fun createPhotoFile(context: Context, sessionId: Long) =
        FileUtil.createPhotoFile(context, sessionId, _photoType.value)

    fun savePhoto(sessionId: Long, filePath: String) {
        viewModelScope.launch {
            repository.insertPhoto(
                RentalPhoto(sessionId = sessionId, filePath = filePath, type = _photoType.value)
            )
        }
    }
}
