package com.example.finanse.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.ui.theme.ThemeMode
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