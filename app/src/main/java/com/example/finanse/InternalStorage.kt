package com.example.finanse

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InternalStorage {
    fun cleanCache(cacheDir: File) {
        cacheDir.listFiles { file ->
            file.name.endsWith(".jpg") // Assuming images are saved with .jpg extension
        }?.forEach { it.delete() }
    }

    fun moveImageToInternalStorage(context: Context, bitmap: Bitmap, directory: String): String? {
        val internalDir = File(context.filesDir, directory)
        if (!internalDir.exists()) internalDir.mkdirs()

        val fileName = directory+"_${System.currentTimeMillis()}.jpg"
        val destinationFile = File(internalDir, fileName)

        return try {
            // Saving Bitmap to file
            val outputStream = FileOutputStream(destinationFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            destinationFile.absolutePath // Return the new file path
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}