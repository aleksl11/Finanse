package com.example.finanse.viewModels


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.finanse.events.AlbumEvent
import com.example.finanse.screens.getBitmapFromFile
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
    fun onEvent(event: AlbumEvent) {
        when (event) {
            is AlbumEvent.OnPermissionGrantedWith -> {
                val tempFile = File(event.compositionContext.cacheDir, "image_${System.currentTimeMillis()}.jpg")

                val uri = FileProvider.getUriForFile(
                    event.compositionContext,
                    "com.example.finanse.provider",
                    tempFile
                )

                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = uri)
            }

            is AlbumEvent.OnPermissionDenied -> {
                Log.d("Permission Denied","User did not grant permission to use the camera")
            }
            is AlbumEvent.OnFinishPickingImagesWith -> {
                if (event.imageUrls.isNotEmpty()) {
                    val newImages = mutableListOf<ImageBitmap>()

                    for (eachImageUrl in event.imageUrls) {
                        val bitmap = getBitmapFromUri(event.compositionContext, eachImageUrl)
                        if (bitmap != null) {
                            val tempFile = File(event.compositionContext.cacheDir, "image_${System.currentTimeMillis()}.jpg")

                            // Save only if it doesn't exist already
                            if (!tempFile.exists()) {
                                tempFile.outputStream().use { outputStream ->
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                }
                            }
                            newImages.add(bitmap.asImageBitmap())
                        }
                    }
                    _albumViewState.value = _albumViewState.value.copy(
                        selectedPictures = _albumViewState.value.selectedPictures + newImages,
                        tempFileUrl = null
                    )
                }
            }
            is AlbumEvent.OnImageSavedWith -> {
                val tempImageUrl = _albumViewState.value.tempFileUrl ?: return

                val file = File(event.compositionContext.cacheDir, File(tempImageUrl.path!!).name)

                if (file.exists()) {
                    val bitmap = getBitmapFromFile(file)
                    if (bitmap != null) {
                        _albumViewState.value = _albumViewState.value.copy(
                            tempFileUrl = null,
                            selectedPictures = _albumViewState.value.selectedPictures + bitmap
                        )
                    }
                }
            }

            is AlbumEvent.OnImageSavingCanceled -> {
                val tempImageUrl = _albumViewState.value.tempFileUrl
                tempImageUrl?.let {
                    val file = File(it.path!!)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null)
            }
            is AlbumEvent.OnDeletePicture -> {
                val cacheDir = event.compositionContext.cacheDir
                val tempFile = File(cacheDir, event.title)
                if (tempFile.exists()) {
                    tempFile.delete()
                }
            }
        }
    }
}

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream).also {
            inputStream?.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun createImageFile(context: Context): File {
    val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")
    file.createNewFile() // Ensure it exists
    return file
}