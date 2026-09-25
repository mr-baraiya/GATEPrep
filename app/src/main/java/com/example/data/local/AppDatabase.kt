package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ExamMilestoneEntity
import com.example.data.model.FormulaEntity
import com.example.data.model.MockTestEntity
import com.example.data.model.NoteEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.data.model.UserStatsEntity

@Database(
    entities = [
        SubjectEntity::class,
        TopicEntity::class,
        QuestionEntity::class,
        MockTestEntity::class,
        StudyTaskEntity::class,
        NoteEntity::class,
        FormulaEntity::class,
        ExamMilestoneEntity::class,
        UserStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gateDao(): GateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gate_2027_prep.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
