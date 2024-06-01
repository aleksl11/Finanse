package com.example.finanse.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.finanse.TopNavBar

@Composable
fun SummaryScreen(navController: NavController){
    Column{
        TopNavBar(navController, "summary","menu")
        LazyColumn {
            item {Text("Test")}
        }
    }
}

