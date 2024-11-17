package com.example.finanse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.ConfirmPopup
import com.example.finanse.TopNavBar
import com.example.finanse.events.CategoryEvent
import com.example.finanse.sortTypes.CategorySortType
import com.example.finanse.states.CategoryState
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(CategoryEvent.ShowDialog)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add a category")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) { padding ->

        if (state.isAddingCategory) {
            AddCategoryDialog(state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp), // Add consistent top padding
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopNavBar(navController, "Categories", "menu")
            }
            item {
                var expanded by remember { mutableStateOf(false) } // Control dropdown menu state
                var selectedSortType by remember { mutableStateOf(state.categorySortType) } // Track selected sort type

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp) // Add padding around sorting options
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Align items vertically centered
                        modifier = Modifier.padding(vertical = 8.dp) // Space around the row
                    ) {
                        Text(
                            text = "Sort type: ", // Label for the dropdown
                            fontSize = 16.sp, // Font size
                            modifier = Modifier.padding(end = 8.dp) // Space between label and dropdown
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded } // Toggle dropdown
                        ) {
                            // The TextField that shows the currently selected sort type
                            OutlinedTextField(
                                value = getSortTypeName(selectedSortType), // Show the selected sort type's name
                                onValueChange = {},
                                readOnly = true, // Prevent editing
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor() // Open dropdown when clicked
                            )

                            // The DropdownMenu with all sorting options
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false } // Close the dropdown menu
                            ) {
                                CategorySortType.entries.forEach { categorySortType ->
                                    DropdownMenuItem(
                                        text = { Text(text = getSortTypeName(categorySortType)) },
                                        onClick = {
                                            selectedSortType = categorySortType
                                            onEvent(CategoryEvent.SortCategories(categorySortType)) // Trigger event on selection
                                            expanded = false // Close dropdown
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(state.category) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = MaterialTheme.shapes.medium,
//                    backgroundColor = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier// Transparent background
                                .size(30.dp)
                                .padding(2.dp), // Padding for spacing
                            shape = MaterialTheme.shapes.small, // Add some elevation for shadow
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardColors(Color(category.color),Color(category.color),Color(category.color),Color(category.color))
                        ){}
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp) // Add space between color box and text
                        ) {
                            Text(text = category.name, fontSize = 20.sp)
                        }

                        // Default categories can't be edited or deleted
                        val defaultCategories =
                            listOf("Bills", "Groceries", "Entertainment", "Transport", "Other")
                        if (category.name !in defaultCategories) {
                            IconButton(onClick = {
                                onEvent(CategoryEvent.GetData(category.name))
                                onEvent(CategoryEvent.ShowDialog)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit category"
                                )
                            }
                            ConfirmPopup().DeleteIconButton("Confirm Delete", "Are you sure you want to delete this category?") {
                                onEvent(CategoryEvent.DeleteCategory(category))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    val errorMessage = remember { mutableStateOf("") }
    BasicAlertDialog(onDismissRequest = { onEvent(CategoryEvent.HideDialog) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp), // Increase space between elements
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(text = "Add or Edit Category", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            // Name Input Field
            TextField(
                value = state.name,
                onValueChange = {
                    onEvent(CategoryEvent.SetName(it))
                },
                placeholder = {
                    Text(text = "Category Name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Display current color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(state.color))
                    .border(1.dp, Color.Black)
            )

            // Color Picker
            val controller = rememberColorPickerController()
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 10.dp),
                controller = controller,
                initialColor = Color(state.color),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    onEvent(CategoryEvent.SetColor(colorEnvelope.color.hashCode()))
                }
            )

            // Save and Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onEvent(CategoryEvent.HideDialog)
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Cancel")
                }

                Button(
                    onClick = {
                        if (state.name.isEmpty()) {
                            errorMessage.value = "Category must have a name"
                        } else {
                            errorMessage.value = ""
                            onEvent(CategoryEvent.SaveCategory)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Save")
                }
            }

            // Error message display
            if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = Color.Red)
            }
        }
    }
}
fun getSortTypeName(name: CategorySortType): String{
    return when (name) {
        CategorySortType.NAME-> "Name"
        CategorySortType.DATE_ADDED -> "Default"
    }
}