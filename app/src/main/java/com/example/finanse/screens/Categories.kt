package com.example.finanse.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.ConfirmPopup
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.entities.Category
import com.example.finanse.events.CategoryEvent
import com.example.finanse.sortTypes.CategorySortType
import com.example.finanse.states.CategoryState
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

private var isEditing = false
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(CategoryEvent.ShowDialog) },
                shape = MaterialTheme.shapes.medium,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_category_desc))
            }
        },
    ) { padding ->

        if (state.isAddingCategory) {
            AddCategoryDialog(context = context, state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize(),
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
                            text = stringResource(R.string.sort_by)+": ", // Label for the dropdown
                            fontSize = 16.sp, // Font size
                            modifier = Modifier.padding(end = 8.dp) // Space between label and dropdown
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded } // Toggle dropdown
                        ) {
                            // The TextField that shows the currently selected sort type
                            OutlinedTextField(
                                value = getSortTypeName(context, selectedSortType), // Show the selected sort type's name
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
                                        text = { Text(text = getSortTypeName(context, categorySortType)) },
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
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Background color
                        contentColor = MaterialTheme.colorScheme.onSurface  // Text and icon color
                    )
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
                                isEditing = true
                                onEvent(CategoryEvent.ShowDialog)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_category_desc)
                                )
                            }
                            ConfirmPopup().DeleteIconButton(stringResource(R.string.delete_name), stringResource(R.string.delete_message_category)) {
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
    context: Context,
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
            Text(text = stringResource(R.string.add_category_dialog), fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            // Name Input Field
            TextField(
                value = state.name,
                onValueChange = {
                    onEvent(CategoryEvent.SetName(it))
                },
                placeholder = {
                    Text(text = stringResource(R.string.category_name_label))
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
                    Text(text = stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        val mCategories = state.category
                        if (state.name.isEmpty()) {
                            errorMessage.value = context.getString(R.string.no_name_error)
                        } else if (isNameInDb(state.name, mCategories) && !isEditing) {
                            errorMessage.value = context.getString(R.string.repeat_category_error)
                        }
                        else{
                            errorMessage.value = ""
                            isEditing = false
                            onEvent(CategoryEvent.SaveCategory)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }

            // Error message display
            if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = Color.Red)
            }
        }
    }
}
fun getSortTypeName(context: Context, name: CategorySortType): String{
    return when (name) {
        CategorySortType.NAME-> context.getString(R.string.sort_by_name)
        CategorySortType.DATE_ADDED -> context.getString(R.string.sort_by_default)
    }
}

fun isNameInDb(name: String, mCategories: List<Category>): Boolean{
    var check = false
    mCategories.forEach{ a ->
        if (a.name == name ) {
            check = true
        }
    }
    return check
}