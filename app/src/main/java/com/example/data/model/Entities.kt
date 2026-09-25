package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val stream: String, // "CSE", "DA"
    val name: String,
    val code: String,
    val weightagePercent: Int,
    val iconName: String,
    val description: String
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val stream: String,
    val title: String,
    val subtopics: String, // comma-separated or brief text
    val isCompleted: Boolean = false,
    val studyMinutesLogged: Int = 0,
    val revisionCount: Int = 0,
    val isBookmarked: Boolean = false,
    val difficultyLevel: String = "MODERATE", // "WEAK", "MODERATE", "STRONG"
    val isImportant: Boolean = true
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val stream: String, // "CSE", "DA"
    val subjectId: String,
    val subjectName: String,
    val topic: String,
    val year: Int,
    val questionType: String, // "MCQ", "MSQ", "NAT"
    val questionText: String,
    val optionsJson: String, // JSON array: ["A. ...", "B. ...", "C. ...", "D. ..."] or empty for NAT
    val correctAnswersJson: String, // JSON array: ["A"] or ["A", "C"] or ["42.0"] for NAT
    val explanation: String,
    val marks: Int = 1,
    val negativeMarks: Float = 0.33f,
    val difficulty: String = "MEDIUM", // "EASY", "MEDIUM", "HARD"
    val userSelectedAnswersJson: String = "",
    val isAttempted: Boolean = false,
    val isCorrect: Boolean = false,
    val isBookmarked: Boolean = false,
    val isMistake: Boolean = false,
    val timeSpentSeconds: Int = 0
)

@Entity(tableName = "mock_tests")
data class MockTestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val stream: String,
    val testType: String, // "FULL_LENGTH", "SUBJECT_WISE", "MINI_MOCK"
    val totalQuestions: Int,
    val totalMarks: Int,
    val durationMinutes: Int,
    val completedAtMillis: Long = 0L,
    val isCompleted: Boolean = false,
    val score: Float = 0f,
    val accuracyPercent: Float = 0f,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unattemptedCount: Int = 0,
    val questionIdsJson: String = ""
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val stream: String,
    val subjectName: String,
    val topicTitle: String,
    val targetDateMillis: Long,
    val durationMinutes: Int = 60,
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val notes: String = ""
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val stream: String,
    val subjectName: String,
    val title: String,
    val content: String,
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey val id: String,
    val stream: String,
    val subjectName: String,
    val topic: String,
    val title: String,
    val formulaText: String,
    val explanation: String,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "exam_milestones")
data class ExamMilestoneEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val eventDateMillis: Long,
    val dateDisplay: String,
    val category: String, // "APPLICATION", "ADMIT_CARD", "EXAM", "RESULT"
    val isReminderEnabled: Boolean = true
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val targetStream: String = "BOTH", // "CSE", "DA", "BOTH"
    val dailyStudyGoalMinutes: Int = 180, // 3 hours default
    val currentStreakDays: Int = 3,
    val lastStudyDateString: String = "",
    val totalStudyMinutes: Int = 720,
    val targetRank: Int = 100,
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val enableDailyReminder: Boolean = true,
    val dailyReminderHour: Int = 20, // 8 PM
    val dailyReminderMinute: Int = 0,
    val enableExamAlerts: Boolean = true,
    val enableRevisionAlerts: Boolean = true
)
