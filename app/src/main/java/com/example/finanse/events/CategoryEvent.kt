package com.example.finanse.events

import com.example.finanse.entities.Category
import com.example.finanse.sortTypes.CategorySortType

sealed interface CategoryEvent {
    object SaveCategory: CategoryEvent

    data class  SetName(val name: String): CategoryEvent

    object ShowDialog: CategoryEvent
    object HideDialog: CategoryEvent

    data class SortCategories(val categorySortType: CategorySortType): CategoryEvent
    data class DeleteCategory(val category: Category): CategoryEvent
}