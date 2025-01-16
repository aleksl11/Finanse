package com.example.finanse.states

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class AlbumState(
    /**
     * holds the URL of the temporary file which stores the image taken by the camera.
     */
    val tempFileUrl: Uri? = null,

    /**
     * holds the list of images taken by camera or selected pictures from the gallery.
     */
    val selectedPictures: List<ImageBitmap> = emptyList(),
)
