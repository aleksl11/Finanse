package com.example.finanse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

class ConfirmPopup {

    @Composable
    fun DeleteIconButton(title: String, message: String, onDeleteConfirmed: () -> Unit) {
        var showDialog by remember { mutableStateOf(false) }

        IconButton(onClick = { showDialog = true }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete record")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteConfirmed()
                        showDialog = false
                    }) {
                        Text(stringResource(R.string.delete_button))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }

    @Composable
    fun ConfirmButtonAction(title: String, message: String, onConfirmed: () -> Unit) {
        var showDialog by remember { mutableStateOf(false) }

        Button(onClick = { showDialog = true }) {
            Text(title)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = {
                        onConfirmed()
                        showDialog = false
                    }) {
                        Text(stringResource(R.string.confirm_button))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}