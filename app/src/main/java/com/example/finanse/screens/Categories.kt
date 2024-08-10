package com.example.finanse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.TopNavBar
import com.example.finanse.events.CategoryEvent
import com.example.finanse.sortTypes.CategorySortType
import com.example.finanse.states.CategoryState
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun CategoriesScreen(
    navController: NavController,
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(CategoryEvent.ShowDialog)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add a category")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {padding ->

        if(state.isAddingCategory) {
            AddCategoryDialog(state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                TopNavBar(navController, "Categories","menu")
            }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    CategorySortType.entries.forEach { categorySortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(CategoryEvent.SortCategories(categorySortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            RadioButton(selected = state.categorySortType == categorySortType,
                                onClick = {
                                    onEvent(CategoryEvent.SortCategories(categorySortType))
                                }
                            )
                            Text(text = getSortTypeName(categorySortType))
                        }
                    }
                }
            }
            items(state.category){category ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .background(Color(category.color))
                            .size(20.dp)
                            .border(1.dp, Color.Black)
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
                        Text(text = category.name, fontSize = 20.sp)
                    }
                    val defaultCategories = listOf("Bills", "Groceries", "Entertainment", "Transport", "Other")
                    if (category.name !in defaultCategories) {
                        IconButton(onClick = {
                            onEvent(CategoryEvent.GetData(category.name))
                            onEvent(CategoryEvent.ShowDialog)
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit expense")
                        }
                        IconButton(onClick = {
                            onEvent(CategoryEvent.DeleteCategory(category))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete category"
                            )
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
){
    val text = remember { mutableStateOf("") }
    BasicAlertDialog(onDismissRequest = { onEvent(CategoryEvent.HideDialog) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(Color.Gray)
                .padding(8.dp)
        ) {
            Text(text = "Category")
            TextField(
                value = state.name,
                onValueChange = {
                    onEvent(CategoryEvent.SetName(it))
                },
                placeholder = {
                    Text(text = "Name")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(40.dp)
                    .background(Color(state.color))
            )
            val controller = rememberColorPickerController()
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(10.dp),
                controller = controller,
                initialColor = Color(state.color),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    onEvent(CategoryEvent.SetColor(colorEnvelope.color.hashCode()))
                }
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    if (state.name == "") {
                        text.value = "Category must have a name"
                    } else {
                        text.value = ""
                        onEvent(CategoryEvent.SaveCategory)
                    }
                }) {
                    Text(text = "Save")
                }
            }
            Text(text = text.value, color = Color.Red)
        }
    }
}
fun getSortTypeName(name: CategorySortType): String{
    return when (name) {
        CategorySortType.NAME-> "Name"
        CategorySortType.DATE_ADDED -> "Default"
    }
}