package com.example.finanse.states

import androidx.compose.ui.graphics.Color
import com.example.finanse.entities.Category
import com.example.finanse.sortTypes.CategorySortType

data class CategoryState(
    val category: List<Category> = emptyList(),
    val name: String = "",
    val color: Int = Color.White.hashCode(),
    val isAddingCategory: Boolean = false,
    val categorySortType: CategorySortType = CategorySortType.DATE_ADDED
)
