package com.example.finanse

import com.example.finanse.entities.Account

class DisplayFormat {

    fun getAccountName(accountId: String, accounts: List<Account>): String{
        val id: Int
        if(accountId.isEmpty()) return ""
        else id = accountId.toInt()
        accounts.forEach { a ->
            if(a.id == id) return a.name
        }
        return ""
    }
}