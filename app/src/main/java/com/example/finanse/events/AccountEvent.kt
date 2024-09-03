package com.example.finanse.events

import com.example.finanse.entities.Account
import com.example.finanse.sortTypes.AccountSortType

sealed interface AccountEvent {
    object SaveAccount: AccountEvent
    data class  SetName(val name: String): AccountEvent
    data class  SetId(val id: Int): AccountEvent
    data class  SetBalance(val balance: String): AccountEvent

    object ShowDialog: AccountEvent
    object HideDialog: AccountEvent

    data class GetData(val id: Int): AccountEvent
    data class SortAccounts(val accountSortType: AccountSortType): AccountEvent
    data class DeleteAccount(val account: Account): AccountEvent
}