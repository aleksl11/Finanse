package com.example.finanse.states

import com.example.finanse.entities.Account
import com.example.finanse.sortTypes.AccountSortType

data class AccountState(
    val account: List<Account> = emptyList(),
    val id: Int = -1,
    val name: String = "",
    val balance: String = "",
    val isAddingAccount: Boolean = false,
    val isMakingATransfer: Boolean = false,
    val accountSortType: AccountSortType = AccountSortType.DATE_ADDED,
    val accountOneName: String = "",
    val accountTwoName: String = "",
    val transferAmount: String = "",
)
