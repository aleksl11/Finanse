package com.example.finanse.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.InternalStorage
import com.example.finanse.dao.AccountDao
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Income
import com.example.finanse.events.IncomeEvent
import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.states.IncomeState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IncomeViewModel(
    private val dao: IncomeDao,
    private val accountDao: AccountDao
): ViewModel() {

    private val _incomeSortType = MutableStateFlow(IncomeSortType.DATE_ADDED)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _incomes = _incomeSortType
        .flatMapLatest { incomeSortType ->
            when(incomeSortType){
                IncomeSortType.DATE_ADDED ->dao.getIncomesOrderedById()
                IncomeSortType.AMOUNT -> dao.getIncomesOrderedByAmount()
                IncomeSortType.DATE_OF_INCOME -> dao.getIncomesOrderedByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(IncomeState())
    val state = combine(_state, _incomeSortType, _incomes) {state, incomeSortType, income ->
        state.copy(
            income = income,
            incomeSortType = incomeSortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IncomeState())

    fun onEvent(event: IncomeEvent){
        when(event){
            is IncomeEvent.DeleteIncome -> {
                viewModelScope.launch {
                    val amount = withContext(Dispatchers.IO) {
                        dao.getAmount(event.income.id)

                    }
                    val account = withContext(Dispatchers.IO) {
                        dao.getAccount(event.income.id)
                    }
                    withContext(Dispatchers.IO) {
                        accountDao.updateAccountBalance(-amount, account)
                    }
                    dao.deleteIncome(event.income)
                }
            }
            is IncomeEvent.HideDialog -> {
                val cacheDir = event.context.cacheDir
                InternalStorage().cleanCache(cacheDir)
                _state.update{it.copy(
                    isAddingIncome = false,
                    amount = "",
                    title = "",
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    account = "",
                    description = null,
                    photoPaths = null,
                    id = -1
                )}
            }
            IncomeEvent.SaveIncome -> {
                val amount = state.value.amount.toDouble()
                val title = state.value.title
                val date = state.value.date
                val description = state.value.description
                val account = state.value.account.toInt()
                val photoPaths = state.value.photoPaths

                if(amount.isNaN() || title.isBlank()){
                    return
                }
                Log.d("IncomeDebug", "PhotoPaths: ${state.value.photoPaths}")
                val photosJson = if (photoPaths?.isNotEmpty() == true) Gson().toJson(photoPaths) else null

                if (state.value.id == -1) {
                    val income = Income(
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        account = account,
                        description = description,
                        photos = photosJson
                    )
                    viewModelScope.launch {
                        dao.insertIncome(income)
                        withContext(Dispatchers.IO) {
                            accountDao.updateAccountBalance(amount, account)
                        }
                    }
                }else {
                    val income = Income(
                        id = state.value.id,
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        account = account,
                        description = description,
                        photos = photosJson
                    )
                    viewModelScope.launch {
                        val difference = withContext(Dispatchers.IO) {
                            val previousAmount = dao.getAmount(state.value.id)
                            amount - previousAmount
                        }
                        withContext(Dispatchers.IO) {
                            accountDao.updateAccountBalance(difference, account)
                        }
                        dao.insertIncome(income)
                    }
                }
                _state.update{it.copy(
                    isAddingIncome = false,
                    amount = "",
                    title = "",
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    account = "",
                    description = null,
                    photoPaths = null,
                    id = -1
                )}
            }
            is IncomeEvent.GetData -> {
                val id = event.id
                Thread {
                    val photoString = dao.getPhotos(id)
                    val photosToList = if (!photoString.isNullOrBlank()) Gson().fromJson(photoString, Array<String>::class.java).toList() else null
                    moveIncomePhotosToCache(event.context, photosToList)
                    _state.update { it.copy(
                        title = dao.getTitle(id),
                        amount = dao.getAmount(id).toString(),
                        date = dao.getDate(id).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        description = dao.getDescription(id),
                        account = dao.getAccount(id).toString(),
                        photoPaths = null
                    )}
                }.start()
            }
            is IncomeEvent.SetId -> {
                _state.update { it.copy(
                    id = event.id
                ) }
            }
            is IncomeEvent.SetAmount -> {
                _state.update { it.copy(
                    amount = event.amount
                ) }
            }
            is IncomeEvent.SetDate -> {
                _state.update { it.copy(
                    date = event.date
                ) }
            }
            is IncomeEvent.SetAccount -> {
                _state.update { it.copy(
                    account = event.account
                ) }
            }
            is IncomeEvent.SetDescription -> {
                _state.update { it.copy(
                    description = event.description
                ) }
            }
            is IncomeEvent.SetPhotoPaths -> {
                _state.update { it.copy(
                    photoPaths = event.photoPaths
                ) }
            }
            is IncomeEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
            IncomeEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingIncome = true
                )}
            }
            is IncomeEvent.SortIncomes -> {
                _incomeSortType.value = event.incomeSortType
            }
        }
    }
}

fun moveIncomePhotosToCache(context: Context, photosToList: List<String>?) {
    val cacheDir = context.cacheDir

    photosToList?.forEach { photoPath ->
        val originalFile = File(photoPath)
        if (originalFile.exists()) {
            try {
                val newFile = File(cacheDir, originalFile.name)
                originalFile.copyTo(newFile, overwrite = true)
            } catch (e: IOException) {
                Toast.makeText(context, "Error loading photos", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}