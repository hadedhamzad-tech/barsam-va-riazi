package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary

@Composable
fun AppBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Dashboard,
            onClick = { onNavigate(Screen.Dashboard) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "داشبورد") },
            label = { Text("داشبورد", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = JadePrimary,
                selectedTextColor = JadePrimary,
                indicatorColor = JadePrimary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Practice,
            onClick = { onNavigate(Screen.Practice) },
            icon = { Icon(Icons.Default.Calculate, contentDescription = "تمرین") },
            label = { Text("تمرین", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = JadePrimary,
                selectedTextColor = JadePrimary,
                indicatorColor = JadePrimary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.QuizSetup || currentScreen is Screen.QuizActive || currentScreen is Screen.QuizResult,
            onClick = { onNavigate(Screen.QuizSetup) },
            icon = { Icon(Icons.Default.Quiz, contentDescription = "آزمون") },
            label = { Text("آزمون", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = JadePrimary,
                selectedTextColor = JadePrimary,
                indicatorColor = JadePrimary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Mistakes,
            onClick = { onNavigate(Screen.Mistakes) },
            icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "اشتباهات") },
            label = { Text("اشتباهات", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = JadePrimary,
                selectedTextColor = JadePrimary,
                indicatorColor = JadePrimary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Analytics,
            onClick = { onNavigate(Screen.Analytics) },
            icon = { Icon(Icons.Default.AutoGraph, contentDescription = "تحلیل") },
            label = { Text("تحلیل", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = JadePrimary,
                selectedTextColor = JadePrimary,
                indicatorColor = JadePrimary.copy(alpha = 0.12f)
            )
        )
    }
}
