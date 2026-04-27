package com.example.csci215_final.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.csci215_final.ui.screens.ExistingReminderScreen
import com.example.csci215_final.ui.screens.ExistingTaskScreen
import com.example.csci215_final.ui.screens.HelperDistractionScreen
import com.example.csci215_final.ui.screens.HomeScreen
import com.example.csci215_final.ui.screens.NewReminderScreen
import com.example.csci215_final.ui.screens.NewTaskScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val drawerItems = listOf(
        Triple("Home", Icons.Default.Home, Screen.Home.route),
        Triple("New Task", Icons.Default.Add, Screen.NewTask.route),
        Triple("New Reminder", Icons.Default.AddAlert, Screen.NewReminder.route),
        Triple("Helpers & Distractions", Icons.AutoMirrored.Filled.List, Screen.HelperDistraction.route),
    )

    fun navigateAndClose(route: String) {
        scope.launch { drawerState.close() }
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Focus Todo",
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                drawerItems.forEach { (label, icon, route) ->
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentRoute == route,
                        onClick = { navigateAndClose(route) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToNewTask = { navController.navigate(Screen.NewTask.route) },
                    onNavigateToNewReminder = { navController.navigate(Screen.NewReminder.route) },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(Screen.ExistingTask.createRoute(taskId))
                    },
                    onNavigateToEditReminder = { reminderId ->
                        navController.navigate(Screen.ExistingReminder.createRoute(reminderId))
                    }
                )
            }

            composable(Screen.NewTask.route) {
                NewTaskScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NewReminder.route) {
                NewReminderScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ExistingTask.route,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
                ExistingTaskScreen(
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ExistingReminder.route,
                arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val reminderId = backStackEntry.arguments?.getLong("reminderId") ?: return@composable
                ExistingReminderScreen(
                    reminderId = reminderId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.HelperDistraction.route) {
                HelperDistractionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
