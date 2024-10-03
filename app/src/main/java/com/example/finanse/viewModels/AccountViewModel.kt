package com.example.finanse.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.dao.AccountDao
import com.example.finanse.entities.Account
import com.example.finanse.events.AccountEvent
import com.example.finanse.sortTypes.AccountSortType
import com.example.finanse.states.AccountState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val dao: AccountDao
): ViewModel() {

    private val _accountSortType = MutableStateFlow(AccountSortType.DATE_ADDED)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _expenses = _accountSortType
        .flatMapLatest { accountSortType ->
            when(accountSortType){
                AccountSortType.DATE_ADDED ->dao.getAccounts()
                AccountSortType.NAME -> dao.getAccountsOrderedByName()
                AccountSortType.BALANCE -> dao.getAccountsOrderedByBalance()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(AccountState())
    val state = combine(_state, _accountSortType, _expenses) {state, accountSortType, account ->
        state.copy(
            account = account,
            accountSortType = accountSortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccountState())


    fun onEvent(event: AccountEvent){
        when(event){
            is AccountEvent.DeleteAccount -> {
                viewModelScope.launch {
                    dao.deleteAccount(event.account)
                }
            }
            AccountEvent.HideDialog -> {
                _state.update{it.copy(
                    isAddingAccount = false,
                    name = "",
                    balance = ""
                )}
            }
            AccountEvent.HideTranserDialog -> {
                _state.update { it.copy(
                    isMakingATransfer = false,
                    accountTwoName = "",
                    accountOneName = "",
                    transferAmount = ""
                )}
            }
            AccountEvent.SaveAccount -> {
                val name = state.value.name
                val balance = if(state.value.balance != "")state.value.balance.toDouble() else 0.0

                if(name==""){
                    return
                }
                if (state.value.id == -1) {
                    val account = Account(
                        name = name,
                        balance = balance
                    )
                    viewModelScope.launch {
                        dao.insertAccount(account)
                    }
                }else {
                    val account = Account(
                        id = state.value.id,
                        name = name,
                        balance = balance
                    )
                    viewModelScope.launch {
                        dao.insertAccount(account)
                    }
                }

                _state.update{it.copy(
                    isAddingAccount = false,
                    name = "",
                    balance = ""
                )}
            }
            AccountEvent.MakeTransfer -> {
                viewModelScope.launch {
                    try {
                        // Perform the transfer in sequence (part one and part two)
                        dao.makeTransferPartOne(state.value.accountOneName, state.value.transferAmount)
                        dao.makeTransferPartTwo(state.value.accountTwoName, state.value.transferAmount)
                    } catch (e: Exception) {
                        // Handle error if needed
                    } finally {
                        // Update the state once the transfer completes
                        _state.update { it.copy(
                            isMakingATransfer = false,
                            accountTwoName = "",
                            accountOneName = "",
                            transferAmount = ""
                        )}
                    }
                }
            }
            is AccountEvent.SetId -> {
                _state.update { it.copy(
                    id = event.id
                ) }
            }
            is AccountEvent.SetBalance -> {
                _state.update { it.copy(
                    balance = event.balance
                ) }
            }
            is AccountEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is AccountEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingAccount = true
                )}
            }
            is AccountEvent.ShowTranserDialog -> {
                _state.update { it.copy(
                    isMakingATransfer = true
                ) }
            }
            is AccountEvent.SortAccounts -> {
                _accountSortType.value = event.accountSortType
            }
            is AccountEvent.SetAccountOneName -> {
                _state.update { it.copy(
                    accountOneName = event.name
                ) }
            }
            is AccountEvent.SetAccountTwoName -> {
                _state.update { it.copy(
                    accountTwoName = event.name
                ) }
            }
            is AccountEvent.SetTransferAmount -> {
                _state.update { it.copy(
                    transferAmount = event.amount
                ) }
            }

            is AccountEvent.GetData -> {
                val id = event.id
                Thread {
                    _state.update { it.copy(
                        name = dao.getName(id),
                        balance = dao.getBalance(id).toString(),
                    )}
                }.start()
            }
        }
    }
}
