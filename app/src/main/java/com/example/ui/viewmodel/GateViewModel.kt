package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.RecommendationEngine
import com.example.data.local.AppDatabase
import com.example.data.model.AiRecommendation
import com.example.data.model.ExamMilestoneEntity
import com.example.data.model.FormulaEntity
import com.example.data.model.MockTestEntity
import com.example.data.model.NoteEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.data.model.UserStatsEntity
import com.example.data.notification.GateNotificationHelper
import com.example.data.repository.GateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CountdownTime(
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val totalSeconds: Long = 0
)

class GateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GateRepository
    private val recommendationEngine = RecommendationEngine()

    val targetExamTimeMillis = 1801886400000L // Feb 6, 2027 09:30 AM IST

    private val _selectedStream = MutableStateFlow("BOTH")
    val selectedStream: StateFlow<String> = _selectedStream.asStateFlow()

    private val _countdown = MutableStateFlow(CountdownTime())
    val countdown: StateFlow<CountdownTime> = _countdown.asStateFlow()

    private val _aiRecommendations = MutableStateFlow<List<AiRecommendation>>(emptyList())
    val aiRecommendations: StateFlow<List<AiRecommendation>> = _aiRecommendations.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = GateRepository(db.gateDao())
        GateNotificationHelper.initChannels(application)

        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeDatabaseIfEmpty()
        }

        // Live Countdown Ticker
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                val now = System.currentTimeMillis()
                val diff = (targetExamTimeMillis - now).coerceAtLeast(0)
                val totalSeconds = diff / 1000
                val days = totalSeconds / (24 * 3600)
                val hours = (totalSeconds % (24 * 3600)) / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                _countdown.value = CountdownTime(days, hours, minutes, seconds, totalSeconds)
                delay(1000)
            }
        }
    }

    val userStats: StateFlow<UserStatsEntity?> = repository.getUserStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val subjects: StateFlow<List<SubjectEntity>> = combine(
        _selectedStream,
        repository.getSubjects("BOTH")
    ) { stream, allSubjects ->
        if (stream == "BOTH") allSubjects else allSubjects.filter { it.stream == stream }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topics: StateFlow<List<TopicEntity>> = combine(
        _selectedStream,
        repository.getTopics("BOTH")
    ) { stream, allTopics ->
        if (stream == "BOTH") allTopics else allTopics.filter { it.stream == stream }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuestionEntity>> = combine(
        _selectedStream,
        repository.getAllQuestions("BOTH")
    ) { stream, allQuestions ->
        if (stream == "BOTH") allQuestions else allQuestions.filter { it.stream == stream }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedQuestions: StateFlow<List<QuestionEntity>> = repository.getBookmarkedQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mistakeQuestions: StateFlow<List<QuestionEntity>> = repository.getMistakeQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockTests: StateFlow<List<MockTestEntity>> = combine(
        _selectedStream,
        repository.getMockTests("BOTH")
    ) { stream, allTests ->
        if (stream == "BOTH") allTests else allTests.filter { it.stream == stream || it.stream == "BOTH" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTaskEntity>> = repository.getAllStudyTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val formulas: StateFlow<List<FormulaEntity>> = combine(
        _selectedStream,
        repository.getFormulas("BOTH")
    ) { stream, allFormulas ->
        if (stream == "BOTH") allFormulas else allFormulas.filter { it.stream == stream }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: StateFlow<List<ExamMilestoneEntity>> = repository.getExamMilestones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setStream(stream: String) {
        _selectedStream.value = stream
        viewModelScope.launch {
            userStats.value?.let { current ->
                repository.updateUserStats(current.copy(targetStream = stream))
            }
        }
        refreshAiRecommendations()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTopicCompletion(topicId: String, current: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setTopicCompleted(topicId, !current)
        }
    }

    fun incrementTopicRevision(topicId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.incrementTopicRevision(topicId)
        }
    }

    fun updateTopicDifficulty(topicId: String, difficulty: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTopicDifficulty(topicId, difficulty)
        }
    }

    fun submitQuestionAnswer(
        question: QuestionEntity,
        selectedAnswers: List<String>,
        timeSpent: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val isCorrect = repository.submitQuestionAnswer(question, selectedAnswers, timeSpent)
            launch(Dispatchers.Main) { onResult(isCorrect) }
        }
    }

    fun toggleQuestionBookmark(questionId: String, current: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleQuestionBookmark(questionId, current)
        }
    }

    fun submitMockTest(
        testId: String,
        score: Float,
        accuracyPercent: Float,
        correctCount: Int,
        wrongCount: Int,
        unattemptedCount: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitMockTestResult(
                testId,
                score,
                accuracyPercent,
                correctCount,
                wrongCount,
                unattemptedCount
            )
            refreshAiRecommendations()
        }
    }

    fun addStudyTask(
        subjectName: String,
        topicTitle: String,
        targetDateMillis: Long,
        durationMinutes: Int,
        priority: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStudyTask(
                StudyTaskEntity(
                    stream = _selectedStream.value,
                    subjectName = subjectName,
                    topicTitle = topicTitle,
                    targetDateMillis = targetDateMillis,
                    durationMinutes = durationMinutes,
                    priority = priority,
                    notes = notes
                )
            )
        }
    }

    fun toggleStudyTaskCompletion(task: StudyTaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val isNowDone = !task.isCompleted
            repository.updateStudyTask(task.copy(isCompleted = isNowDone))
            if (isNowDone) {
                repository.logStudyTime(task.durationMinutes)
            }
        }
    }

    fun deleteStudyTask(task: StudyTaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudyTask(task)
        }
    }

    fun addNote(subjectName: String, title: String, content: String, isPinned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNote(
                NoteEntity(
                    stream = _selectedStream.value,
                    subjectName = subjectName,
                    title = title,
                    content = content,
                    isPinned = isPinned,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun toggleFormulaBookmark(formulaId: String, current: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFormulaBookmark(formulaId, current)
        }
    }

    fun toggleMilestoneReminder(milestoneId: String, current: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleMilestoneReminder(milestoneId, !current)
        }
    }

    fun sendTestNotification(title: String, message: String) {
        GateNotificationHelper.sendTestExamNotification(getApplication(), title, message)
    }

    fun updateDailyGoal(minutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userStats.value?.let { current ->
                repository.updateUserStats(current.copy(dailyStudyGoalMinutes = minutes))
            }
        }
    }

    fun updateTheme(themeMode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userStats.value?.let { current ->
                repository.updateUserStats(current.copy(themeMode = themeMode))
            }
        }
    }

    fun updateNotificationSettings(
        enableDaily: Boolean,
        hour: Int,
        minute: Int,
        enableExamAlerts: Boolean,
        enableRevisionAlerts: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            userStats.value?.let { current ->
                repository.updateUserStats(
                    current.copy(
                        enableDailyReminder = enableDaily,
                        dailyReminderHour = hour,
                        dailyReminderMinute = minute,
                        enableExamAlerts = enableExamAlerts,
                        enableRevisionAlerts = enableRevisionAlerts
                    )
                )
            }
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetAllProgress()
            refreshAiRecommendations()
        }
    }

    fun refreshAiRecommendations() {
        viewModelScope.launch(Dispatchers.IO) {
            _isAiLoading.value = true
            val currentTopics = topics.value
            val currentQuestions = questions.value
            val currentMocks = mockTests.value
            val currentStream = _selectedStream.value

            val recs = recommendationEngine.getRecommendations(
                topics = currentTopics,
                questions = currentQuestions,
                mockTests = currentMocks,
                stream = currentStream
            )
            _aiRecommendations.value = recs
            _isAiLoading.value = false
        }
    }
}
