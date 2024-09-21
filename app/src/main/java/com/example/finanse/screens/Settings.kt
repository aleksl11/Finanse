package com.example.finanse.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finanse.TopNavBar
import com.example.finanse.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeMode: ThemeMode,
    onThemeModeChanged: (ThemeMode) -> Unit
){
    Column{
        TopNavBar(navController, "settings","menu")
        LazyColumn {
            item {
                Text("Theme Settings", modifier = Modifier.padding(16.dp))
                ThemeSelection(
                    currentThemeMode = currentThemeMode,
                    onThemeSelected = { selectedTheme ->
                        onThemeModeChanged(selectedTheme)
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSelection(
    currentThemeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val themeOptions = listOf("Light", "Dark", "System")

    themeOptions.forEachIndexed { index, option ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    onThemeSelected(ThemeMode.entries[index])
                }
        ) {
            RadioButton(
                selected = currentThemeMode == ThemeMode.entries.toTypedArray()[index],
                onClick = {
                    onThemeSelected(ThemeMode.entries[index])
                }
            )
            Text(text = option, modifier = Modifier.padding(start = 8.dp))
        }
    }
}