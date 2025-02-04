package com.example.finanse.viewModels


import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.finanse.events.AlbumEvent
import com.example.finanse.states.AlbumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.coroutines.CoroutineContext

class AlbumViewModel(private val coroutineContext: CoroutineContext): ViewModel() {
    //region View State
    private val _albumViewState: MutableStateFlow<AlbumState> = MutableStateFlow(AlbumState())
    // exposes the ViewState to the composable view
    val viewStateFlow: StateFlow<AlbumState>
        get() = _albumViewState
    // endregion

    // region Intents
    fun onEvent(event: AlbumEvent) {
        when (event) {
            is AlbumEvent.OnPermissionGrantedWith -> {
                val tempFile = File.createTempFile(
                    "temp_image_file_", /* prefix */
                    ".jpg", /* suffix */
                    event.compositionContext.cacheDir  /* cache directory */
                )

                val uri = FileProvider.getUriForFile(event.compositionContext,
                    "com.example.finanse.provider", /* needs to match the provider information in the manifest */
                    tempFile
                )
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = uri)
            }
            is AlbumEvent.OnPermissionDenied -> {
                System.out.println("User did not grant permission to use the camera")
            }
            is AlbumEvent.OnFinishPickingImagesWith -> {
                if (event.imageUrls.isNotEmpty()) {
                    // Handle picked images
                    val newImages = mutableListOf<ImageBitmap>()
                    for (eachImageUrl in event.imageUrls) {
                        val inputStream = event.compositionContext.contentResolver.openInputStream(eachImageUrl)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            // Save the image to the cache directory
                            val tempFile = File(event.compositionContext.cacheDir, "image_${System.currentTimeMillis()}.jpg")
                            tempFile.writeBytes(bytes)

                            // Add the image to selectedPictures
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                            newImages.add(bitmap)
                        }
                    }

                    val currentViewState = _albumViewState.value
                    val newCopy = currentViewState.copy(
                        selectedPictures = (currentViewState.selectedPictures + newImages),
                        tempFileUrl = null
                    )
                    _albumViewState.value = newCopy
                }
            }
            is AlbumEvent.OnImageSavedWith -> {
                val tempImageUrl = _albumViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val inputStream = event.compositionContext.contentResolver.openInputStream(tempImageUrl)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        // Save the image to the cache directory
                        val tempFile = File(event.compositionContext.cacheDir, "image_${System.currentTimeMillis()}.jpg")
                        tempFile.writeBytes(bytes)

                        // Add the image to selectedPictures
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                        val currentState = _albumViewState.value
                        val updatedState = currentState.copy(
                            tempFileUrl = null,
                            selectedPictures = currentState.selectedPictures + bitmap
                        )

                        _albumViewState.value = updatedState
                    }
                }
            }
            is AlbumEvent.OnImageSavingCanceled -> {
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null)
            }
            is AlbumEvent.OnDeletePicture -> {
                val pictureToDelete = _albumViewState.value.selectedPictures[event.index]
                val cacheDir = event.compositionContext.cacheDir
                val tempFile = File(cacheDir, "image_${pictureToDelete.hashCode()}.jpg")
                if (tempFile.exists()) {
                    tempFile.delete()
                }
                _albumViewState.update { state ->
                    state.copy(selectedPictures = state.selectedPictures.toMutableList().apply {
                        removeAt(event.index)
                    })
                }
            }
        }
    }
}