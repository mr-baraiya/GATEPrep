package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.navigation.GateBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.navigation.bottomNavItems
import com.example.ui.screens.ai.AiRecommendationScreen
import com.example.ui.screens.examinfo.ExamInfoScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.practice.MockTestScreen
import com.example.ui.screens.practice.PracticeScreen
import com.example.ui.screens.practice.PyqPracticeScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.revision.FlashcardsScreen
import com.example.ui.screens.revision.FormulaSheetsScreen
import com.example.ui.screens.revision.MistakeNotebookScreen
import com.example.ui.screens.revision.RevisionScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.study.StudyPlannerScreen
import com.example.ui.screens.study.StudySubjectsScreen
import com.example.ui.theme.GateTheme
import com.example.ui.viewmodel.GateViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GateViewModel = viewModel()
            val userStats by viewModel.userStats.collectAsStateWithLifecycle()

            val isDarkTheme = when (userStats?.themeMode ?: "SYSTEM") {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            GateTheme(darkTheme = isDarkTheme) {
                GateApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GateApp(viewModel: GateViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                GateBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main Bottom Bar Screens
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToExamInfo = { navController.navigate(Screen.ExamInfo.route) },
                    onNavigateToStudyPlanner = { navController.navigate(Screen.StudyPlanner.route) },
                    onNavigateToPyq = { navController.navigate(Screen.PyqPractice.route) },
                    onNavigateToMockTest = { navController.navigate(Screen.Practice.route) },
                    onNavigateToRevision = { navController.navigate(Screen.Revision.route) },
                    onNavigateToFormulas = { navController.navigate(Screen.Formulas.route) },
                    onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToAiRecs = { navController.navigate(Screen.AiRecommendations.route) }
                )
            }

            composable(Screen.Study.route) {
                StudySubjectsScreen(
                    viewModel = viewModel,
                    onNavigateToPlanner = { navController.navigate(Screen.StudyPlanner.route) }
                )
            }

            composable(Screen.Practice.route) {
                PracticeScreen(
                    viewModel = viewModel,
                    onNavigateToPyq = { navController.navigate(Screen.PyqPractice.route) },
                    onNavigateToMockTest = { testId -> navController.navigate(Screen.MockTestTaking.createRoute(testId)) },
                    onNavigateToMistakes = { navController.navigate(Screen.MistakeNotebook.route) }
                )
            }

            composable(Screen.Progress.route) {
                ProgressScreen(
                    viewModel = viewModel,
                    onNavigateToAiRecs = { navController.navigate(Screen.AiRecommendations.route) }
                )
            }

            composable(Screen.Profile.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Secondary / Detail Screens
            composable(Screen.ExamInfo.route) {
                ExamInfoScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.StudyPlanner.route) {
                StudyPlannerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PyqPractice.route) {
                PyqPracticeScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.MockTestTaking.route,
                arguments = listOf(navArgument("testId") { type = NavType.StringType })
            ) { backStackEntry ->
                val testId = backStackEntry.arguments?.getString("testId") ?: "mock_cse_fl_1"
                MockTestScreen(
                    testId = testId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Revision.route) {
                RevisionScreen(
                    viewModel = viewModel,
                    onNavigateToFlashcards = { navController.navigate(Screen.Flashcards.route) },
                    onNavigateToFormulas = { navController.navigate(Screen.Formulas.route) },
                    onNavigateToMistakes = { navController.navigate(Screen.MistakeNotebook.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Flashcards.route) {
                FlashcardsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Formulas.route) {
                FormulaSheetsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.MistakeNotebook.route) {
                MistakeNotebookScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Notes.route) {
                NotesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Search.route) {
                GlobalSearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPyq = { navController.navigate(Screen.PyqPractice.route) },
                    onNavigateToFormulas = { navController.navigate(Screen.Formulas.route) },
                    onNavigateToNotes = { navController.navigate(Screen.Notes.route) }
                )
            }

            composable(Screen.AiRecommendations.route) {
                AiRecommendationScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPyq = { navController.navigate(Screen.PyqPractice.route) },
                    onNavigateToStudyPlanner = { navController.navigate(Screen.StudyPlanner.route) }
                )
            }
        }
    }
}
