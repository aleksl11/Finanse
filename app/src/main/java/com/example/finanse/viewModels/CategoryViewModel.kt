package com.example.finanse.viewModels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.dao.CategoryDao
import com.example.finanse.entities.Category
import com.example.finanse.events.CategoryEvent
import com.example.finanse.sortTypes.CategorySortType
import com.example.finanse.states.CategoryState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val dao: CategoryDao
): ViewModel() {

    private val _categorySortType = MutableStateFlow(CategorySortType.DATE_ADDED)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _categories = _categorySortType
        .flatMapLatest { categorySortType ->
            when(categorySortType){
                CategorySortType.DATE_ADDED ->dao.getCategories()
                CategorySortType.NAME -> dao.getCategoriesOrderedByName()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(CategoryState())
    val state = combine(_state, _categorySortType, _categories) {state, categorySortType, category ->
        state.copy(
            category = category,
            categorySortType = categorySortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryState())


    fun onEvent(event: CategoryEvent){
        when(event){
            is CategoryEvent.DeleteCategory -> {
                viewModelScope.launch {
                    dao.deleteCategory(event.category)
                }
            }
            CategoryEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingCategory = false
                )}
            }
            CategoryEvent.SaveCategory -> {
                val name = state.value.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                val color = state.value.color

                if(name.isBlank()){
                    return
                }

                val category = Category(
                    name = name,
                    color = color
                )
                viewModelScope.launch {
                    dao.insertCategory(category)
                }
                _state.update{it.copy(
                    isAddingCategory = false,
                    name = "",
                    color = Color.White.hashCode()
                )}
            }
            is CategoryEvent.GetData -> {
                val name = event.name
                Thread {
                    _state.update { it.copy(
                        name = name,
                        color = dao.getColor(name),
                    )}
                }.start()
            }
            is CategoryEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is CategoryEvent.SetColor -> {
                _state.update { it.copy(
                    color = event.color
                ) }
            }
            is CategoryEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingCategory = true
                )}
            }
            is CategoryEvent.SortCategories -> {
                _categorySortType.value = event.categorySortType
            }
        }
    }
}