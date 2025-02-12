package com.example.finanse.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun PhotoGallery(photoJson: String) {

    val photoPaths = remember(photoJson) {
        Gson().fromJson<List<String>>(photoJson, object : TypeToken<List<String>>() {}.type)
    }

    var selectedPhoto by remember { mutableStateOf<String?>(null) }

    Box {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(photoPaths) { photoPath ->
                AsyncImage(
                    model = photoPath,
                    contentDescription = "Minimised Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { selectedPhoto = photoPath }
                )
            }
        }

        if (selectedPhoto != null) {
            Dialog(
                onDismissRequest = { selectedPhoto = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black) // Black background for full-screen
                        .clickable { selectedPhoto = null } // Tap anywhere to close
                ) {
                    AsyncImage(
                        model = selectedPhoto,
                        contentDescription = "Full Screen Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}