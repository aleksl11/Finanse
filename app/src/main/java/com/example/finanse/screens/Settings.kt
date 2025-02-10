package com.example.finanse.screens

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.ui.theme.ThemeMode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

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
                    Text(if (user == null) "Sign In with Google" else "Sign Out (${user?.email})")
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
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent) // ✅ Use the launcher instead of registerForActivityResult
}