package com.example.csci215_final

import androidx.compose.runtime.Composable
import com.example.csci215_final.ui.navigation.AppNavigation
import com.example.csci215_final.ui.theme.TodoAppTheme

@Composable
fun App() {
    TodoAppTheme {
        AppNavigation()
    }
}
