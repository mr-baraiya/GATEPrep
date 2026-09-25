package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag

data class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

val bottomNavItems = listOf(
    NavItem(Screen.Home.route, "Home", Icons.Default.Home, "tab_home"),
    NavItem(Screen.Study.route, "Study", Icons.Default.MenuBook, "tab_study"),
    NavItem(Screen.Practice.route, "Practice", Icons.Default.Quiz, "tab_practice"),
    NavItem(Screen.Progress.route, "Analytics", Icons.Default.Insights, "tab_progress"),
    NavItem(Screen.Profile.route, "Profile", Icons.Default.Person, "tab_profile")
)

@Composable
fun GateBottomNavBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToRoute(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
