package com.example.finanse.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.ConfirmPopup
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.ValidateInputs
import com.example.finanse.entities.Account
import com.example.finanse.events.AccountEvent
import com.example.finanse.sortTypes.AccountSortType
import com.example.finanse.states.AccountState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    navController: NavController,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit
){
    val context = LocalContext.current
    Scaffold (
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End, // Align to the end of the screen
                verticalArrangement = Arrangement.spacedBy(16.dp) // Add space between buttons
            ) {
                FloatingActionButton(
                    onClick = { onEvent(AccountEvent.ShowTranserDialog) },
                    shape = MaterialTheme.shapes.medium,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(painter = painterResource(R.drawable.transfer_icon), contentDescription = stringResource(R.string.make_a_transfer_desc), modifier = Modifier.size(24.dp))
                }
                FloatingActionButton(
                    onClick = { onEvent(AccountEvent.ShowDialog) },
                    shape = MaterialTheme.shapes.medium,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_account_desc))
                }
            }
        },
    ) {padding ->
        if(state.isAddingAccount) {
            AddAccountDialog(context, state = state, onEvent = onEvent)
        }
        if(state.isMakingATransfer) {
            MakeTransferDialog(context, state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                TopNavBar(navController, "Accounts","menu")
            }
            item {
                var expanded by remember { mutableStateOf(false) } // Control dropdown menu state
                var selectedSortType by remember { mutableStateOf(state.accountSortType) } // Track selected sort type

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp) // Add padding around sorting options
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Align items vertically centered
                        modifier = Modifier.padding(vertical = 8.dp) // Space around the row
                    ) {
                        Text(
                            text = stringResource(R.string.sort_by) + ": ", // Label for the dropdown
                            fontSize = 16.sp, // Font size
                            modifier = Modifier.padding(end = 8.dp) // Space between label and dropdown
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded } // Toggle dropdown
                        ) {
                            // The TextField that shows the currently selected sort type
                            OutlinedTextField(
                                value = getSortTypeName(context, selectedSortType), // Show the selected sort type's name
                                onValueChange = {},
                                readOnly = true, // Prevent editing
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor() // Open dropdown when clicked
                            )

                            // The DropdownMenu with all sorting options
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false } // Close the dropdown menu
                            ) {
                                AccountSortType.entries.forEach { accountSortType ->
                                    DropdownMenuItem(
                                        text = { Text(text = getSortTypeName(context, accountSortType)) },
                                        onClick = {
                                            selectedSortType = accountSortType
                                            onEvent(AccountEvent.SortAccounts(accountSortType)) // Trigger event on selection
                                            expanded = false // Close dropdown
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            items(state.account){account ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${account.name}: ${account.balance}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = {
                            onEvent(AccountEvent.GetData(account.id))
                            onEvent(AccountEvent.ShowDialog)
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.edit_account_desc))
                        }
                        if (numberOfAccounts(state) > 1) {
                            ConfirmPopup().DeleteIconButton(stringResource(R.string.delete_name), stringResource(R.string.delete_message)) {
                                onEvent(AccountEvent.DeleteAccount(account))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    context: Context,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
){
    val text = remember { mutableStateOf("") }
    val mAccounts = state.account
    val validate = ValidateInputs()

    BasicAlertDialog(onDismissRequest = { onEvent(AccountEvent.HideDialog) }) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.add_account_dialog), fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(AccountEvent.SetName(it)) },
                label = { Text(text = stringResource(R.string.name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.balance,
                onValueChange = {
                    if (validate.isAmountValid(it)) onEvent(AccountEvent.SetBalance(it))
                },
                label = { Text(text = stringResource(R.string.balance_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (state.name.isEmpty())
                            text.value = context.getString(R.string.no_name_error)
                        else if (state.id == -1 && isNameInDb(state.name, mAccounts))
                            text.value = context.getString(R.string.repeat_account_error)
                        else if (state.balance.isEmpty())
                            text.value = context.getString(R.string.no_balance_error)
                        else {
                            text.value = ""
                            onEvent(AccountEvent.SaveAccount)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.save))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { onEvent(AccountEvent.HideDialog) }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.cancel))
                }
            }

            if (text.value.isNotEmpty()) {
                Text(text = text.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeTransferDialog(
    context: Context,
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
){
    val text = remember { mutableStateOf("") }
    val maxTransfer = remember {mutableStateOf(0.0)}
    val mAccounts = state.account
    var expandedAccountOne by remember { mutableStateOf(false) }
    var expandedAccountTwo by remember { mutableStateOf(false) }
    val iconOne = if (expandedAccountOne)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    val iconTwo = if (expandedAccountTwo)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    val validate = ValidateInputs()

    BasicAlertDialog(onDismissRequest = { onEvent(AccountEvent.HideTranserDialog) }) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.make_a_transfer_desc), fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = state.transferAmount,
                onValueChange = {
                    if (validate.isAmountValid(it)) onEvent(AccountEvent.SetTransferAmount(it))
                },
                label = { Text(text = stringResource(R.string.amount_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.accountOneName,
                onValueChange = {},
                label = { Text(stringResource(R.string.account_one_label)) },
                trailingIcon = {
                    Icon(iconOne, "drop down menu arrow",
                        Modifier.clickable { expandedAccountOne = !expandedAccountOne })
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedAccountOne,
                onDismissRequest = { expandedAccountOne = false }
            ) {
                mAccounts.forEach { a ->
                    DropdownMenuItem(onClick = {
                        expandedAccountOne = false
                        maxTransfer.value = a.balance
                        onEvent(AccountEvent.SetAccountOneName(a.name))
                    },
                        text = { Text(text = a.name) }
                    )
                }
            }
            OutlinedTextField(
                value = state.accountTwoName,
                onValueChange = {},
                label = { Text(stringResource(R.string.account_two_label)) },
                trailingIcon = {
                    Icon(iconTwo, "drop down menu arrow",
                        Modifier.clickable { expandedAccountTwo = !expandedAccountTwo })
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedAccountTwo,
                onDismissRequest = { expandedAccountTwo = false }
            ) {
                mAccounts.forEach { a ->
                    DropdownMenuItem(onClick = {
                        expandedAccountTwo = false
                        onEvent(AccountEvent.SetAccountTwoName(a.name))
                    },
                        text = { Text(text = a.name) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (state.accountOneName == state.accountTwoName) {
                            text.value = context.getString(R.string.same_account_error)
                        }
                        else if (state.transferAmount.toDouble() > maxTransfer.value){
                            text.value = context.getString(R.string.not_enough_balance_error, state.accountOneName)
                        }
                        else {
                            text.value = ""
                            onEvent(AccountEvent.MakeTransfer)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.confirm_transfer))
                }
                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { onEvent(AccountEvent.HideTranserDialog) }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.cancel))
                }
            }

            if (text.value.isNotEmpty()) {
                Text(text = text.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
fun getSortTypeName(context: Context, name: AccountSortType): String{
    return when (name) {
        AccountSortType.NAME-> context.getString(R.string.sort_by_name)
        AccountSortType.DATE_ADDED -> context.getString(R.string.sort_by_default)
        AccountSortType.BALANCE -> context.getString(R.string.sort_by_balance)
    }
}

fun isNameInDb(name: String, mAccounts: List<Account>): Boolean{
    var check = false
    mAccounts.forEach{ a ->
        if (a.name == name ) {
            check = true
        }
    }
    return check
}

fun numberOfAccounts(state: AccountState): Int{
    val mAccounts = state.account
    return mAccounts.size
}