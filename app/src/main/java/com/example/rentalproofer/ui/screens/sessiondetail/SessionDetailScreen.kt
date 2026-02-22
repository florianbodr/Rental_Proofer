package com.example.rentalproofer.ui.screens.sessiondetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rentalproofer.data.model.PhotoType
import com.example.rentalproofer.data.model.RentalPhoto
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: Long,
    onAddPhoto: (String) -> Unit,
    onPhotoClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: SessionDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var addPhotoMenuExpanded by remember { mutableStateOf(false) }
    val inSelectionMode = state.selectedPhotoIds.isNotEmpty()
    val allPhotos = state.beforePhotos + state.afterPhotos

    LaunchedEffect(sessionId) { viewModel.setSessionId(sessionId) }

    Scaffold(
        topBar = {
            if (inSelectionMode) {
                TopAppBar(
                    title = { Text("${state.selectedPhotoIds.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, "Clear selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.exportZip(context, state.selectedPhotoIds, allPhotos)
                        }) {
                            Icon(Icons.Default.Share, "Export ZIP")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(state.session?.name ?: "") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = { addPhotoMenuExpanded = true }) {
                    Icon(Icons.Default.Add, "Add Photo")
                }
                DropdownMenu(
                    expanded = addPhotoMenuExpanded,
                    onDismissRequest = { addPhotoMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Before Photo") },
                        onClick = { addPhotoMenuExpanded = false; onAddPhoto(PhotoType.BEFORE.name) }
                    )
                    DropdownMenuItem(
                        text = { Text("After Photo") },
                        onClick = { addPhotoMenuExpanded = false; onAddPhoto(PhotoType.AFTER.name) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            state.session?.let { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (session.company.isNotBlank()) {
                            Text(
                                session.company,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (session.notes.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(session.notes, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (session.latitude != null && session.longitude != null) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "%.6f, %.6f".format(session.latitude, session.longitude),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            PhotoSection(
                title = "Before (${state.beforePhotos.size})",
                photos = state.beforePhotos,
                selectedIds = state.selectedPhotoIds,
                onPhotoClick = { photo ->
                    if (inSelectionMode) viewModel.togglePhotoSelection(photo.id)
                    else onPhotoClick(photo.id)
                },
                onPhotoLongClick = { photo -> viewModel.togglePhotoSelection(photo.id) }
            )

            PhotoSection(
                title = "After (${state.afterPhotos.size})",
                photos = state.afterPhotos,
                selectedIds = state.selectedPhotoIds,
                onPhotoClick = { photo ->
                    if (inSelectionMode) viewModel.togglePhotoSelection(photo.id)
                    else onPhotoClick(photo.id)
                },
                onPhotoLongClick = { photo -> viewModel.togglePhotoSelection(photo.id) }
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PhotoSection(
    title: String,
    photos: List<RentalPhoto>,
    selectedIds: Set<Long>,
    onPhotoClick: (RentalPhoto) -> Unit,
    onPhotoLongClick: (RentalPhoto) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (photos.isEmpty()) {
            Text(
                "No photos yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                photos.chunked(3).forEach { rowPhotos ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowPhotos.forEach { photo ->
                            PhotoThumbnail(
                                photo = photo,
                                isSelected = photo.id in selectedIds,
                                onClick = { onPhotoClick(photo) },
                                onLongClick = { onPhotoLongClick(photo) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowPhotos.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoThumbnail(
    photo: RentalPhoto,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        AsyncImage(
            model = File(photo.filePath),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (isSelected) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
