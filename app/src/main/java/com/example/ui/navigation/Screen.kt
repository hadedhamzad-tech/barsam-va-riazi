package com.example.ui.navigation

sealed class Screen(val title: String) {
    object Dashboard : Screen("برسام و ریاضی")
    object StudyPlan : Screen("برنامه مطالعه")
    object Practice : Screen("تمرین و فرمول‌نامه")
    object QuizSetup : Screen("آزمون‌ساز سفارشی")
    object QuizActive : Screen("در حال آزمون")
    object QuizResult : Screen("کارنامه آزمون")
    object Mistakes : Screen("دفترچه اشتباهات")
    object Analytics : Screen("تحلیل عملکرد")
    object Badges : Screen("تالار مدال‌ها")
    object AiAssistant : Screen("دستیار هوشمند")
    object Settings : Screen("تنظیمات")
}
