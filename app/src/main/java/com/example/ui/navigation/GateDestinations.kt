package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Study : Screen("study", "Study")
    object Practice : Screen("practice", "Practice")
    object Progress : Screen("progress", "Analytics")
    object Profile : Screen("profile", "Profile")

    // Sub-screens
    object ExamInfo : Screen("exam_info", "Exam Information")
    object StudyPlanner : Screen("study_planner", "Study Planner")
    object SubjectDetail : Screen("subject_detail/{subjectId}", "Subject Details") {
        fun createRoute(subjectId: String) = "subject_detail/$subjectId"
    }
    object PyqPractice : Screen("pyq_practice", "PYQs")
    object MockTestTaking : Screen("mock_test/{testId}", "Mock Test") {
        fun createRoute(testId: String) = "mock_test/$testId"
    }
    object Revision : Screen("revision", "Revision System")
    object Flashcards : Screen("flashcards", "Flashcards")
    object Formulas : Screen("formulas", "Formula Sheets")
    object MistakeNotebook : Screen("mistake_notebook", "Mistake Notebook")
    object Notes : Screen("notes", "My Notes")
    object Search : Screen("search", "Search")
    object AiRecommendations : Screen("ai_recommendations", "AI Insights")
    object Settings : Screen("settings", "Settings")
}
