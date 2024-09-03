package com.example.finanse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.TopNavBar
import com.example.finanse.events.AccountEvent
import com.example.finanse.sortTypes.AccountSortType
import com.example.finanse.states.AccountState


@Composable
fun AccountsScreen(
    navController: NavController,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(AccountEvent.ShowDialog)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add an account")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {padding ->
        if(state.isAddingAccount) {
            AddAccountDialog(state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                TopNavBar(navController, "Accounts","menu")
            }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AccountSortType.entries.forEach { accountSortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(AccountEvent.SortAccounts(accountSortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            RadioButton(selected = state.accountSortType == accountSortType,
                                onClick = {
                                    onEvent(AccountEvent.SortAccounts(accountSortType))
                                }
                            )
                            Text(text = getSortTypeName(accountSortType))
                        }
                    }
                }
            }
            items(state.account){account ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column {
                        Text(text = "${account.name}: ${account.balance}", fontSize = 20.sp)
                    }
                    IconButton(onClick = {
                        onEvent(AccountEvent.GetData(account.id))
                        onEvent(AccountEvent.ShowDialog)
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit account")
                    }
                    IconButton(onClick = {
                        onEvent(AccountEvent.DeleteAccount(account))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete account"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
){
    val text = remember { mutableStateOf("") }
    BasicAlertDialog(onDismissRequest = { onEvent(AccountEvent.HideDialog) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(Color.Gray)
                .padding(8.dp)
        ) {
            Text(text = "Account")
            TextField(
                value = state.name,
                onValueChange = {
                    onEvent(AccountEvent.SetName(it))
                },
                placeholder = {
                    Text(text = "Name")
                },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = state.balance,
                onValueChange = {
                    onEvent(AccountEvent.SetBalance(it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = {
                    Text(text = "0.00")
                }
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    if (state.name == "") {
                        text.value = "Category must have a name"
                    } else {
                        text.value = ""
                        onEvent(AccountEvent.SaveAccount)
                    }
                }) {
                    Text(text = "Save")
                }
            }
            Text(text = text.value, color = Color.Red)
        }
    }
}
fun getSortTypeName(name: AccountSortType): String{
    return when (name) {
        AccountSortType.NAME-> "Name"
        AccountSortType.DATE_ADDED -> "Default"
        AccountSortType.BALANCE -> "Balance"
    }
}