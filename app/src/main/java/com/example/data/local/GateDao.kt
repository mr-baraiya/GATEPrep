package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface GateDao {

    // --- Subjects ---
    @Query("SELECT * FROM subjects WHERE stream = :stream OR :stream = 'BOTH' ORDER BY weightagePercent DESC")
    fun getSubjects(stream: String): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    // --- Topics ---
    @Query("SELECT * FROM topics WHERE stream = :stream OR :stream = 'BOTH'")
    fun getTopics(stream: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE subjectId = :subjectId")
    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Query("UPDATE topics SET isCompleted = :completed WHERE id = :topicId")
    suspend fun setTopicCompleted(topicId: String, completed: Boolean)

    @Query("UPDATE topics SET revisionCount = revisionCount + 1 WHERE id = :topicId")
    suspend fun incrementTopicRevision(topicId: String)

    @Query("UPDATE topics SET difficultyLevel = :difficulty WHERE id = :topicId")
    suspend fun updateTopicDifficulty(topicId: String, difficulty: String)

    // --- Questions (PYQs) ---
    @Query("SELECT * FROM questions WHERE (:stream = 'BOTH' OR stream = :stream) ORDER BY year DESC")
    fun getAllQuestions(stream: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE isBookmarked = 1")
    fun getBookmarkedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE isMistake = 1")
    fun getMistakeQuestions(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("UPDATE questions SET isBookmarked = :bookmarked WHERE id = :questionId")
    suspend fun setQuestionBookmarked(questionId: String, bookmarked: Boolean)

    // --- Mock Tests ---
    @Query("SELECT * FROM mock_tests WHERE (:stream = 'BOTH' OR stream = :stream) ORDER BY id ASC")
    fun getMockTests(stream: String): Flow<List<MockTestEntity>>

    @Query("SELECT * FROM mock_tests WHERE id = :testId")
    suspend fun getMockTestById(testId: String): MockTestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockTests(tests: List<MockTestEntity>)

    @Update
    suspend fun updateMockTest(test: MockTestEntity)

    // --- Study Planner Tasks ---
    @Query("SELECT * FROM study_tasks ORDER BY targetDateMillis ASC")
    fun getAllStudyTasks(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyTask(task: StudyTaskEntity): Long

    @Update
    suspend fun updateStudyTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteStudyTask(task: StudyTaskEntity)

    // --- Notes ---
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAtMillis DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // --- Formulas ---
    @Query("SELECT * FROM formulas WHERE (:stream = 'BOTH' OR stream = :stream)")
    fun getFormulas(stream: String): Flow<List<FormulaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormulas(formulas: List<FormulaEntity>)

    @Query("UPDATE formulas SET isBookmarked = :bookmarked WHERE id = :formulaId")
    suspend fun setFormulaBookmarked(formulaId: String, bookmarked: Boolean)

    // --- Exam Milestones ---
    @Query("SELECT * FROM exam_milestones ORDER BY eventDateMillis ASC")
    fun getExamMilestones(): Flow<List<ExamMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<ExamMilestoneEntity>)

    @Query("UPDATE exam_milestones SET isReminderEnabled = :enabled WHERE id = :milestoneId")
    suspend fun setMilestoneReminder(milestoneId: String, enabled: Boolean)

    // --- User Stats & Preferences ---
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    // Reset All Progress
    @Query("UPDATE topics SET isCompleted = 0, studyMinutesLogged = 0, revisionCount = 0")
    suspend fun resetTopicsProgress()

    @Query("UPDATE questions SET isAttempted = 0, isCorrect = 0, isMistake = 0, userSelectedAnswersJson = ''")
    suspend fun resetQuestionsProgress()

    @Query("UPDATE mock_tests SET isCompleted = 0, score = 0, accuracyPercent = 0, correctCount = 0, wrongCount = 0, unattemptedCount = 0")
    suspend fun resetMockTests()
}
