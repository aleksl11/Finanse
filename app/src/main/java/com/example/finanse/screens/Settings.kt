package com.example.finanse.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.MainDatabase
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.ui.theme.ThemeMode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import com.google.api.services.drive.model.File as DriveFile

@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeMode: ThemeMode,
    currentLanguage: String,
    onThemeModeChanged: suspend (ThemeMode) -> Unit,
    onLanguageChanged: (String) -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    var user by remember {
        mutableStateOf(auth.currentUser)
    }
    val context = LocalContext.current

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    user = authResult.user
                }
                .addOnFailureListener { e ->
                    Log.e("Auth", "Sign-in failed", e)
                }
        } catch (e: ApiException) {
            Log.e("Auth", "Google Sign-In Failed", e)
        }
    }


    Column{
        TopNavBar(navController, "settings","menu")
        LazyColumn {
            item {
                ThemeSelection(
                    currentThemeMode = currentThemeMode,
                    onThemeSelected = { selectedTheme ->
                        coroutineScope.launch {
                            onThemeModeChanged(selectedTheme) // Call inside coroutine
                        }
                    }
                )
            }
            item {
                LanguageSelection(
                    currentLanguage,
                    onLanguageChanged
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Sign-In Button
                Button(onClick = {
                    if (user == null) {
                        signInWithGoogle(context, signInLauncher)
                    } else {
                        auth.signOut()
                        user = null
                    }
                }) {
                    Text(if (user == null) stringResource(R.string.sign_in) else stringResource(R.string.sign_out)+" (${user?.email})")
                }
            }
            if (user!= null){
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Button(onClick = {
                            backupData(context)
                        }) {
                            Text(text = stringResource(R.string.backup_data))
                        }
                        Button(onClick = {
                            deleteBackup(context)
                        }) {
                            Text(text = stringResource(R.string.delete_data))
                        }
                    }
                    Button(onClick = {
                        restoreBackup(context)
                    }) {
                        Text(text = stringResource(R.string.restore_data))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelection(
    currentThemeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val themeOptions = ThemeMode.entries
    var expanded by remember { mutableStateOf(false) }

    val currentThemeNameResId = when (currentThemeMode.name) {
        "LIGHT" -> R.string.light
        "DARK" -> R.string.dark
        "SYSTEM" -> R.string.system
        else -> R.string.system
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.theme) + ": ",
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = stringResource(currentThemeNameResId),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                themeOptions.forEach { theme ->
                    val stringResId = when (theme.name) {
                        "LIGHT" -> R.string.light
                        "DARK" -> R.string.dark
                        "SYSTEM" -> R.string.system
                        else -> {
                            R.string.system
                        }
                    }
                    DropdownMenuItem(
                        text = { Text(text = stringResource(stringResId)) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelection(
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Dropdown state
    val languages = mapOf("English" to "en", "Polski" to "pl", "Español" to "es")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.language) + ": ",
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = languages.entries.find { it.value == currentLanguage }?.key ?: "English",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { (languageName, languageCode) ->
                    DropdownMenuItem(
                        text = { Text(text = languageName) },
                        onClick = {
                            onLanguageChanged(languageCode) // Trigger callback
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun signInWithGoogle(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("488381489727-a7r7s1tok0kd2tld6cic2d6jib7qpqnk.apps.googleusercontent.com")
        .requestEmail()
        .requestScopes(
            Scope(DriveScopes.DRIVE_FILE),
            Scope(DriveScopes.DRIVE_APPDATA)
        )
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}

private const val DATABASE_NAME = "main.db"
private const val BACKUP_FILE_NAME = "backup.zip"

fun backupData(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val zipFile = createBackupZip(context)

            val driveService = getDriveService(context)

            val backupFileId = getBackupFileId(driveService)

            val mediaContent = FileContent("application/zip", zipFile)

            if (backupFileId != null) {
                val fileMetadata = DriveFile().apply {
                    name = BACKUP_FILE_NAME

                }
                driveService.files().update(backupFileId, fileMetadata, mediaContent).execute()
            } else {
                val fileMetadata = DriveFile().apply {
                    name = BACKUP_FILE_NAME
                    parents = listOf("appDataFolder")
                }
                driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
            }
            zipFile.delete()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Backup completed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Backup failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun restoreBackup(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            MainDatabase.getInstance(context).close()

            val driveService = getDriveService(context)

            val backupFileId = getBackupFileId(driveService) ?: return@launch

            val outputStream = ByteArrayOutputStream()
            driveService.files().get(backupFileId)
                .executeMediaAndDownloadTo(outputStream)
            val zipFile = File(context.cacheDir, BACKUP_FILE_NAME)
            zipFile.writeBytes(outputStream.toByteArray())

            restoreFromZip(context, zipFile)

            zipFile.delete()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Restore completed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Restore failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun deleteBackup(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val driveService = getDriveService(context)
            val backupFileId = getBackupFileId(driveService)
            if (backupFileId != null) {
                driveService.files().delete(backupFileId).execute()
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Backup deleted", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun getDriveService(context: Context): Drive {
    val account = GoogleSignIn.getLastSignedInAccount(context)
        ?: throw IllegalStateException("No Google account is signed in")

    val credential = GoogleAccountCredential.usingOAuth2(
        context, listOf(DriveScopes.DRIVE_FILE)
    )
    credential.selectedAccount = account.account

    val transport = GoogleNetHttpTransport.newTrustedTransport()

    return Drive.Builder(
        transport,
        GsonFactory(),
        credential
    )
        .setApplicationName("FInanse")
        .build()
}

private fun getBackupFileId(driveService: Drive): String? {
    val result = driveService.files().list()
        .setSpaces("appDataFolder")
        .setQ("name = '$BACKUP_FILE_NAME'")
        .setFields("files(id, name)")
        .execute()
    return result.files.firstOrNull()?.id
}
private fun createBackupZip(context: Context): File {
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    val expenseDir = File(context.filesDir, "Expense")
    val incomeDir = File(context.filesDir, "Income")

    val zipFile = File(context.cacheDir, BACKUP_FILE_NAME)
    ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
        if (dbFile.exists()) {
            addFileToZip(zos, dbFile, "database/$DATABASE_NAME")
        }
        // Add the photos
        if (expenseDir.exists() && expenseDir.isDirectory) {
            addFolderToZip(zos, expenseDir, "photos")
        }
        if (incomeDir.exists() && incomeDir.isDirectory) {
            addFolderToZip(zos, incomeDir, "photos")
        }
    }
    return zipFile
}

private fun addFileToZip(zos: ZipOutputStream, file: File, entryName: String) {
    FileInputStream(file).use { fis ->
        val zipEntry = ZipEntry(entryName)
        zos.putNextEntry(zipEntry)
        fis.copyTo(zos)
        zos.closeEntry()
    }
}

private fun addFolderToZip(zos: ZipOutputStream, folder: File, parentFolder: String) {
    folder.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            addFolderToZip(zos, file, "$parentFolder/${file.name}")
        } else {
            addFileToZip(zos, file, "$parentFolder/${file.name}")
        }
    }
}

private fun restoreFromZip(context: Context, zipFile: File) {
    ZipInputStream(FileInputStream(zipFile)).use { zis ->
        var entry: ZipEntry? = zis.nextEntry
        while (entry != null) {
            when {
                entry.name.startsWith("database/") -> {
                    val fileName = entry.name.substringAfter("database/")
                    val outFile = context.getDatabasePath(fileName)
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                entry.name.startsWith("Expense/") -> {
                    // Extract files into the photos directory.
                    val relativePath = entry.name.substringAfter("Expense/")
                    val outFile = File(File(context.filesDir, "Expense"), relativePath)
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                entry.name.startsWith("Income/") -> {
                    // Extract files into the photos directory.
                    val relativePath = entry.name.substringAfter("Income/")
                    val outFile = File(File(context.filesDir, "Income"), relativePath)
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
            }
            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
}