package com.example.finanse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finanse.screens.AccountsScreen
import com.example.finanse.screens.CategoriesScreen
import com.example.finanse.screens.ExpensesScreen
import com.example.finanse.screens.IncomesScreen
import com.example.finanse.screens.Menu
import com.example.finanse.screens.SettingsScreen
import com.example.finanse.screens.SummaryScreen
import com.example.finanse.ui.theme.FinanseTheme
import com.example.finanse.ui.theme.ThemeMode
import com.example.finanse.ui.theme.getThemeMode
import com.example.finanse.ui.theme.saveThemeMode
import com.example.finanse.viewModels.AccountViewModel
import com.example.finanse.viewModels.CategoryViewModel
import com.example.finanse.viewModels.ExpenseViewModel
import com.example.finanse.viewModels.IncomeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy{
        MainDatabase.getInstance(applicationContext)
    }

    private val incomeViewModel by viewModels<IncomeViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IncomeViewModel(db.incomeDao(), db.accountDao()) as T
                }
            }
        }
    )

    private val accountViewModel by viewModels<AccountViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AccountViewModel(db.accountDao()) as T
                }
            }
        }
    )

    private val expenseViewModel by viewModels<ExpenseViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(db.expenseDao(), db.accountDao()) as T
                }
            }
        }
    )

    private val categoryViewModel by viewModels<CategoryViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CategoryViewModel(db.categoryDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val themeModeFlow = getThemeMode(context)
            val themeMode by themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)

            FinanseTheme(themeMode = themeMode) {
                val incomeState by incomeViewModel.state.collectAsState()
                val expenseState by expenseViewModel.state.collectAsState()
                val categoryState by categoryViewModel.state.collectAsState()
                val accountState by accountViewModel.state.collectAsState()
                val coroutineScope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "menu" ){
                        composable("menu"){
                            Menu(navController)
                        }
                        composable("summary"){
                            SummaryScreen(navController, incomeState, expenseState, categoryState)
                        }
                        composable("incomes"){
                            IncomesScreen(navController, incomeState, accountState, incomeViewModel::onEvent)
                        }
                        composable("expenses"){
                            ExpensesScreen(navController, expenseState, categoryState, accountState, expenseViewModel::onEvent)
                        }
                        composable("categories"){
                            CategoriesScreen(navController, categoryState, categoryViewModel::onEvent)
                        }
                        composable("settings"){
                            SettingsScreen(navController, themeMode) { selectedTheme ->
                                coroutineScope.launch {
                                    saveThemeMode(context, selectedTheme) // Save the selected theme
                                }
                            }
                        }
                        composable("account"){
                            AccountsScreen(navController, accountState, accountViewModel::onEvent)
                        }
                    }
                }
            }
        }
    }
}


