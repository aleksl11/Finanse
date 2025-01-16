package com.example.finanse.events

import android.content.Context
import android.net.Uri

sealed interface AlbumEvent {
    data class OnPermissionGrantedWith(val compositionContext: Context): AlbumEvent
    data object OnPermissionDenied: AlbumEvent
    data class OnImageSavedWith (val compositionContext: Context): AlbumEvent
    data object OnImageSavingCanceled: AlbumEvent
    data class OnFinishPickingImagesWith(val compositionContext: Context, val imageUrls: List<Uri>): AlbumEvent
}