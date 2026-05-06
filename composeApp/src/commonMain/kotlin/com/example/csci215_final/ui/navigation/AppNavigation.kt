package com.example.csci215_final.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.csci215_final.ui.screens.ArchiveScreen
import com.example.csci215_final.ui.screens.ExistingReminderScreen
import com.example.csci215_final.ui.screens.ExistingTaskScreen
import com.example.csci215_final.ui.screens.HomeScreen
import com.example.csci215_final.ui.screens.NewReminderScreen
import com.example.csci215_final.ui.screens.NewTaskScreen
import com.example.csci215_final.ui.theme.CustomRed
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Res
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import kotlin.reflect.KClass

private data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val key: KClass<*>,
    val navigate: () -> Unit,
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Track which drawer destination is active without relying on hasRoute(KClass)
    var selectedKey by remember { mutableStateOf<KClass<*>>(HomeRoute::class) }

    fun navigateAndClose(key: KClass<*>, block: () -> Unit) {
        selectedKey = key
        scope.launch { drawerState.close() }
        block()
    }

    val drawerItems = listOf(
        DrawerItem("Home", Icons.Default.Home, HomeRoute::class) {
            navigateAndClose(HomeRoute::class) {
                navController.navigate(HomeRoute) {
                    popUpTo<HomeRoute> { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        DrawerItem("New Task", Icons.Default.Add, NewTaskRoute::class) {
            navigateAndClose(NewTaskRoute::class) {
                navController.navigate(NewTaskRoute) { launchSingleTop = true }
            }
        },
        DrawerItem("New Reminder", Icons.Default.AddAlert, NewReminderRoute::class) {
            navigateAndClose(NewReminderRoute::class) {
                navController.navigate(NewReminderRoute) { launchSingleTop = true }
            }
        },
        DrawerItem("Archive", Icons.Default.Archive, ArchiveRoute::class) {
            navigateAndClose(ArchiveRoute::class) {
                navController.navigate(ArchiveRoute) { launchSingleTop = true }
            }
        },
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Check it Off", // Changed name
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily(Font(Res.font.Brownist)),
                    color = CustomRed, // Added your color
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedKey == item.key,
                        onClick = item.navigate,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToNewTask = { navController.navigate(NewTaskRoute) },
                    onNavigateToNewReminder = { navController.navigate(NewReminderRoute) },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(ExistingTaskRoute(taskId))
                    },
                    onNavigateToEditReminder = { reminderId ->
                        navController.navigate(ExistingReminderRoute(reminderId))
                    },
                )
            }

            composable<NewTaskRoute> {
                NewTaskScreen(onNavigateBack = {
                    selectedKey = HomeRoute::class
                    navController.popBackStack()
                })
            }

            composable<NewReminderRoute> {
                NewReminderScreen(onNavigateBack = {
                    selectedKey = HomeRoute::class
                    navController.popBackStack()
                })
            }

            composable<ExistingTaskRoute> { backStackEntry ->
                val route: ExistingTaskRoute = backStackEntry.toRoute()
                ExistingTaskScreen(
                    taskId = route.taskId,
                    onNavigateBack = {
                        selectedKey = HomeRoute::class
                        navController.popBackStack()
                    },
                )
            }

            composable<ExistingReminderRoute> { backStackEntry ->
                val route: ExistingReminderRoute = backStackEntry.toRoute()
                ExistingReminderScreen(
                    reminderId = route.reminderId,
                    onNavigateBack = {
                        selectedKey = HomeRoute::class
                        navController.popBackStack()
                    },
                )
            }

            composable<ArchiveRoute> {
                ArchiveScreen(
                    onNavigateBack = {
                        selectedKey = HomeRoute::class
                        navController.popBackStack()
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(ExistingTaskRoute(taskId))
                    },
                    onNavigateToEditReminder = { reminderId ->
                        navController.navigate(ExistingReminderRoute(reminderId))
                    },
                )
            }
        }
    }
}
