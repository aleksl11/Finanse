package com.example.finanse.screens

import android.Manifest
import android.graphics.BitmapFactory
import android.os.FileObserver
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.finanse.R
import com.example.finanse.events.AlbumEvent
import com.example.finanse.states.AlbumState
import com.example.finanse.viewModels.AlbumViewModel
import java.io.File


@Composable
fun AlbumScreen(viewModel: AlbumViewModel) {
    val viewState: AlbumState by viewModel.viewStateFlow.collectAsState()
    val currentContext = LocalContext.current

    val pickImageFromAlbumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { urls ->
        viewModel.onEvent(AlbumEvent.OnFinishPickingImagesWith(currentContext, urls))
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            viewModel.onEvent(AlbumEvent.OnImageSavedWith(currentContext))
        } else {
            viewModel.onEvent(AlbumEvent.OnImageSavingCanceled)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            viewModel.onEvent(AlbumEvent.OnPermissionGrantedWith(currentContext))
        } else {
            Toast.makeText(currentContext, "Permission denied", Toast.LENGTH_SHORT).show()
            viewModel.onEvent(AlbumEvent.OnPermissionDenied)
        }
    }

    val cacheDir = currentContext.cacheDir
    val imageFiles = remember { mutableStateListOf<File>() }

    val fileObserver = remember {
        object : FileObserver(cacheDir.path, CREATE or DELETE or MOVED_TO or MOVED_FROM) {
            override fun onEvent(event: Int, path: String?) {
                if (path != null && path.endsWith(".jpg")) {
                    val updatedList = cacheDir.listFiles()?.filter { it.extension == "jpg" } ?: emptyList()
                    imageFiles.clear()
                    imageFiles.addAll(updatedList)
                }
            }
        }.apply { startWatching() }
    }

    LaunchedEffect(viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    Column(modifier = Modifier) {
        Row {
            Button(onClick = {
                // get user's permission first to use camera
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text(text = stringResource(R.string.take_picture))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val mediaRequest =
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                pickImageFromAlbumLauncher.launch(mediaRequest)
            }) {
                Text(text = stringResource(R.string.pick_picture))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.selected_pictures))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.dp, 1200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = true
        ) {
            itemsIndexed(imageFiles) { index, file ->
                val bitmap = remember(file.path) {
                    getBitmapFromFile(file)
                }

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp) // The Box defines the size of the image
                ) {
                    bitmap?.let {
                        Image(
                            modifier = Modifier.matchParentSize(), // The Image fills the entire Box
                            bitmap = it,
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                    }
                    IconButton(
                        onClick = {
                            // Trigger delete event
                            viewModel.onEvent(AlbumEvent.OnDeletePicture(file.name, currentContext))
                        },
                        modifier = Modifier
                            .size(16.dp) // Ensure the IconButton has a smaller size
                            .align(Alignment.TopEnd) // Align it to the top-right corner of the Box
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.delete),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

fun getBitmapFromFile(file: File): ImageBitmap? {
    return try {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}