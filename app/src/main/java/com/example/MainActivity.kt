package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BadgesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MistakesScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.QuizActiveScreen
import com.example.ui.screens.QuizResultScreen
import com.example.ui.screens.QuizSetupScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyPlanScreen
import com.example.ui.theme.BarsamTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarsamTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val mainViewModel: MainViewModel = viewModel()
                    MainAppContent(viewModel = mainViewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val displayedScore = settings?.totalScore ?: 0

    val showBottomNav = currentScreen !is Screen.QuizActive

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen !is Screen.QuizActive) {
                AppTopBar(
                    currentScreen = currentScreen,
                    totalPoints = displayedScore,
                    onNavigateBack = { viewModel.navigateTo(Screen.Dashboard) },
                    onOpenSettings = { viewModel.navigateTo(Screen.Settings) }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                AppBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Dashboard -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                is Screen.StudyPlan -> StudyPlanScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.Practice -> PracticeScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.QuizSetup -> QuizSetupScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.QuizActive -> QuizActiveScreen(
                    viewModel = viewModel,
                    onCancel = { viewModel.cancelActiveQuiz() }
                )
                is Screen.QuizResult -> QuizResultScreen(
                    viewModel = viewModel,
                    onNavigateToSetup = { viewModel.navigateTo(Screen.QuizSetup) },
                    onNavigateToDashboard = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.Mistakes -> MistakesScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.Analytics -> AnalyticsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.Badges -> BadgesScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
                is Screen.AiAssistant -> AiAssistantScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) },
                    onOpenSettings = { viewModel.navigateTo(Screen.Settings) }
                )
                is Screen.Settings -> SettingsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateTo(Screen.Dashboard) }
                )
            }
        }
    }
}
