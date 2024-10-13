package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.finanse.entities.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Upsert
    suspend fun insertAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("SELECT name FROM account where id = :id")
    fun getName(id: Int): String

    @Query("SELECT balance FROM account where id = :id")
    fun getBalance(id: Int): Int

    @Query("SELECT * FROM account")
    fun getAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM account ORDER BY name ASC")
    fun getAccountsOrderedByName(): Flow<List<Account>>

    @Query("SELECT * FROM account ORDER BY balance ASC")
    fun getAccountsOrderedByBalance(): Flow<List<Account>>

    @Query("UPDATE account SET balance = balance + :amount where id = :accountId")
    fun updateAccountBalance(amount: Double, accountId: Int)

    @Query("UPDATE account SET balance = balance - :transferAmount where name = :accountOneName ")
    suspend fun makeTransferPartOne(accountOneName: String, transferAmount: String)
    @Query("UPDATE account SET balance = balance + :transferAmount where name = :accountTwoName ")
    suspend fun makeTransferPartTwo(accountTwoName: String, transferAmount: String)

    @Query("SELECT id from account where name = :accountName")
    suspend fun getAccountIdByName(accountName: String): Int
}