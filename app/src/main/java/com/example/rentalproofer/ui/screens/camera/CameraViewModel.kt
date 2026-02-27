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
    private val _photosTakenCount = MutableStateFlow(0)
    val photosTakenCount: StateFlow<Int> = _photosTakenCount.asStateFlow()

    fun setPhotoType(type: PhotoType) { _photoType.value = type }

    fun togglePhotoType() {
        _photoType.value = if (_photoType.value == PhotoType.BEFORE) PhotoType.AFTER else PhotoType.BEFORE
    }

    fun createPhotoFile(context: Context, sessionId: Long) =
        FileUtil.createPhotoFile(context, sessionId, _photoType.value)

    fun savePhoto(sessionId: Long, filePath: String) {
        viewModelScope.launch {
            val currentType = _photoType.value
            repository.insertPhoto(
                RentalPhoto(sessionId = sessionId, filePath = filePath, type = currentType)
            )
            _photosTakenCount.value++

            val session = repository.getSessionByIdOnce(sessionId) ?: return@launch
            val now = System.currentTimeMillis()
            when {
                currentType == PhotoType.BEFORE && session.beforeDate == null ->
                    repository.updateSession(session.copy(beforeDate = now))
                currentType == PhotoType.AFTER && session.afterDate == null ->
                    repository.updateSession(session.copy(afterDate = now))
            }
        }
    }
}
