package com.example.finanse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.finanse.screens.CategoriesScreen
import com.example.finanse.screens.ExpensesScreen
import com.example.finanse.screens.IncomesScreen
import com.example.finanse.ui.theme.FinanseTheme

import com.example.finanse.screens.SummaryScreen
import com.example.finanse.screens.Menu
import com.example.finanse.screens.SettingsScreen

class MainActivity : ComponentActivity() {

    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            MainDatabase::class.java,
            "main.db"
        ).build()
    }

    private val viewModel by viewModels<IncomeViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return IncomeViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanseTheme {
                val state by viewModel.state.collectAsState()
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
                            SummaryScreen(navController)
                        }
                        composable("incomes"){
                            IncomesScreen(navController, state, viewModel::onEvent)
                        }
                        composable("expenses"){
                            ExpensesScreen(navController)
                        }
                        composable("categories"){
                            CategoriesScreen(navController)
                        }
                        composable("settings"){
                            SettingsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}


