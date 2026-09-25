package com.example.data.repository

import com.example.data.local.GateDao
import com.example.data.local.InitialData
import com.example.data.model.ExamMilestoneEntity
import com.example.data.model.FormulaEntity
import com.example.data.model.MockTestEntity
import com.example.data.model.NoteEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.data.model.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GateRepository(private val dao: GateDao) {

    suspend fun initializeDatabaseIfEmpty() {
        val existingStats = dao.getUserStats().firstOrNull()
        if (existingStats == null) {
            dao.insertOrUpdateUserStats(InitialData.defaultUserStats)
            dao.insertSubjects(InitialData.getSubjects())
            dao.insertTopics(InitialData.getTopics())
            dao.insertQuestions(InitialData.getQuestions())
            dao.insertMockTests(InitialData.getMockTests())
            dao.insertFormulas(InitialData.getFormulas())
            dao.insertMilestones(InitialData.getMilestones())
        }
    }

    // Subjects & Topics
    fun getSubjects(stream: String): Flow<List<SubjectEntity>> = dao.getSubjects(stream)

    fun getTopics(stream: String): Flow<List<TopicEntity>> = dao.getTopics(stream)

    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>> = dao.getTopicsForSubject(subjectId)

    suspend fun setTopicCompleted(topicId: String, completed: Boolean) {
        dao.setTopicCompleted(topicId, completed)
    }

    suspend fun incrementTopicRevision(topicId: String) {
        dao.incrementTopicRevision(topicId)
    }

    suspend fun updateTopicDifficulty(topicId: String, difficulty: String) {
        dao.updateTopicDifficulty(topicId, difficulty)
    }

    suspend fun updateTopic(topic: TopicEntity) {
        dao.updateTopic(topic)
    }

    // Questions
    fun getAllQuestions(stream: String): Flow<List<QuestionEntity>> = dao.getAllQuestions(stream)

    suspend fun getQuestionById(id: String): QuestionEntity? = dao.getQuestionById(id)

    fun getBookmarkedQuestions(): Flow<List<QuestionEntity>> = dao.getBookmarkedQuestions()

    fun getMistakeQuestions(): Flow<List<QuestionEntity>> = dao.getMistakeQuestions()

    suspend fun toggleQuestionBookmark(questionId: String, currentStatus: Boolean) {
        dao.setQuestionBookmarked(questionId, !currentStatus)
    }

    suspend fun submitQuestionAnswer(
        question: QuestionEntity,
        selectedAnswers: List<String>,
        timeSpentSeconds: Int
    ): Boolean {
        // parse correct answers
        val correctAnswers = question.correctAnswersJson
            .replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val isCorrect = when (question.questionType) {
            "MCQ" -> {
                val userAns = selectedAnswers.firstOrNull()?.trim() ?: ""
                correctAnswers.any { it.equals(userAns, ignoreCase = true) }
            }
            "MSQ" -> {
                // MSQ requires all correct and no incorrect
                val setA = selectedAnswers.map { it.trim().uppercase() }.toSet()
                val setB = correctAnswers.map { it.trim().uppercase() }.toSet()
                setA == setB && setA.isNotEmpty()
            }
            "NAT" -> {
                // Numerical range / value check
                val userVal = selectedAnswers.firstOrNull()?.trim()?.toDoubleOrNull()
                if (userVal == null) false
                else {
                    correctAnswers.any {
                        val cVal = it.toDoubleOrNull()
                        cVal != null && kotlin.math.abs(cVal - userVal) < 0.05
                    }
                }
            }
            else -> false
        }

        val updated = question.copy(
            isAttempted = true,
            isCorrect = isCorrect,
            isMistake = !isCorrect,
            userSelectedAnswersJson = selectedAnswers.joinToString(separator = ",", prefix = "[", postfix = "]") { "\"$it\"" },
            timeSpentSeconds = question.timeSpentSeconds + timeSpentSeconds
        )
        dao.updateQuestion(updated)
        return isCorrect
    }

    // Mock Tests
    fun getMockTests(stream: String): Flow<List<MockTestEntity>> = dao.getMockTests(stream)

    suspend fun getMockTestById(testId: String): MockTestEntity? = dao.getMockTestById(testId)

    suspend fun submitMockTestResult(
        testId: String,
        score: Float,
        accuracyPercent: Float,
        correctCount: Int,
        wrongCount: Int,
        unattemptedCount: Int
    ) {
        val test = dao.getMockTestById(testId) ?: return
        val updated = test.copy(
            isCompleted = true,
            completedAtMillis = System.currentTimeMillis(),
            score = score,
            accuracyPercent = accuracyPercent,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount
        )
        dao.updateMockTest(updated)
    }

    // Study Tasks
    fun getAllStudyTasks(): Flow<List<StudyTaskEntity>> = dao.getAllStudyTasks()

    suspend fun addStudyTask(task: StudyTaskEntity): Long = dao.insertStudyTask(task)

    suspend fun updateStudyTask(task: StudyTaskEntity) = dao.updateStudyTask(task)

    suspend fun deleteStudyTask(task: StudyTaskEntity) = dao.deleteStudyTask(task)

    // Notes
    fun getAllNotes(): Flow<List<NoteEntity>> = dao.getAllNotes()

    suspend fun addNote(note: NoteEntity): Long = dao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    // Formulas
    fun getFormulas(stream: String): Flow<List<FormulaEntity>> = dao.getFormulas(stream)

    suspend fun toggleFormulaBookmark(formulaId: String, currentStatus: Boolean) {
        dao.setFormulaBookmarked(formulaId, !currentStatus)
    }

    // Exam Milestones
    fun getExamMilestones(): Flow<List<ExamMilestoneEntity>> = dao.getExamMilestones()

    suspend fun toggleMilestoneReminder(milestoneId: String, enabled: Boolean) {
        dao.setMilestoneReminder(milestoneId, enabled)
    }

    // User Stats
    fun getUserStats(): Flow<UserStatsEntity?> = dao.getUserStats()

    suspend fun updateUserStats(stats: UserStatsEntity) {
        dao.insertOrUpdateUserStats(stats)
    }

    suspend fun logStudyTime(minutes: Int) {
        val current = dao.getUserStats().firstOrNull() ?: InitialData.defaultUserStats
        val updated = current.copy(
            totalStudyMinutes = current.totalStudyMinutes + minutes
        )
        dao.insertOrUpdateUserStats(updated)
    }

    suspend fun resetAllProgress() {
        dao.resetTopicsProgress()
        dao.resetQuestionsProgress()
        dao.resetMockTests()
        val current = dao.getUserStats().firstOrNull() ?: InitialData.defaultUserStats
        dao.insertOrUpdateUserStats(
            current.copy(
                totalStudyMinutes = 0,
                currentStreakDays = 0
            )
        )
    }
}
