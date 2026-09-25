package com.example.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiRecommendationCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StreamSelectorChips
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSecondaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateTertiaryLight
import com.example.ui.theme.GateWarning
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: GateViewModel,
    onNavigateToAiRecs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val aiRecommendations by viewModel.aiRecommendations.collectAsStateWithLifecycle()

    val totalTopics = topics.size.coerceAtLeast(1)
    val completedTopics = topics.count { it.isCompleted }
    val prepPercentage = (completedTopics * 100) / totalTopics

    val attemptedQuestions = questions.count { it.isAttempted }
    val correctQuestions = questions.count { it.isCorrect }
    val accuracy = if (attemptedQuestions > 0) (correctQuestions * 100) / attemptedQuestions else 0

    val completedMocks = mockTests.filter { it.isCompleted }
    val avgScore = if (completedMocks.isNotEmpty()) completedMocks.map { it.score }.average() else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress & Analytics", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StreamSelectorChips(
                    selectedStream = selectedStream,
                    onSelectStream = { viewModel.setStream(it) }
                )
            }

            // Big Circular Progress Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("prep_progress_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Overall Syllabus Covered",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$completedTopics of $totalTopics core topics completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Target AIR: Top ${userStats?.targetRank ?: 100}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { prepPercentage / 100f },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 8.dp,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                text = "$prepPercentage%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Stat Cards Grid
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Study Hours",
                        value = "${(userStats?.totalStudyMinutes ?: 0) / 60}h ${(userStats?.totalStudyMinutes ?: 0) % 60}m",
                        subtitle = "Active preparation",
                        icon = Icons.Default.Schedule,
                        iconTint = GatePrimaryLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "PYQ Accuracy",
                        value = "$accuracy%",
                        subtitle = "$attemptedQuestions solved",
                        icon = Icons.Default.CheckCircle,
                        iconTint = GateSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Subject-wise Breakdown
            item {
                SectionHeader(
                    title = "Subject-Wise Progress",
                    subtitle = "Completion rate against GATE weightage"
                )
            }

            items(subjects, key = { it.id }) { subject ->
                val subTopics = topics.filter { it.subjectId == subject.id }
                val done = subTopics.count { it.isCompleted }
                val percent = if (subTopics.isNotEmpty()) (done * 100) / subTopics.size else 0

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { percent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Weightage: ~${subject.weightagePercent}% • $done of ${subTopics.size} topics",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Strong vs Weak Areas
            item {
                SectionHeader(
                    title = "Strengths & Weaknesses",
                    subtitle = "Evaluated by diagnostic performance"
                )

                val weakTopics = topics.filter { it.difficultyLevel == "WEAK" }.take(3)
                val strongTopics = topics.filter { it.difficultyLevel == "STRONG" }.take(3)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = GateError, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Weak Topics Needing Attention:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (weakTopics.isEmpty()) {
                            Text("None currently flagged as weak. Keep up the high standard!", style = MaterialTheme.typography.bodySmall)
                        } else {
                            weakTopics.forEach {
                                Text("• ${it.title} (${it.stream})", style = MaterialTheme.typography.bodySmall, color = GateError)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GateSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Strong Mastered Areas:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        strongTopics.forEach {
                            Text("• ${it.title} (${it.stream})", style = MaterialTheme.typography.bodySmall, color = GateSuccess)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
