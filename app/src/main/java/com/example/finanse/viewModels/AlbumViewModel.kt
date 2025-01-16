package com.example.finanse.viewModels


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.finanse.events.AlbumEvent
import com.example.finanse.states.AlbumState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    @RequiresApi(Build.VERSION_CODES.P)
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
                            val bitmapOptions = BitmapFactory.Options()
                            bitmapOptions.inMutable = true
                            val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bitmapOptions)
                            newImages.add(bitmap.asImageBitmap())
                        } else {
                            // error reading the bytes from the image url
                            println("The image that was picked could not be read from the device at this url: $eachImageUrl")
                        }
                    }

                    val currentViewState = _albumViewState.value
                    val newCopy = currentViewState.copy(
                        selectedPictures = (currentViewState.selectedPictures + newImages),
                        tempFileUrl = null
                    )
                    _albumViewState.value = newCopy
                } else {
                    println("noting picked")
                }

            }
            is AlbumEvent.OnImageSavedWith -> {
                val tempImageUrl = _albumViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val source = ImageDecoder.createSource(event.compositionContext.contentResolver, tempImageUrl)

                    val currentPictures = _albumViewState.value.selectedPictures.toMutableList()
                    currentPictures.add(ImageDecoder.decodeBitmap(source).asImageBitmap())

                    _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null,
                        selectedPictures = currentPictures)
                }
            }
            is AlbumEvent.OnImageSavingCanceled -> {
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null)
            }
        }
    }
    // endregion
}