package com.example.finanse.states

import com.example.finanse.entities.Category
import com.example.finanse.sortTypes.CategorySortType

data class CategoryState(
    val category: List<Category> = emptyList(),
    val name: String = "",
    val isAddingCategory: Boolean = false,
    val categorySortType: CategorySortType = CategorySortType.DATE_ADDED
)
