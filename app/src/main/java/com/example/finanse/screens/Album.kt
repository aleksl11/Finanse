package com.example.finanse.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.finanse.events.AlbumEvent
import com.example.finanse.states.AlbumState
import com.example.finanse.viewModels.AlbumViewModel


@Composable
fun AlbumScreen(viewModel: AlbumViewModel) {
    // collecting the flow from the view model as a state allows our ViewModel and View
    // to be in sync with each other.
    val viewState: AlbumState by viewModel.viewStateFlow.collectAsState()
    // basic view that has 2 buttons and a grid for selected pictures

    val currentContext = LocalContext.current

    // launches photo picker
    val pickImageFromAlbumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { urls ->
        viewModel.onEvent(AlbumEvent.OnFinishPickingImagesWith(currentContext, urls))
    }

    // launches camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            viewModel.onEvent(AlbumEvent.OnImageSavedWith(currentContext))
        } else {
            // handle image saving error or cancellation
            viewModel.onEvent(AlbumEvent.OnImageSavingCanceled)
        }
    }

    // launches camera permissions
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            viewModel.onEvent(AlbumEvent.OnPermissionGrantedWith(currentContext))
        } else {
            // handle permission denied such as:
            viewModel.onEvent(AlbumEvent.OnPermissionDenied)
        }
    }

    LaunchedEffect(key1 = viewState.tempFileUrl) {
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
                Text(text = "Take a photo")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val mediaRequest =
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                pickImageFromAlbumLauncher.launch(mediaRequest)
            }) {
                Text(text = "Pick a picture")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Selected Pictures")
        LazyVerticalGrid(modifier = Modifier.fillMaxWidth().heightIn(0.dp, 1200.dp),
            columns = GridCells.Adaptive(150.dp),
            userScrollEnabled = false) {
            itemsIndexed(viewState.selectedPictures) { _, picture ->
                Image(modifier = Modifier.padding(8.dp),
                    bitmap = picture,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}