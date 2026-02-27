package com.example.rentalproofer.ui.screens.sessiondetail

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentalproofer.RentalProoferApp
import com.example.rentalproofer.data.model.PhotoType
import com.example.rentalproofer.data.model.RentalPhoto
import com.example.rentalproofer.data.model.RentalSession
import com.example.rentalproofer.util.FileUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class SessionDetailState(
    val session: RentalSession? = null,
    val beforePhotos: List<RentalPhoto> = emptyList(),
    val afterPhotos: List<RentalPhoto> = emptyList(),
    val selectedPhotoIds: Set<Long> = emptySet()
)

class SessionDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as RentalProoferApp).repository
    private val _sessionId = MutableStateFlow(-1L)
    private val _selectedPhotoIds = MutableStateFlow<Set<Long>>(emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SessionDetailState> = _sessionId
        .flatMapLatest { id ->
            if (id == -1L) flowOf(SessionDetailState())
            else combine(
                repository.getSessionById(id),
                repository.getPhotosForSession(id),
                _selectedPhotoIds
            ) { session, photos, selected ->
                SessionDetailState(
                    session = session,
                    beforePhotos = photos.filter { it.type == PhotoType.BEFORE },
                    afterPhotos = photos.filter { it.type == PhotoType.AFTER },
                    selectedPhotoIds = selected
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionDetailState())

    fun setSessionId(id: Long) { _sessionId.value = id }

    fun togglePhotoSelection(photoId: Long) {
        _selectedPhotoIds.value = if (photoId in _selectedPhotoIds.value)
            _selectedPhotoIds.value - photoId
        else
            _selectedPhotoIds.value + photoId
    }

    fun clearSelection() { _selectedPhotoIds.value = emptySet() }

    fun deletePhoto(photo: RentalPhoto) {
        viewModelScope.launch {
            _selectedPhotoIds.value = _selectedPhotoIds.value - photo.id
            repository.deletePhoto(photo)
        }
    }

    fun updateBeforeDate(millis: Long) {
        viewModelScope.launch {
            state.value.session?.let { session ->
                repository.updateSession(session.copy(beforeDate = millis))
            }
        }
    }

    fun updateAfterDate(millis: Long) {
        viewModelScope.launch {
            state.value.session?.let { session ->
                repository.updateSession(session.copy(afterDate = millis))
            }
        }
    }

    fun exportZip(context: Context, selectedIds: Set<Long>, allPhotos: List<RentalPhoto>) {
        viewModelScope.launch {
            val files = allPhotos
                .filter { it.id in selectedIds }
                .map { File(it.filePath) }
                .filter { it.exists() }
            if (files.isEmpty()) return@launch
            val zipFile = FileUtil.createZipFile(context, files)
            val uri = FileUtil.getShareUri(context, zipFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share ZIP"))
        }
    }
}
